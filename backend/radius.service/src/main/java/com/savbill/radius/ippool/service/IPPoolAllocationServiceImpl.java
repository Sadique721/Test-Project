package com.savbill.radius.ippool.service;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.domain.QIPPoolAllocationDtls;
import com.savbill.radius.ippool.model.IPPoolAllocationDtlsDTO;
import com.savbill.radius.ippool.repository.IPPoolAllocationRepository;
import com.savbill.radius.repository.IPPoolMappingRepository;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IPPoolAllocationServiceImpl implements IPPoolAllocationService {
    private static final Logger log = LoggerFactory.getLogger(IPPoolAllocationServiceImpl.class);
    @Autowired
    private IPPoolAllocationRepository ipPoolAllocationRepository;
    @Autowired
    private IPPoolMappingRepository ipPoolMappingRepository;

    @Override
    public IPPoolAllocationDtls findByPoolId(Long poolId, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(poolId))
                throw new IllegalArgumentException(RadiusConstants.BASIC_NUMERIC_MSG + "Please enter valid pool id.");
            QIPPoolAllocationDtls iPPoolAllocationDtls = QIPPoolAllocationDtls.iPPoolAllocationDtls;
            BooleanExpression boolExp = iPPoolAllocationDtls.isNotNull();

            Optional<IPPoolAllocationDtls> ipPool = ipPoolAllocationRepository.findOne(boolExp);
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
    public PageableResponse<IPPoolAllocationDtlsDTO> getListByPageAndSize(PaginationDTO paginationDTO, Long poolId) {
        Page<IPPoolAllocationDtlsDTO> ipPoolPage = null;
        try {
            QIPPoolAllocationDtls qipPoolAllocationDtls = QIPPoolAllocationDtls.iPPoolAllocationDtls;

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }

            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));

            ipPoolPage = ipPoolAllocationRepository.findAll(poolId, pageable);

            PageableResponse<IPPoolAllocationDtlsDTO> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<>(ipPoolPage.getContent(), pageable, ipPoolPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public List<IPPoolAllocationDtls> findAll(Long poolId) {
        QIPPoolAllocationDtls qipPool = QIPPoolAllocationDtls.iPPoolAllocationDtls;
        BooleanExpression booleanExpression = qipPool.isNotNull();
        booleanExpression = booleanExpression.and(qipPool.poolId.eq(poolId));

        return (List<IPPoolAllocationDtls>) ipPoolAllocationRepository.findAll(booleanExpression);
    }

    @Override
    public IPPoolAllocationDtls findByIPAndPoolId(Long poolId, String ipAddress) {
        QIPPoolAllocationDtls qipPool = QIPPoolAllocationDtls.iPPoolAllocationDtls;
        BooleanExpression booleanExpression = qipPool.isNotNull();
        booleanExpression = booleanExpression.and(qipPool.poolId.eq(poolId)).and(qipPool.ipAddress.equalsIgnoreCase(ipAddress));

        Optional<IPPoolAllocationDtls> ipPool;
        ipPool = ipPoolAllocationRepository.findOne(booleanExpression);

        if (!ipPool.isPresent()) {
            throw new IllegalArgumentException(
                    "No record found with Pool id " + poolId + " . Please enter valid pool id.");
        }
        return ipPool.get();
    }

}
