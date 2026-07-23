package com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroup;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.QTeamHierarchyMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyDTO;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamHierarchyMapping.TeamHierarchyMappingRepo;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit.WorkflowAuditService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.service.WorkflowAssignStaffMappingService;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveHierarchyShareDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateHierarchyShareDataMessage;
import com.savbill.inventorymanagement.utils.WorkFlowQueryUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class HierarchyService extends ExBaseAbstractService<HierarchyDTO, Hierarchy, Long> {

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    WorkFlowQueryUtils workFlowQueryUtils;

    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    StaffUserRepository staffRepository;
    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    WorkflowAssignStaffMappingService workflowAssignStaffMappingService;

    public HierarchyService(HierarchyRepository repository, HierarchyMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[HierarchyService]";
    }

    private static final Logger logger = Logger.getLogger(HierarchyService.class);

    public Map<String, Object> getTeamForNextApprove(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) throws Exception{
        Map<String, Object> map = new HashMap<>();
        try {
            System.out.println("======================================================Common method called for workflow started.================================================================");
            Hierarchy hierarchy = hierarchyRepository.findOne(
                    QHierarchy.hierarchy.isNotNull()
                            .and(QHierarchy.hierarchy.eventName.eq(eventName)
                                    .and(QHierarchy.hierarchy.isDeleted.eq(false))
                                    .and(QHierarchy.hierarchy.mvnoId.eq(mvnoId))
                                    .and(buId != null ? QHierarchy.hierarchy.buId.eq(buId) : QHierarchy.hierarchy.buId.isNull()))
            ).orElse(null);
            if (hierarchy != null) {
                List<TeamHierarchyMapping> teamHierarchyMappingList = IterableUtils.toList(teamHierarchyMappingRepo.findAll(
                        QTeamHierarchyMapping.teamHierarchyMapping.isNotNull()
                                .and(QTeamHierarchyMapping.teamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.getId()))
                                        .and(QTeamHierarchyMapping.teamHierarchyMapping.isDeleted.eq(false)))
                ));
                Integer finalOrderNumber = null;
                TeamHierarchyMapping nextTeamMapping = null;
                TeamHierarchyMapping currentTeamMapping = null;
                finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
                for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                    finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                    if (t.getOrderNumber().equals(finalOrderNumber)) {
                        nextTeamMapping = t;
                    }
                    if (finalOrderNumber != 0) {
                        int orderNumber = finalOrderNumber - 1;
                        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
                        if (teamHierarchyMappings.size() > 0) {
                            currentTeamMapping = teamHierarchyMappings.get(0);
                        } else {
                            currentTeamMapping = nextTeamMapping;
                        }
                    } else {
                        if (teamHierarchyMappingList.size() == 1) {
                            currentTeamMapping = teamHierarchyMappingList.get(0);
                        }
                    }
                }

                if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
                    if (isApproveRequest) {
                        workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
                    }
                }
                    if (nextTeamMapping != null) {
                        boolean flag = true;
                        List<StaffUserPojo> staffUsers = new ArrayList<>();
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
    //                        flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffUsers.size() == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
    //                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }

                        }
                        if (staffUsers.size() != 0 && nextTeamMapping.getId() != 0) {
                            map.put("assignableStaff", staffUsers);
                            map.put("nextTeamHierarchyMappingId", nextTeamMapping.getId());
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping != null ? currentTeamMapping.getTat_id() : nextTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                        }
                        map.put("eventId", 0);
                        map.put("eventName", String.valueOf(eventName));

                    }
                System.out.println("======================================================Common method called for workflow ended.================================================================");
                    return map;
                }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error(e.getMessage());
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return map;
    }

    private Integer getFinalOrderNumber(Boolean isApproveRequest, boolean isCreateRequest, Object entity, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber) {
        int teamHierarchyMappingId;
        if (isCreateRequest) {
            finalOrderNumber = 0;
        } else {
            if (Objects.nonNull(entity) && entity.getClass().equals(CustomerInventoryMapping.class)) {
                if (((CustomerInventoryMapping) entity).getTeamHierarchyMappingId() != null) {
                    teamHierarchyMappingId = ((CustomerInventoryMapping) entity).getTeamHierarchyMappingId();
                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
                }
            }
        }
        return finalOrderNumber;
    }
    private Integer getFinalOrderNumber(Boolean isApproveRequest, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber,
                                        int teamHierarchyMappingId) {
        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getId().equals(teamHierarchyMappingId)).collect(Collectors.toList());
        if (teamHierarchyMappings.size() > 0) {
            if (isApproveRequest) {
                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() + 1;
            } else {
                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() - 1;
            }
        }
        return finalOrderNumber;
    }

    List<Long> getServiceAreaFromEntity(Object entity) {
        List<Integer> ids = new ArrayList<>();
        List<PlanGroup> plangroupList = new ArrayList<>();
        List<Long> serviceAreaList = new ArrayList<>();
        if (Objects.nonNull(entity) && entity.getClass().equals(CustomerInventoryMapping.class)) {
            serviceAreaList.add(((CustomerInventoryMapping) entity).getCustomer().getServicearea().getId());
        }
        return serviceAreaList;
    }

    public Map<String, String> getTeamForNextApproveForAuto(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
        Map<String, String> map = new HashMap<>();
        try {
            System.out.println("======================================================Common method called for auto approve workflow started.================================================================");
            Optional<Hierarchy> hierarchy;
            Long eventId = 0L;
            QTeamHierarchyMapping qTeamHierarchyMapping = QTeamHierarchyMapping.teamHierarchyMapping;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
            }
            hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            if (hierarchy.isPresent()) {
                BooleanExpression expForTeamHirMapping = qTeamHierarchyMapping.isNotNull().and(qTeamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.get().getId())).and(qTeamHierarchyMapping.isDeleted.eq(false)));
                List<TeamHierarchyMapping> teamHierarchyMappingList = (List<TeamHierarchyMapping>) teamHierarchyMappingRepo.findAll(expForTeamHirMapping);
                Integer finalOrderNumber = null;
                TeamHierarchyMapping nextTeamMapping = null;
                TeamHierarchyMapping currentTeamMapping = new TeamHierarchyMapping();
                finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
                for (TeamHierarchyMapping t : teamHierarchyMappingList) {
                    finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
                    if (t.getOrderNumber().equals(finalOrderNumber)) {
                        nextTeamMapping = t;
                    }
                    if (finalOrderNumber != 0) {
                        int orderNumber = finalOrderNumber - 1;
                        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
                        if (teamHierarchyMappings.size() > 0) {
                            currentTeamMapping = teamHierarchyMappings.get(0);
                        } else {
                            currentTeamMapping = nextTeamMapping;
                        }
                    } else {
                        if (teamHierarchyMappingList.size() == 1) {
                            currentTeamMapping = teamHierarchyMappingList.get(0);
                        }
                    }
                }
                if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
                    if (isApproveRequest) {
                        workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
                    }
                }
                    if (nextTeamMapping != null) {
                        boolean flag = true;
                        int staffId = 0;
                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
    //                        flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                        }
                        if (flag) {
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            List<Long> serviceAreaList = getServiceAreaFromEntity(entity);
                            List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                            staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                        }
                        int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
                        while (k < teamHierarchyMappingList.size() && staffId == 0 && k >= 0) {
                            flag = true;
                            nextTeamMapping = teamHierarchyMappingList.get(k);
                            Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                            if (nextTeamMapping.getQueryFieldList().size() > 0) {
    //                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
                            }
                            if (flag) {
                                nextTeamMapping = teamHierarchyMappingList.get(k);
                                if (teams != null) {
                                    teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
                                    List<Long> serviceAreaList = getServiceAreaFromEntity(entity);
                                    List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
                                    staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
                                }
                            }
                            if (isApproveRequest != null) {
                                if (isApproveRequest || isCreateRequest) {
                                    k++;
                                } else {
                                    k--;
                                }
                            } else {
                                k++;
                            }

                        }
                        if (staffId != 0 && nextTeamMapping != null) {
                            map.put("staffId", String.valueOf(staffId));
                            map.put("nextTatMappingId", String.valueOf(nextTeamMapping.getId()));
                            map.put("eventId", String.valueOf(eventId));
                            map.put("eventName", String.valueOf(eventName));
                            map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
                            map.put("current_tat_id", String.valueOf(currentTeamMapping.getTat_id()));
                            map.put("workFlowId", String.valueOf(hierarchy.get().getId()));
                            map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
                        }
                    } else {
                        map.put("eventId", String.valueOf(eventId));
                        map.put("eventName", String.valueOf(eventName));
                    }
                    System.out.println("======================================================Common method called for auto approve workflow ended.================================================================");
                    return map;
                }
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return map;
    }

    public void rejectDirectFromCreatedStaff(String eventName, Integer entityid) {
        boolean b = false;
        try {
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityid)).orElse(null);
                    if (customerInventoryMapping != null) {

                        if (customerInventoryMapping.getTeamHierarchyMappingId() == null) {
                            b = (customerInventoryMapping.getCreatedById()) == getLoggedInUserId();
                            if (true == b) {
                                customerInventoryMapping.setStatus(CommonConstants.REJECTED);
//                                customerInventoryMapping.setStatus(SubscriberConstants.REJECT);
                            }
                        }

                    }
                    break;
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public GenericDataDTO assignFromStaffList(Integer nextAssignStaff, String eventName, Integer entityId,
                                              boolean isApproveRequest) throws NoSuchFieldException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            StaffUser staffUser = staffRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
            StaffUser assignedToStaff = staffRepository.findById(Integer.valueOf(nextAssignStaff)).get();
            Customers customers = null;
            PostpaidPlan postpaidPlan = null;
            PlanGroup planGroup = null;
            CustomerInventoryMapping customerInventoryMapping = new CustomerInventoryMapping();
            Map<String, String> map = new HashMap<>();
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityId)).orElse(null);
                    if (customerInventoryMapping != null) {
                        map = getTeamForNextApproveForAuto(customerInventoryMapping.getCustomer().getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMapping);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            customerInventoryMapping.setNextApprover(assignedToStaff);
                            customerInventoryMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMapping.getId()), customerInventoryMapping.getProduct().getName(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.INVENTORY + " with product name : " + " ' " + customerInventoryMapping.getProduct().getName() + " ' " + "and " + "quantity : " + " ' " + customerInventoryMapping.getQty() + " '";
                        }
                        customerInventoryMappingRepo.save(customerInventoryMapping);
                    }
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN_REPLACE: {
                    InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(Long.valueOf(entityId)).orElse(null);
                    customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                    if (inOutWardMACMapping != null) {
                        map = getTeamForNextApproveForAuto(customerInventoryMapping.getCustomer().getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, inOutWardMACMapping);
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            inOutWardMACMapping.setCurrentApproveId(assignedToStaff.getId());
                            inOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inOutWardMACMapping.getId()), inOutWardMACMapping.getSerialNumber(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
                        }
                        inOutWardMacRepo.save(inOutWardMACMapping);
                    }
                    break;
                }
            }
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    public void saveHierachy(SaveHierarchyShareDataMessage message) throws Exception{
        try {
            Hierarchy hierarchy = new Hierarchy();
            hierarchy.setId(message.getId());
            hierarchy.setHierarchyName(message.getHierarchyName());
            hierarchy.setEventName(message.getEventName());
            hierarchy.setMvnoId(message.getMvnoId());
            hierarchy.setBuId(message.getBuId());
            hierarchy.setLcoId(message.getLcoId());
            hierarchy.setIsDeleted(message.getIsDeleted());
            hierarchy.setTeamHierarchyMappingList(message.getTeamHierarchyMappingList());
            hierarchyRepository.save(hierarchy);
            logger.info("Hierarchy created successfully with name " + message.getHierarchyName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create hierarchy with name " + message.getHierarchyName() + " , Error: " + e.getMessage());
        }
    }


    public void updateHierarchy(UpdateHierarchyShareDataMessage message) throws Exception{
        try {
        Hierarchy hierarchy = hierarchyRepository.findById(message.getId()).orElse(null);
            if (hierarchy != null) {
                hierarchy.setId(message.getId());
                hierarchy.setHierarchyName(message.getHierarchyName());
                hierarchy.setEventName(message.getEventName());
                hierarchy.setMvnoId(message.getMvnoId());
                hierarchy.setBuId(message.getBuId());
                hierarchy.setLcoId(message.getLcoId());
                hierarchy.setIsDeleted(message.getIsDeleted());
                hierarchy.setTeamHierarchyMappingList(message.getTeamHierarchyMappingList());
                hierarchyRepository.save(hierarchy);
                logger.info("Hierarchy updated successfully with name " + message.getHierarchyName());
            } else {
                Hierarchy hierarchy1 = new Hierarchy();
                hierarchy1.setId(message.getId());
                hierarchy1.setHierarchyName(message.getHierarchyName());
                hierarchy1.setEventName(message.getEventName());
                hierarchy1.setMvnoId(message.getMvnoId());
                hierarchy1.setBuId(message.getBuId());
                hierarchy1.setLcoId(message.getLcoId());
                hierarchy1.setIsDeleted(message.getIsDeleted());
                hierarchy1.setTeamHierarchyMappingList(message.getTeamHierarchyMappingList());
                hierarchyRepository.save(hierarchy1);
                logger.info("Hierarchy updated successfully with name " + message.getHierarchyName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update hierarchy with name " + message.getHierarchyName() + " , Error: " + e.getMessage());
        }
    }

    public List<TeamHierarchyDTO> getApproveProgress(String eventName, Long entityId) {
        List<TeamHierarchyDTO> teamHierarchyDTOList = new ArrayList<TeamHierarchyDTO>();
        Integer mvnoId = 0;
        Long buId = 0L;
        Integer currentTeamMappingId = 0;
        try {
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(entityId).orElse(null);
                    if (customerInventoryMapping != null) {
                        if (customerInventoryMapping.getCustomer().getMvnoId() != null) {
                            mvnoId = customerInventoryMapping.getMvnoId();
                        }
                        if (customerInventoryMapping.getCustomer().getBuId() != null) {
                            buId = customerInventoryMapping.getCustomer().getBuId();
                        }
                        if (customerInventoryMapping.getTeamHierarchyMappingId() != null) {
                            currentTeamMappingId = customerInventoryMapping.getTeamHierarchyMappingId();
                        }
                    }
                    break;
                }
            }
            Optional<Hierarchy> hierarchy;
            QHierarchy qHierarchy = QHierarchy.hierarchy;
            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
            if (buId != null && buId != 0) {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            } else {
                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
            }
            if (hierarchy.isPresent()) {
                List<TeamHierarchyMapping> teamHierarchyMappings = hierarchy.get().getTeamHierarchyMappingList();
                if (currentTeamMappingId == null || currentTeamMappingId == 0) {
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        TeamHierarchyDTO dto = new TeamHierarchyDTO();
                        dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                        dto.setStatus("Approved");
                        if (i + 1 == teamHierarchyMappings.size()) {
                            dto.setParentTeamsId(null);
                        } else {
                            dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                        }
                        dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                        teamHierarchyDTOList.add(dto);

                    }

                } else {
                    int currentOrder = 0;
                    for (TeamHierarchyMapping t : hierarchy.get().getTeamHierarchyMappingList()) {
                        if (Objects.equals(t.getId(), currentTeamMappingId)) {
                            currentOrder = t.getOrderNumber();
                        }
                    }
                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
                        if (teamHierarchyMappings.get(i).getOrderNumber() < currentOrder) {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus("Approved");
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);
                        } else {
                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
                            dto.setStatus(CommonConstants.PENDING);
                            if (i + 1 == teamHierarchyMappings.size()) {
                                dto.setParentTeamsId(null);
                            } else {
                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
                            }
                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
                            teamHierarchyDTOList.add(dto);

                        }
                    }
                }
            }
            return teamHierarchyDTOList;


        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());
        }
        return teamHierarchyDTOList;
    }

    public GenericDataDTO assignEveryStaff(Integer entityId, String eventName, Boolean isApproveRequest) throws
            Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Object> map = new HashMap<>();
            switch (eventName) {
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityId)).orElse(null);
                    map = getTeamForNextApprove(customerInventoryMapping.getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMapping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerInventoryMapping, map);
                    break;
                }
                case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN_REPLACE: {
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(Long.valueOf(entityId)).orElse(null);
                    map = getTeamForNextApprove(customerInventoryMapping.getMvnoId(), customerInventoryMapping.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMapping);
                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, eventName, customerInventoryMapping, map);
                    break;
                }
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully Assigned ");
        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }
}
