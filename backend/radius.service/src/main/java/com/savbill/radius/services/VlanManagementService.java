package com.savbill.radius.services;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.helper.BulkVlanResponseDto;
import com.savbill.radius.helper.VlanManagementDto;
import com.savbill.radius.helper.VlanSearch;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VlanManagementService {
    VLANManagement save(VlanManagementDto vlanManagementDto, Integer mvnoId);

    List<VLANManagement> findAllVlans(Integer mvnoId);

    VLANManagement findVlanById(Long vlanId, Integer mvnoId);

    VLANManagement updateVlanManagement(VlanManagementDto vlanManagementDto, Integer mvnoId);

    void deleteByVlanId(Long vlanId, Integer mvnoId);

    PageableResponse<VLANManagement> findVlansList(Integer mvnoId, PaginationDTO paginationDTO);

    PageableResponse<VLANManagement> findAllVlansBySearch(Integer mvnoId, VlanSearch vlanSearch, PaginationDTO paginationDTO);

    Map<String, Object> addBulkVlan(MultipartFile file, Integer mvnoId, Integer staffId, String username);

    int delete(List<Long> ids, Integer mvnoId);

//    Integer updateBulkVlan(MultipartFile file, Integer mvnoId);
    Integer updateBulkVlan(MultipartFile file, Integer mvnoId,Integer staffId,String userName);
    int delete(List<Long> ids, Integer mvnoId,Integer staffId,String userName);

    List<BulkVlanResponseDto> exportVlan(Integer mvnoId);
}
