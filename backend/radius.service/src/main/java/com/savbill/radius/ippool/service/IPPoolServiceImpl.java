package com.savbill.radius.ippool.service;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPool;
import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.domain.QIPPool;
import com.savbill.radius.ippool.model.IPPoolDTO;
import com.savbill.radius.ippool.repository.IPPoolAllocationRepository;
import com.savbill.radius.ippool.repository.IPPoolRepository;
import com.savbill.radius.ippool.utils.SubnetUtils;
import com.savbill.radius.repository.IPPoolMappingRepository;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IPPoolServiceImpl implements IPPoolService {

    @Autowired
    private IPPoolRepository ipPoolRepository;

    @Autowired
    private IPPoolAllocationRepository ipPoolAllocationRepository;
    @Autowired
    private IPPoolMappingRepository ipPoolMappingRepository;

    private static final Logger log = LoggerFactory.getLogger(IPPoolServiceImpl.class);

    @Transactional
    public IPPoolDTO saveIPPool(IPPoolDTO ipPoolDTO, Long mvnoId) throws Exception {

        if (duplicateVerifyAtSave(ipPoolDTO.getPoolName(), getMvnoList(mvnoId))) {
            throw new IllegalArgumentException("IP Pool is already exists with name: " + ipPoolDTO.getPoolName());
        }

        if (duplicateIPRange(ipPoolDTO.getIpRange(), getMvnoList(mvnoId))) {
            throw new IllegalArgumentException("IP-range is already existed in IP-Pool: " + ipPoolDTO.getIpRange());
        }

        SubnetUtils subnetUtils = new SubnetUtils(ipPoolDTO.getIpRange());
        ipPoolDTO.setNetMask(subnetUtils.getNetmask());
        ipPoolDTO.setNetworkIp(subnetUtils.getNetworkIp());
        ipPoolDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
        ipPoolDTO.setFirstHost(subnetUtils.getFirstIp());
        ipPoolDTO.setLastHost(subnetUtils.getLastIp());
        ipPoolDTO.setTotalHost(subnetUtils.getNumberOfHosts());
//           ipPoolDTO.setIpRange(subnetUtils.getHostAddressRange());

        String overlappingRange = isOverlappingRange(subnetUtils.getFirstIp(), subnetUtils.getLastIp());
        if (overlappingRange != null && !overlappingRange.isEmpty()) {
            log.debug(String.format("IP Overlapping for given ip range: %s", ipPoolDTO.getIpRange()));
            throw new IllegalArgumentException(" IP Range Overlapping with IP-Pool name: " + overlappingRange);
        }

        ipPoolDTO = saveEntity(ipPoolDTO, mvnoId);
        List<IPPoolAllocationDtls> ipPoolDtlsDTOList = new ArrayList<>();
        for (String ipAddress : subnetUtils.getAvailableIPs(subnetUtils.getNumberOfHosts())) {
            IPPoolAllocationDtls ipPoolDtlsDTO = new IPPoolAllocationDtls();
            ipPoolDtlsDTO.setPoolId(ipPoolDTO.getPoolId());
            ipPoolDtlsDTO.setIpAddress(ipAddress);
            ipPoolDtlsDTO.setStatus("Free");
            ipPoolDtlsDTO.setIsDelete(false);
            ipPoolDtlsDTOList.add(ipPoolDtlsDTO);
            ipPoolDtlsDTO.setCreatedOn(new Timestamp(new Date().getTime()));
            ipPoolDtlsDTO.setLastModifiedOn(new Timestamp(new Date().getTime()));
        }
        Runnable ippoolThread = new IpPoolThread(ipPoolDtlsDTOList, ipPoolAllocationRepository);
        Thread ippool = new Thread(ippoolThread);
        ippool.start();
        return ipPoolDTO;
    }

    private String isOverlappingRange(String firstIp, String lastIp) throws UnknownHostException {
        String poolName = null;
        long newStart = ipToLong(firstIp);
        long newEnd = ipToLong(lastIp);

        // String query = "SELECT COUNT(*) FROM ip_pools WHERE (start_ip <= ? AND end_ip >= ?)";
        // Use your database query logic to execute the query with newStart and newEnd values
        // If the result count is greater than 0, there is an overlap

        List<IPPool> l = ipPoolRepository.checkForIPOverLapping(newStart, newEnd);

        if (l != null && l.size() > 0) {
            poolName = l.get(0).getPoolName();
        }
        return poolName;
    }

    public static long ipToLong(String ipAddress) throws UnknownHostException {
        InetAddress inet = InetAddress.getByName(ipAddress);
        byte[] bytes = inet.getAddress();
        long result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }


    private boolean duplicateIPRange(String ipRange, List<Long> mvnoList) {
        boolean flag = false;
        Integer count = ipPoolRepository.duplicateIPRange(ipRange, mvnoList);
        if (count != null && count > 0) {
            flag = true;
        }
        return flag;
    }

    @Transactional
    public IPPoolDTO updateIPPool(IPPoolDTO ipPoolDTO, Long mvnoId) throws Exception {

        List<String> ipAddresses = ipPoolMappingRepository.findClientIPByIpPoolId(ipPoolDTO.getPoolId());

        if (!ipAddresses.isEmpty()) {
            throw new IllegalArgumentException("IP Pool can not be update as it configured with client: " + ipAddresses);
        }

        IPPool oldVlan = findByPoolId(ipPoolDTO.getPoolId(), mvnoId, false);

        if (!oldVlan.getPoolName().equalsIgnoreCase(ipPoolDTO.getPoolName())) {
            if (duplicateVerifyAtSave(ipPoolDTO.getPoolName(), getMvnoList(mvnoId))) {
                throw new IllegalArgumentException("IP Pool is already exists with name: " + ipPoolDTO.getPoolName());
            }
        }

        if (ipAddresses.isEmpty()) {
            ipPoolAllocationRepository.deleteByPoolId(ipPoolDTO.getPoolId());

            SubnetUtils subnetUtils = new SubnetUtils(ipPoolDTO.getIpRange());
            ipPoolDTO.setNetMask(subnetUtils.getNetmask());
            ipPoolDTO.setNetworkIp(subnetUtils.getNetworkIp());
            ipPoolDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
            ipPoolDTO.setFirstHost(subnetUtils.getFirstIp());
            ipPoolDTO.setLastHost(subnetUtils.getLastIp());
            ipPoolDTO.setTotalHost(subnetUtils.getNumberOfHosts());
//            ipPoolDTO.setIpRange(subnetUtils.getHostAddressRange());
            ipPoolDTO = saveEntity(ipPoolDTO, mvnoId);
            List<IPPoolAllocationDtls> ipPoolDtlsDTOList = new ArrayList<>();
            for (String ipAddress : subnetUtils.getAvailableIPs(subnetUtils.getNumberOfHosts())) {
                IPPoolAllocationDtls ipPoolDtlsDTO = new IPPoolAllocationDtls();
                ipPoolDtlsDTO.setPoolId(ipPoolDTO.getPoolId());
                ipPoolDtlsDTO.setIpAddress(ipAddress);
                ipPoolDtlsDTO.setStatus("Free");
                ipPoolDtlsDTO.setIsDelete(false);
                ipPoolDtlsDTOList.add(ipPoolDtlsDTO);
                ipPoolDtlsDTO.setCreatedOn(new Timestamp(new Date().getTime()));
                ipPoolDtlsDTO.setLastModifiedOn(new Timestamp(new Date().getTime()));
            }
            Runnable ippoolThread = new IpPoolThread(ipPoolDtlsDTOList, ipPoolAllocationRepository);
            Thread ippool = new Thread(ippoolThread);
            ippool.start();

            log.debug(String.format("Ip Pool was not in use, so recreated Pool for : %s", ipPoolDTO.getPoolName()));
        }
        return ipPoolDTO;
    }

    public IPPoolDTO saveEntity(IPPoolDTO entity, Long mvnoId) throws Exception {
        try {
            entity.setMvnoId(mvnoId);
            return convertDomainTODto(ipPoolRepository.save(convertDTOToDomain(entity)));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private IPPoolDTO convertDomainTODto(IPPool domain) {
        IPPoolDTO iPPoolDTO = new IPPoolDTO();
        iPPoolDTO.setCreatedOn(domain.getCreatedOn());
        iPPoolDTO.setLastModifiedOn(domain.getLastModifiedOn());
        iPPoolDTO.setUsageCategory(domain.getUsageCategory());
        iPPoolDTO.setPoolId(domain.getPoolId());
        iPPoolDTO.setPoolName(domain.getPoolName());
        iPPoolDTO.setIpRange(domain.getIpRange());
        iPPoolDTO.setNetMask(domain.getNetMask());
        iPPoolDTO.setNetworkIp(domain.getNetworkIp());
        iPPoolDTO.setBroadcastIp(domain.getBroadcastIp());
        iPPoolDTO.setFirstHost(domain.getFirstHost());
        iPPoolDTO.setLastHost(domain.getLastHost());
        iPPoolDTO.setTotalHost(domain.getTotalHost());
        iPPoolDTO.setIsDelete(domain.getIsDelete());
        iPPoolDTO.setStatus(domain.getStatus());
        iPPoolDTO.setRemark(domain.getRemark());
        iPPoolDTO.setMvnoId(domain.getMvnoId());

        return iPPoolDTO;
    }

    private IPPool convertDTOToDomain(IPPoolDTO dtoData) {
        IPPool iPPool = new IPPool();

        iPPool.setCreatedOn(new Timestamp(new Date().getTime()));
        iPPool.setLastModifiedOn(new Timestamp(new Date().getTime()));
        iPPool.setUsageCategory(dtoData.getUsageCategory());
        iPPool.setPoolId(dtoData.getPoolId());
        iPPool.setPoolName(dtoData.getPoolName());
        iPPool.setIpRange(dtoData.getIpRange());
        iPPool.setNetMask(dtoData.getNetMask());
        iPPool.setNetworkIp(dtoData.getNetworkIp());
        iPPool.setBroadcastIp(dtoData.getBroadcastIp());
        iPPool.setFirstHost(dtoData.getFirstHost());
        iPPool.setLastHost(dtoData.getLastHost());
        iPPool.setTotalHost(dtoData.getTotalHost());
        iPPool.setIsDelete(dtoData.getIsDelete());
        iPPool.setStatus(dtoData.getStatus());
        iPPool.setRemark(dtoData.getRemark());
        iPPool.setMvnoId(dtoData.getMvnoId());

        return iPPool;
    }

    public boolean duplicateVerifyAtSave(String name, List<Long> mvnoIds) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            count = ipPoolRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count != 0) {
                flag = true;
            }
        }
        return flag;
    }

    public List<IPPoolDTO> getAllDefaultPool() {
        return ipPoolRepository.findAllByIsDeleteIsFalse().stream().map(this::convertDomainTODto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long ipPoolId, Long mvnoId) throws Exception {
        findByPoolId(ipPoolId, mvnoId, true);
        List<String> ipAddresses = ipPoolMappingRepository.findClientIPByIpPoolId(ipPoolId);

        if (!ipAddresses.isEmpty()) {
            throw new IllegalArgumentException("IP Pool can not be deleted as it configured with client: " + ipAddresses);
        }

        ipPoolAllocationRepository.deleteByPoolId(ipPoolId);
        ipPoolRepository.deleteById(ipPoolId);
    }

    public IPPool findByPoolId(Long poolId, Long mvnoId, boolean isDelete) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(poolId))
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid pool id.");
            QIPPool qipPool = QIPPool.iPPool;
            BooleanExpression boolExp = qipPool.isNotNull();

            if (isDelete) {
                boolExp = boolExp.and(qipPool.isDelete.eq(true));
                boolExp = boolExp.and(qipPool.mvnoId.in(mvnoId));
            } else {
                if (mvnoId == null || mvnoId != 1)
                    boolExp = boolExp.and(qipPool.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(Math.toIntExact(mvnoId)), 1));
            }
            boolExp = boolExp.and(qipPool.poolId.eq(poolId));

            Optional<IPPool> ipPool = ipPoolRepository.findOne(boolExp);
            if (!ipPool.isPresent()) {
                throw new IllegalArgumentException(
                        "No record found with Pool id " + poolId + " . Please enter valid pool id.");
            }

            return ipPool.get();

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public PageableResponse<IPPool> getListByPageAndSize(Long mvnoId, PaginationDTO paginationDTO) {
        Page<IPPool> ipPoolPage = null;
        QIPPool qvlanManagement = QIPPool.iPPool;
        BooleanExpression exp = qvlanManagement.isNotNull();
        if (paginationDTO.getPage() > 0) {
            paginationDTO.setPage(paginationDTO.getPage() - 1);
        }
        Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
        try {
            if (mvnoId != null && mvnoId == 1)
                ipPoolPage = ipPoolRepository.findAll(exp, pageable);
            else {
                exp = exp.and(qvlanManagement.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(Math.toIntExact(mvnoId)), 1));
                ipPoolPage = ipPoolRepository.findAll(exp, pageable);
            }
            PageableResponse<IPPool> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<>(ipPoolPage.getContent(), pageable, ipPoolPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<IPPool> findAll(Long mvnoId) {
        try {
            QIPPool qipPool = QIPPool.iPPool;
            BooleanExpression exp = qipPool.isNotNull();
            if (mvnoId != null && mvnoId == 1)
                return ipPoolRepository.findAll();
            else {
                exp = exp.and(qipPool.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(Math.toIntExact(mvnoId)), 1));
                return (List<IPPool>) ipPoolRepository.findAll(exp);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<IPPool> findAvailableIPPools(Long mvnoId) {
        List<IPPool> ipPools = null;
        try {
            QIPPool qipPool = QIPPool.iPPool;
            BooleanExpression exp = qipPool.isNotNull();
            if (mvnoId == 1) {
                ipPools = ipPoolRepository.getAvailableIPPoolIds(Collections.singletonList(mvnoId));
            } else {
                ipPools = ipPoolRepository.getAvailableIPPoolIds(Arrays.asList(1l, mvnoId));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return ipPools;
    }

    List<Long> getMvnoList(Long mvnoId) {
        List<Long> mvnoList = null;
        if (mvnoId == null) {
            Arrays.asList(1l, 2l);
        } else if (mvnoId == 1) {
            mvnoList = Collections.singletonList(mvnoId);
        } else {
            mvnoList = Arrays.asList(1l, mvnoId);
        }
        return mvnoList;
    }

}
