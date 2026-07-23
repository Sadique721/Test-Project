package com.savbill.ticketmanagement.core.modules.workflowaudit.service;

import com.savbill.ticketmanagement.core.constants.ClientServiceConstant;
import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;
import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
import com.savbill.ticketmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.ticketmanagement.core.modules.utils.CommonConstants;
import com.savbill.ticketmanagement.core.modules.workflowaudit.domain.QWorkflowAudit;
import com.savbill.ticketmanagement.core.modules.workflowaudit.domain.WorkflowAudit;
import com.savbill.ticketmanagement.core.modules.workflowaudit.repository.WorkflowAuditRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkflowAuditService {

    @Autowired
    WorkflowAuditRepository workflowAuditRepository;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    public Integer MAX_PAGE_SIZE;
    public Map<String, String> sortColMap = new HashMap<>();



    public PageRequest pageRequest = null;
    public WorkflowAudit saveAudit(Integer eventId, String eventName, Integer entityId, String entityName, Integer actionByStaffId, String actionByUserName, String action, LocalDateTime actionDateTime, String remark) {
        WorkflowAudit workflowAudit = new WorkflowAudit();
        workflowAudit.setEventId(eventId);
        workflowAudit.setEventName(eventName);
        workflowAudit.setEntityId(entityId);
        workflowAudit.setEntityName(entityName);
        workflowAudit.setActionByStaffId(actionByStaffId);
        workflowAudit.setActionByName(actionByUserName);
        workflowAudit.setAction(action);
        workflowAudit.setActionDateTime(actionDateTime);
        workflowAudit.setRemark(remark);
        return workflowAuditRepository.save(workflowAudit);
    }
    public WorkflowAudit saveAudit(Integer eventId, String eventName, Integer entityId, String entityName, Integer actionByStaffId, String actionByUserName, String action, LocalDateTime actionDateTime, String remark,Integer cust_id,String approval_status) {
        WorkflowAudit workflowAudit = new WorkflowAudit();
        workflowAudit.setEventId(eventId);
        workflowAudit.setEventName(eventName);
        workflowAudit.setEntityId(entityId);
        workflowAudit.setEntityName(entityName);
        workflowAudit.setActionByStaffId(actionByStaffId);
        workflowAudit.setActionByName(actionByUserName);
        workflowAudit.setAction(action);
        workflowAudit.setActionDateTime(actionDateTime);
        workflowAudit.setRemark(remark);
        workflowAudit.setCustId(cust_id);
        workflowAudit.setApprovalStatus(approval_status);
        return workflowAuditRepository.save(workflowAudit);
    }

    public GenericDataDTO getListByCustomerId(Integer page, Integer pageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filters, Integer entityId, String eventName) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "id", sortOrder);
        QWorkflowAudit qWorkflowAudit = QWorkflowAudit.workflowAudit;
        BooleanExpression booleanExpression = qWorkflowAudit.isNotNull().and(qWorkflowAudit.entityId.eq(entityId)).and(qWorkflowAudit.eventName.eq(eventName));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        return makeGenericResponse(genericDataDTO, workflowAuditRepository.findAll(booleanExpression, pageRequest));
    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        if (pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        return pageRequest;
    }

    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<WorkflowAudit> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public GenericDataDTO filterAudit(String filterColumn,String filterValue, PaginationRequestDTO requestDTO) {
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "id", CommonConstants.SORT_ORDER_DESC);
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        QWorkflowAudit qWorkflowAudit=QWorkflowAudit.workflowAudit;
        BooleanExpression booleanExpression=qWorkflowAudit.isNotNull();
        if(filterColumn.equalsIgnoreCase(CommonConstants.WORKFLOW_AUDIT_STATUS.APPROVAL_STATUS) )  {
            booleanExpression=booleanExpression.and(qWorkflowAudit.approvalStatus.equalsIgnoreCase(filterValue));
        }
        if(filterColumn.equalsIgnoreCase(CommonConstants.WORKFLOW_AUDIT_STATUS.CUST_ID)){
            booleanExpression=booleanExpression.and(qWorkflowAudit.custId.eq(Integer.valueOf(filterValue)));

        }
        Page<WorkflowAudit> paginationList = workflowAuditRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }
}
