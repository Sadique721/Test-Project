package com.savbill.radius.services;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.entity.VlanAudit;
import com.savbill.radius.utils.PaginationRequestDTO;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface VlanAuditService {
    public PageableResponse findVlanAuditList(Integer mvnoId, PaginationDTO paginationDTO);
    public void findVlanAuditList(VLANManagement vlanAudit,String username,String action);
    public void saveVlanAudit(VLANManagement vlanAudit, String username, String action, Integer staffId,String details, String fileName);

    public void saveVlanAuditForUpdate(Map<VLANManagement, String> vlanDifferenceMap, String username, String action, Integer staffId, String fileName);

    public PageableResponse findVlanAuditListByVlanId(Long vlanId, PaginationDTO paginationDTO);

    Page<VlanAudit> filterAudit(Integer mvnoId, PaginationRequestDTO paginationDTO, HttpServletRequest request);

    public void saveVlanAudit(String entityName ,String username, String action , Integer staffId,String fileName,String details, String remark);
}
