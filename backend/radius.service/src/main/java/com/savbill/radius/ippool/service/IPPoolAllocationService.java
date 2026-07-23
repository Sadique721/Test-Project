package com.savbill.radius.ippool.service;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.model.IPPoolAllocationDtlsDTO;

import java.util.List;

public interface IPPoolAllocationService {
    IPPoolAllocationDtls findByPoolId(Long poolId, Long mvnoId);
    PageableResponse<IPPoolAllocationDtlsDTO> getListByPageAndSize(PaginationDTO paginationDTO, Long poolId) ;
    List<IPPoolAllocationDtls> findAll(Long mvnoId);

    IPPoolAllocationDtls findByIPAndPoolId(Long poolId, String ipAddress);
}
