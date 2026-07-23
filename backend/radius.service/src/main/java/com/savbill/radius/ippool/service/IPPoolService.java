package com.savbill.radius.ippool.service;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.ippool.domain.IPPool;
import com.savbill.radius.ippool.model.IPPoolDTO;

import java.util.*;

public interface IPPoolService {
    boolean duplicateVerifyAtSave(String name, List<Long> mvnoId)throws Exception;
    IPPoolDTO saveIPPool(IPPoolDTO ipPoolDTO, Long mvnoId) throws Exception;
    List<IPPoolDTO> getAllDefaultPool();
    IPPool findByPoolId(Long vlanId, Long mvnoId, boolean isDelete);
    void deleteById(Long i, Long mvnoId) throws Exception;
    PageableResponse<IPPool> getListByPageAndSize(Long mvnoId, PaginationDTO paginationDTO) ;
    List<IPPool> findAll(Long mvnoId);
    List<IPPool> findAvailableIPPools(Long mvnoId);

}
