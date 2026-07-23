package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.service;

import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPPool;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper.IPPoolDtlsMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper.IPPoolMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPPoolDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPPoolDtlsDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPPoolDtlsRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPPoolRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.utils.SubnetUtils;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPPoolService extends ExBaseAbstractService<IPPoolDTO, IPPool, Long> {

    @Autowired
    private IPPoolRepository ipPoolRepository;

    @Autowired
    private IPPoolDtlsRepository ipPoolDtlsRepository;

    @Autowired
    private IPPoolDtlsMapper ipPoolDtlsMapper;

    public IPPoolService(IPPoolRepository repository, IPPoolMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "pool_id");
        sortColMap.put("name", "pool_name");
        sortColMap.put("type", "pool_type");
        sortColMap.put("category", "pool_category");
    }

//    @Autowired
//    private CustomRepository<CustIPDetailsDTO> customRepository;

    @Override
    public String getModuleNameForLog() {
        return "[IPPoolService]";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getPoolByNameOrTypeOrCategory(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                IPPoolDTO.class.getDeclaredField("poolId"),
                IPPoolDTO.class.getDeclaredField("poolName"),
                IPPoolDTO.class.getDeclaredField("poolCategory"),
                IPPoolDTO.class.getDeclaredField("poolType"),
                IPPoolDTO.class.getDeclaredField("status"),
        };
    }

    public GenericDataDTO getPoolByNameOrTypeOrCategory(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getDeviceByNameOrTypeOrAreaName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<IPPool> ipPoolList;
            if(getMvnoIdFromCurrentStaff() == 1)
                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest);
            else
                ipPoolList = ipPoolRepository.findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != ipPoolList && 0 < ipPoolList.getSize()) {
                makeGenericResponse(genericDataDTO, ipPoolList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

//    public List<CustIPDetailsDTO> getCustIpDetails(Long custId) {
//        List<CustIPDetailsDTO> custIPList = customRepository.getResultOfQuery(IpExpiryScript.getIpDetails(custId), CustIPDetailsDTO.class);
//        if (null != custIPList && 0 < custIPList.size()) {
//            return custIPList;
//        }
//        return new ArrayList<>();
//    }
    public IPPoolDTO saveIPPool(IPPoolDTO ipPoolDTO) throws Exception {
        try {
            if (ipPoolDTO.getIsStaticIpPool()) {
                SubnetUtils subnetUtils = new SubnetUtils(ipPoolDTO.getIpRange());
                ipPoolDTO.setNetMask(subnetUtils.getNetmask());
                ipPoolDTO.setNetworkIp(subnetUtils.getNetworkIp());
                ipPoolDTO.setBroadcastIp(subnetUtils.getBroadcastAddress());
                ipPoolDTO.setFirstHost(subnetUtils.getFirstIp());
                ipPoolDTO.setLastHost(subnetUtils.getLastIp());
                ipPoolDTO.setTotalHost(subnetUtils.getNumberOfHosts());
    //            ipPoolDTO.setIpRange(subnetUtils.getHostAddressRange());
                ipPoolDTO = saveEntity(ipPoolDTO);
                LoggedInUser user = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                List<IPPoolDtlsDTO> ipPoolDtlsDTOList = new ArrayList<>();
                for (String ipAddress : subnetUtils.getAvailableIPs(subnetUtils.getNumberOfHosts())) {
                    IPPoolDtlsDTO ipPoolDtlsDTO = new IPPoolDtlsDTO();
                    ipPoolDtlsDTO.setPoolId(ipPoolDTO.getPoolId());
                    ipPoolDtlsDTO.setIpAddress(ipAddress);
                    ipPoolDtlsDTO.setStatus("Free");
                    ipPoolDtlsDTO.setIsDelete(false);
                    ipPoolDtlsDTOList.add(ipPoolDtlsDTO);
                    ipPoolDtlsDTO.setCreatedById(user.getUserId());
                    ipPoolDtlsDTO.setCreatedByName(user.getFullName());
                    ipPoolDtlsDTO.setLastModifiedById(user.getUserId());
                    ipPoolDtlsDTO.setLastModifiedByName(user.getFullName());
                    ipPoolDtlsDTO.setCreatedate(LocalDateTime.now());
                    ipPoolDtlsDTO.setUpdatedate(LocalDateTime.now());
                }
                Runnable ippoolThread = new IpPoolThread(ipPoolDtlsDTOList, ipPoolDtlsMapper, ipPoolDtlsRepository);
                Thread ippool = new Thread(ippoolThread);
                ippool.start();
            } else {
                SubnetUtils subnetUtils = new SubnetUtils(ipPoolDTO.getIpRange());
                ipPoolDTO = saveEntity(ipPoolDTO);
            }
            return ipPoolDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    @Override
    public IPPoolDTO saveEntity(IPPoolDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            entity.setMvnoId(getMvnoIdFromCurrentStaff());
            if (entity.getDefaultPoolFlag()) {
                List<IPPoolDTO> defaultPoolList = getAllDefaultPool();
                if (null != defaultPoolList && 0 < defaultPoolList.size()) {
                    for (IPPoolDTO ipPoolDTO : defaultPoolList) {
                        ipPoolDTO.setDefaultPoolFlag(false);
                        updateEntity(ipPoolDTO);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(),ex);
            throw ex;
        }
        return super.saveEntity(entity);
    }

    @Override
    public boolean duplicateVerifyAtSave(String name)throws Exception
    {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
            else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        try {
            boolean flag = false;
            if (name != null) {
                name = name.trim();
                Integer count;
                if(getMvnoIdFromCurrentStaff() == 1) count = ipPoolRepository.duplicateVerifyAtSave(name);
                else count = ipPoolRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                if (count >= 1) {
                    Integer countEdit;
                    if(getMvnoIdFromCurrentStaff() == 1) countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id);
                    else countEdit = ipPoolRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    if (countEdit == 1) {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=ipPoolRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public List<IPPoolDTO> getAllDefaultPool() {
        return ipPoolRepository.findAllByDefaultPoolFlagIsTrueAndIsDeleteIsFalse().stream().map(data -> getMapper()
                .domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<IPPool> paginationList = null;
            PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() == 1)
                paginationList = ipPoolRepository.findAll(pageRequest);
            else
                paginationList = ipPoolRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != paginationList && 0 < paginationList.getContent().size()) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
            if (!paginationList.isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }
}
