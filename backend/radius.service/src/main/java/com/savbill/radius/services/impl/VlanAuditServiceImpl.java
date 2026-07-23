package com.savbill.radius.services.impl;

import com.savbill.radius.dto.PageableResponse;
import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.QVLANManagement;
import com.savbill.radius.entity.QVlanAudit;
import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.entity.VlanAudit;
import com.savbill.radius.repository.VlanAuditRepocitory;
import com.savbill.radius.repository.VlanManagementRepository;
import com.savbill.radius.services.VlanAuditService;
import com.savbill.radius.utils.PaginationRequestDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class VlanAuditServiceImpl implements VlanAuditService {
    @Autowired
    VlanAuditRepocitory vlanAuditRepocitory;
    @Autowired
    VlanManagementRepository vlanManagementRepository;
    @Override
    public PageableResponse findVlanAuditList(Integer mvnoId, PaginationDTO paginationDTO) {
        try {
            QVlanAudit qVlanAudit = QVlanAudit.vlanAudit;
            BooleanExpression exp = qVlanAudit.isNotNull();
            Page<VlanAudit> vlanManagementPage = null;

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
                vlanManagementPage = vlanAuditRepocitory.findAll(exp, pageable);
            PageableResponse<VlanAudit> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<VlanAudit>(vlanManagementPage.getContent(), pageable, vlanManagementPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void findVlanAuditList(VLANManagement vlan,String username,String action) {

    }

    @Override
    public void saveVlanAudit(VLANManagement vlan, String username, String action, Integer staffId, String details, String fileName) {
        try {
            if(Objects.isNull(vlan.getVlanId())){
             List<VLANManagement> vlanManagements= vlanManagementRepository.findVlanNameByVlanNameIn(Collections.singleton(vlan.getVlanName()));
               if(vlanManagements.size()>0) {
                   vlan.setVlanId(vlanManagements.get(0).getVlanId());
               }else{
                   vlan.setVlanId(vlanManagementRepository.findLastId().get() +1);
               }
            }
            VlanAudit vlanAudit=new VlanAudit(vlan,username,action,staffId,details, fileName);
            vlanAuditRepocitory.save(vlanAudit);
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void saveVlanAuditForUpdate(Map<VLANManagement, String> vlanDifferenceMap, String username, String action, Integer staffId, String fileName) {
        try {
            for (Map.Entry<VLANManagement, String> set :
                    vlanDifferenceMap.entrySet()) {
                if(Objects.isNull(set.getKey().getVlanId())){
                    List<VLANManagement> vlanManagements= vlanManagementRepository.findVlanNameByVlanNameIn(Collections.singleton(set.getKey().getVlanName()));
                    if(vlanManagements.size()>0) {
                        set.getKey().setVlanId(vlanManagements.get(0).getVlanId());
                    }else{
                        set.getKey().setVlanId(vlanManagementRepository.findLastId().get() +1);
                    }
                }
                VlanAudit vlanAudit=new VlanAudit(set.getKey(),username,action,staffId,set.getValue(), fileName);
                vlanAuditRepocitory.save(vlanAudit);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public PageableResponse findVlanAuditListByVlanId(Long vlanId, PaginationDTO paginationDTO) {
        try {
            QVlanAudit qVlanAudit = QVlanAudit.vlanAudit;
            BooleanExpression exp = qVlanAudit.isNotNull().and(qVlanAudit.entityId.eq(vlanId));
            Page<VlanAudit> vlanManagementPage = null;

            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "id"));
                vlanManagementPage = vlanAuditRepocitory.findAll(exp, pageable);

            PageableResponse<VlanAudit> pageableResponse = new PageableResponse<>();
            return pageableResponse.convert(new PageImpl<VlanAudit>(vlanManagementPage.getContent(), pageable, vlanManagementPage.getTotalElements()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<VlanAudit> filterAudit(Integer mvnoId, PaginationRequestDTO requestDTO, HttpServletRequest request) {
        try {
            QVlanAudit qVlanAudit = QVlanAudit.vlanAudit;
            BooleanExpression exp = qVlanAudit.isNotNull();
            Page<VlanAudit> vlanManagementPage = null;
            if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("staffName")){
                exp = exp.and(qVlanAudit.actionByName.equalsIgnoreCase(requestDTO.getFilters().get(0).getFilterValue()));
            }
                if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("action")){
                exp = exp.and(qVlanAudit.action.equalsIgnoreCase(requestDTO.getFilters().get(0).getFilterValue()));
            }
            Pageable pageable = PageRequest.of(requestDTO.getPage()-1, requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
            vlanManagementPage = vlanAuditRepocitory.findAll(exp, pageable);

            return vlanManagementPage;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

//        return null;
    }
    @Override
    public void saveVlanAudit(String entityName , String username, String action , Integer staffId,String fileName,String details, String remark ){
        VlanAudit vlanAudit=new VlanAudit(entityName,username,action,staffId,fileName,details, remark);
        vlanAuditRepocitory.save(vlanAudit);
    }
}
