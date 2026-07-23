package com.savbill.cpm.service.common;

import com.savbill.cpm.constants.ClientServiceConstant;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.model.common.CustomerCafImageMapping;
import com.savbill.cpm.model.common.QWorkflowAudit;
import com.savbill.cpm.model.common.WorkflowAudit;
import com.savbill.cpm.modules.WorkFlowInProgressEntity.Entity.WorkFlowInProgressData;

import com.savbill.cpm.repository.common.WorkflowAuditRepository;
import com.savbill.cpm.repository.radius.CustomerCafImageMappingRepository;
import com.savbill.cpm.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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

    @Autowired
    CustomerCafImageMappingRepository customerCafImageMappingRepository;

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

    public GenericDataDTO getListByCustomerId(
            Integer page, Integer pageSize, String sortBy, Integer sortOrder,
            List<GenericSearchModel> filters, Integer entityId, String eventName) {

        // Validate and set default sorting column if null
        String sortingColumn = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "id";

        // Generate pageable request
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortingColumn, sortOrder);

        // Build QueryDSL boolean expression
        QWorkflowAudit qWorkflowAudit = QWorkflowAudit.workflowAudit;
        BooleanExpression booleanExpression = qWorkflowAudit.entityId.eq(entityId)
                .and(qWorkflowAudit.eventName.eq(eventName));

        // Execute query and build response
        Page<WorkflowAudit> auditPage = workflowAuditRepository.findAll(booleanExpression, pageRequest);

        // For rejected audits, fetch files from CustomerCafImageMapping and set dynamically
        List<WorkflowAudit> auditsWithFiles = auditPage.stream().map(audit -> {
            if ("Rejected".equalsIgnoreCase(audit.getAction())) {
                List<CustomerCafImageMapping> files = customerCafImageMappingRepository.findByCustomerId(Long.valueOf(audit.getEntityId()));
                List<Map<String,String>> fileList = files.stream().map(f -> {
                    Map<String,String> fm = new HashMap<>();
                    fm.put("customerCafImageMappingId", String.valueOf(f.getId()));
                    fm.put("customerId", String.valueOf(f.getCustomerId()));
                    fm.put("fileName", f.getFilename());
                    fm.put("uniqueName", f.getUniquename());
                    return fm;
                }).collect(Collectors.toList());
                audit.setFiles(fileList);
            }
            return audit;
        }).collect(Collectors.toList());

        // Create new Page for updated list, you may wrap List back in PageImpl for pagination metadata if necessary
        Page<WorkflowAudit> updatedPage = new org.springframework.data.domain.PageImpl<>(auditsWithFiles, pageRequest, auditPage.getTotalElements());

        return makeGenericResponse(new GenericDataDTO(), updatedPage);
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

    public List<WorkFlowInProgressData> getWorkflowInProgressData(Integer mvnoid) {
        List<Object[]> results = workflowAuditRepository.getWorkflowInProgressData(mvnoid);
        return results.stream().map(result -> new WorkFlowInProgressData(
                convertToLong(result[0]),  // Convert BigInteger to Integer
                (String) result[1],
                (String) result[2],
                (String) result[3],
                (String) result[4],
                (String) result[5]
        )).collect(Collectors.toList());
    }



    private Long convertToLong(Object value) {
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }
}
