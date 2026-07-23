package com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.service;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit.WorkflowAuditService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.domain.WorkflowAssignStaffMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.repository.WorkflowAssignStaffMappingRepo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowAssignStaffMappingService {

    @Autowired
    private WorkflowAssignStaffMappingRepo workflowAssignStaffMappingRepo;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    public void assignWorkflowToStaff(Integer eventId, String eventName, Object entityPojo, Map<String, Object> map) {
        try {
            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
            Integer nextTeamHierarchyMappingId = (Integer) map.get("nextTeamHierarchyMappingId");
            Long entityId = 0L;
            for (StaffUserPojo staffUserPojo : staffUserPojos) {
                switch (eventName) {
                    case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
                        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
                        if (entityPojo instanceof CustomerInventoryMapping) {
                            CustomerInventoryMapping customerInventoryMapping = (CustomerInventoryMapping) entityPojo;
                            entityId = customerInventoryMapping.getId();
                            if (nextTeamHierarchyMappingId != null) {
                                customerInventoryMapping.setTeamHierarchyMappingId(nextTeamHierarchyMappingId);
                                customerInventoryMapping.setNextApprover(null);
                            }
                            saveMappings(eventId, eventName, staffUserPojo, entityId, customerInventoryMapping.getCustomer().getUsername(), CommonConstants.WORKFLOW_MSG_ACTION.CUSTOMER, nextTeamHierarchyMappingId);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                        }
                        break;
                    }
                }

                if (staffUserPojo.getParentStaffId() != null && !CollectionUtils.isEmpty(map)) {
                    Map<String, String> workFlowMap = new HashMap<>();
                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null") {
                        workFlowMap.put("tat_id", (String) map.get("current_tat_id"));
                        workFlowMap.put("nextTatMappingId", map.get("nextTeamHierarchyMappingId").toString());
                        workFlowMap.put("workFlowId", (String) map.get("workFlowId"));
                        workFlowMap.put("orderNo", (String) map.get("orderNo"));
                    }
                    workFlowMap.put("eventName", (String) map.get("eventName"));
                    workFlowMap.put("eventId", "0");
                }
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, e.getMessage(), null);
        }
    }

    private void saveMappings(Integer eventId, String eventName, StaffUserPojo staffUserPojo, Long entityId, String entityName, String actionEntity, Integer nextTeamHierarchyMappingId) {
        WorkflowAssignStaffMapping workflowAssignStaffMapping = new WorkflowAssignStaffMapping();
        workflowAssignStaffMapping.setStaffId(staffUserPojo.getId());
        workflowAssignStaffMapping.setEntityId(Math.toIntExact(entityId));
        workflowAssignStaffMapping.setEventName(eventName);
        workflowAssignStaffMapping.setTeamHierarchyMappingId(nextTeamHierarchyMappingId);
        workflowAssignStaffMappingRepo.save(workflowAssignStaffMapping);
        workflowAuditService.saveAudit(eventId, eventName, Math.toIntExact(entityId), entityName, staffUserPojo.getId(), staffUserPojo.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUserPojo.getUsername());
        String action = actionEntity + " " + entityName + " " + CommonConstants.WORKFLOW_ASSIGNED_FOR_APPROVAL;
    }
}
