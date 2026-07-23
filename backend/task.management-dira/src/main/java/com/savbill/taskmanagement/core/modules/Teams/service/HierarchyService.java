package com.savbill.taskmanagement.core.modules.Teams.service;

import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.taskmanagement.core.modules.Teams.domain.*;
import com.savbill.taskmanagement.core.modules.Teams.domain.Hierarchy;
import com.savbill.taskmanagement.core.modules.Teams.domain.QueryFieldMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.taskmanagement.core.modules.Teams.model.HierarchyDTO;
import com.savbill.taskmanagement.core.modules.Teams.repository.*;
import com.savbill.taskmanagement.core.modules.Teams.repository.*;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.dto.StaffUserPojo;
import com.savbill.taskmanagement.core.modules.staffuser.mapper.StaffUserMapper;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseUpdateService;
import com.savbill.taskmanagement.core.modules.utils.TatUtils;
import com.savbill.taskmanagement.core.modules.utils.WorkFlowQueryUtils;
import com.savbill.taskmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.SaveHierarchyShareDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.UpdateHierarchyShareDataMessage;
import com.savbill.taskmanagement.rabbitmq.messages.WorkflowTicketMessage;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HierarchyService extends ExBaseAbstractService<HierarchyDTO, Hierarchy,Long> {

    @Autowired
    HierarchyRepository hierarchyRepository;


    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    TeamsRepository teamsRepository;


    @Autowired
    WorkFlowQueryUtils workFlowQueryUtils;


    @Autowired
    ServiceAreaRepository serviceAreaRepository;


    @Autowired
    CustomersService customersService;


    @Autowired
    CaseRepository caseRepository;


    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    NotificationTemplateRepository templateRepository;


    //@Autowired
//MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    StaffUserService staffUserService;
    @Autowired
    CaseService caseService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    CaseUpdateService caseUpdateService;


    @Autowired
    CaseMapper caseMapper;

    @Autowired
    TatUtils tatUtils;

    @Autowired
    QueryFieldRepo queryFieldRepo;


    @Autowired
    TeamUserMappingsRepository teamUserMappingsRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    StaffUserMapper staffUserMapper;

    private static Log log = LogFactory.getLog(HierarchyService.class);
    public HierarchyService(JpaRepository<Hierarchy, Long> repository, IBaseMapper<HierarchyDTO, Hierarchy> mapper) {
        super(repository, mapper);
    }

    //CRUD code
@Transactional
    public void saveHierachy(SaveHierarchyShareDataMessage message){
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
        }catch (Exception e){
            log.error("Unable to Create Hirarchy"+e.getMessage());
        }
    }


    public void updateHierarchy(UpdateHierarchyShareDataMessage message){
        try {
            Hierarchy hierarchy = new Hierarchy();

            hierarchy = hierarchyRepository.findById(message.getId()).orElse(null);
            hierarchy.setHierarchyName(message.getHierarchyName());
            hierarchy.setEventName(message.getEventName());
            hierarchy.setMvnoId(message.getMvnoId());
            hierarchy.setBuId(message.getBuId());
            hierarchy.setLcoId(message.getLcoId());
            hierarchy.setIsDeleted(message.getIsDeleted());

            List<TeamHierarchyMapping> oldTeamHierarchyMapping = hierarchy.getTeamHierarchyMappingList();
            List<QueryFieldMapping> oldQueryFieldMapping = new ArrayList<>();
            if (!oldTeamHierarchyMapping.isEmpty()) {
                for (TeamHierarchyMapping teamHierarchyMapping : oldTeamHierarchyMapping) {
                    if (teamHierarchyMapping.getQueryFieldList() != null) {
                        queryFieldRepo.deleteInBatch(teamHierarchyMapping.getQueryFieldList());
                    }
                }
                teamHierarchyMappingRepo.deleteInBatch(oldTeamHierarchyMapping);
            }
            hierarchy.setTeamHierarchyMappingList(message.getTeamHierarchyMappingList());

            hierarchyRepository.save(hierarchy);

        }catch (Exception e){
            log.error("Unable to create Hierarchy "+e.getMessage());
        }
    }


    // Workflow code
//@Transactional
//    public Map<String, String> getTeamForNextApproveForAuto(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
//        System.out.println("======================================================Common method called for workflow.================================================================");
//        Map<String, String> map = new HashMap<>();
//        Optional<Hierarchy> hierarchy;
//        Long eventId = 0L;
//        QTeamHierarchyMapping qTeamHierarchyMapping = QTeamHierarchyMapping.teamHierarchyMapping;
//        QHierarchy qHierarchy = QHierarchy.hierarchy;
//        BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
//        if (buId != null) {
//            booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
//        } else {
//            booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
//        }
//        hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
//        if (hierarchy.isPresent()) {
//            BooleanExpression expForTeamHirMapping = qTeamHierarchyMapping.isNotNull().and(qTeamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.get().getId())).and(qTeamHierarchyMapping.isDeleted.eq(false)));
//            List<TeamHierarchyMapping> teamHierarchyMappingList = (List<TeamHierarchyMapping>) teamHierarchyMappingRepo.findAll(expForTeamHirMapping);
//            Integer finalOrderNumber = null;
//            TeamHierarchyMapping nextTeamMapping = null;
//            TeamHierarchyMapping currentTeamMapping = new TeamHierarchyMapping();
//            finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
//            for (TeamHierarchyMapping t : teamHierarchyMappingList) {
//                finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
//                if (t.getOrderNumber().equals(finalOrderNumber)) {
//                    nextTeamMapping = t;
//                }
//                if (finalOrderNumber != 0) {
//                    int orderNumber = finalOrderNumber - 1;
//                    List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
//                    if (teamHierarchyMappings.size() > 0) {
//                        currentTeamMapping = teamHierarchyMappings.get(0);
//                    } else {
//                        currentTeamMapping = nextTeamMapping;
//                    }
//                } else {
//                    if (teamHierarchyMappingList.size() == 1) {
//                        currentTeamMapping = teamHierarchyMappingList.get(0);
//                    }
//                }
//            }
//            if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
//                if (isApproveRequest) {
//
//                    //Rabbitmq Call
//                   // workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
//                }
//            }
//            try {
//                if (nextTeamMapping != null) {
//                    boolean flag = true;
//                    int staffId = 0;
//                    if (nextTeamMapping.getQueryFieldList().size() > 0) {
//                        flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
//                    }
//                    if (flag) {
//                        Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                        List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
//                        List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
//                        staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
//                    }
//                    int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
//                    while (k < teamHierarchyMappingList.size() && staffId == 0 && k >= 0) {
//                        flag = true;
//                        nextTeamMapping = teamHierarchyMappingList.get(k);
//                        Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
//                            //Rabbitmq call
//                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
//                        }
//                        if (flag) {
//                            nextTeamMapping = teamHierarchyMappingList.get(k);
//                            if (teams != null) {
//                                teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                                List<ServiceArea> serviceAreaList = getServiceAreaFromEntity(entity);
//                                List<StaffUserPojo> staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(serviceAreaList, buId, teams);
//                                staffId = workFlowQueryUtils.assignStaffFromList(staffUsers, eventName, entity);
//                            }
//                        }
//                        if (isApproveRequest != null) {
//                            if (isApproveRequest || isCreateRequest) {
//                                k++;
//                            } else {
//                                k--;
//                            }
//                        } else {
//                            k++;
//                        }
//
//                    }
//                    if (staffId != 0 && nextTeamMapping != null) {
//                        map.put("staffId", String.valueOf(staffId));
//                        map.put("nextTatMappingId", String.valueOf(nextTeamMapping.getId()));
//                        map.put("eventId", String.valueOf(eventId));
//                        map.put("eventName", String.valueOf(eventName));
//                        map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
//                        map.put("current_tat_id", String.valueOf(currentTeamMapping.getTat_id()));
//                        map.put("workFlowId", String.valueOf(hierarchy.get().getId()));
//                        map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
//                    }
//                } else {
//                    map.put("eventId", String.valueOf(eventId));
//                    map.put("eventName", String.valueOf(eventName));
//                }
//                return map;
//            } catch (CustomValidationException ex) {
//                ApplicationLogger.logger.error(ex.getMessage(), ex);
//                throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
//            } catch (Exception e) {
//                ApplicationLogger.logger.error(e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }
//
//        return map;
//    }
//@Transactional
//    public List<TeamHierarchyDTO> getApproveProgress(String eventName, Long entityId) {
//        List<TeamHierarchyDTO> teamHierarchyDTOList = new ArrayList<TeamHierarchyDTO>();
//        Integer mvnoId = 0;
//        Long buId = 0L;
//        Integer currentTeamMappingId = 0;
//        try {
//            switch (eventName) {
//
//                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
//                    Case aCase = caseRepository.findById(entityId).orElse(null);
//                    if (aCase != null) {
//                        if (aCase.getStaffUser().getMvnoId() != null) {
//                            mvnoId = aCase.getStaffUser().getMvnoId();
//                        }
////                        if (aCase.getStaffUser().getBuId() != null) {
////                            buId = aCase.getCustomers().getBuId();
////                        }
//                        if (aCase.getTeamHierarchyMappingId() != null) {
//                            currentTeamMappingId = Math.toIntExact(aCase.getTeamHierarchyMappingId());
//                        }
//                    }
//                    break;
//                }
//
//            }
//            Optional<Hierarchy> hierarchy;
//            QHierarchy qHierarchy = QHierarchy.hierarchy;
//            BooleanExpression booleanExpHierarchy = qHierarchy.isNotNull().and(qHierarchy.eventName.eq(eventName).and(qHierarchy.isDeleted.eq(false)).and(qHierarchy.mvnoId.eq(mvnoId)));
//            if (buId != null && buId != 0) {
//                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.eq(buId));
//                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
//            } else {
//                booleanExpHierarchy = booleanExpHierarchy.and(qHierarchy.buId.isNull());
//                hierarchy = hierarchyRepository.findOne(booleanExpHierarchy);
//            }
//            if (hierarchy.isPresent()) {
//                List<TeamHierarchyMapping> teamHierarchyMappings = hierarchy.get().getTeamHierarchyMappingList();
//                if (currentTeamMappingId == null || currentTeamMappingId == 0) {
//                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
//                        TeamHierarchyDTO dto = new TeamHierarchyDTO();
//                        dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
//                        dto.setStatus("Approved");
//                        if (i + 1 == teamHierarchyMappings.size()) {
//                            dto.setParentTeamsId(null);
//                        } else {
//                            dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
//                        }
//                        dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
//                        teamHierarchyDTOList.add(dto);
//
//                    }
//
//                } else {
//                    int currentOrder = 0;
//                    for (TeamHierarchyMapping t : hierarchy.get().getTeamHierarchyMappingList()) {
//                        if (Objects.equals(t.getId(), currentTeamMappingId)) {
//                            currentOrder = t.getOrderNumber();
//                        }
//                    }
//                    for (int i = 0; i < teamHierarchyMappings.size(); i++) {
//                        if (teamHierarchyMappings.get(i).getOrderNumber() < currentOrder) {
//                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
//                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
//                            dto.setStatus("Approved");
//                            if (i + 1 == teamHierarchyMappings.size()) {
//                                dto.setParentTeamsId(null);
//                            } else {
//                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
//                            }
//                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
//                            teamHierarchyDTOList.add(dto);
//                        } else {
//                            TeamHierarchyDTO dto = new TeamHierarchyDTO();
//                            dto.setTeamsId(teamHierarchyMappings.get(i).getTeamId().longValue());
//                            dto.setStatus("Pending");
//                            if (i + 1 == teamHierarchyMappings.size()) {
//                                dto.setParentTeamsId(null);
//                            } else {
//                                dto.setParentTeamsId(teamHierarchyMappings.get(i + 1).getTeamId().longValue());
//                            }
//                            dto.setTeamName(teamsRepository.findById(teamHierarchyMappings.get(i).getTeamId().longValue()).get().getName());
//                            teamHierarchyDTOList.add(dto);
//
//                        }
//                    }
//                }
//            }
//            return teamHierarchyDTOList;
//
//
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(e.getMessage());
//        }
//        return teamHierarchyDTOList;
//    }

//    List<ServiceArea> getServiceAreaFromEntity(Object entity) {
//        List<Integer> ids = new ArrayList<>();
//
//        List<ServiceArea> serviceAreaList = new ArrayList<>();
//        if (Objects.nonNull(entity) && entity.getClass().equals(CaseDTO.class)) {
//            serviceAreaList.add(serviceAreaRepository.findById(Long.valueOf(customersService.get(((CaseDTO) entity).getCustomersId()).getServiceAreaId())).get());
//        }
//
//        return serviceAreaList;
//    }
//@Transactional
//    public Map<String, Object> getTeamForNextApprove(Integer mvnoId, Long buId, String eventName, String listType, Boolean isApproveRequest, boolean isCreateRequest, Object entity) {
//        System.out.println("======================================================Common method called for workflow.================================================================");
//        Map<String, Object> map = new HashMap<>();
//        Hierarchy hierarchy = hierarchyRepository.findOne(
//                QHierarchy.hierarchy.isNotNull()
//                        .and(QHierarchy.hierarchy.eventName.eq(eventName)
//                                .and(QHierarchy.hierarchy.isDeleted.eq(false))
//                                .and(QHierarchy.hierarchy.mvnoId.eq(mvnoId))
//                                .and(buId != null ? QHierarchy.hierarchy.buId.eq(buId) : QHierarchy.hierarchy.buId.isNull()))
//        ).orElse(null);
//        if (hierarchy != null) {
//            List<TeamHierarchyMapping> teamHierarchyMappingList = IterableUtils.toList(teamHierarchyMappingRepo.findAll(
//                    QTeamHierarchyMapping.teamHierarchyMapping.isNotNull()
//                            .and(QTeamHierarchyMapping.teamHierarchyMapping.hierarchyId.eq(Math.toIntExact(hierarchy.getId()))
//                                    .and(QTeamHierarchyMapping.teamHierarchyMapping.isDeleted.eq(false)))
//            ));
//            Integer finalOrderNumber = null;
//            TeamHierarchyMapping nextTeamMapping = null;
//            TeamHierarchyMapping currentTeamMapping = null;
//            finalOrderNumber = getFinalOrderNumber(isApproveRequest, isCreateRequest, entity, teamHierarchyMappingList, finalOrderNumber);
//            for (TeamHierarchyMapping t : teamHierarchyMappingList) {
//                finalOrderNumber = finalOrderNumber == null ? 0 : finalOrderNumber;
//                if (t.getOrderNumber().equals(finalOrderNumber)) {
//                    nextTeamMapping = t;
//                }
//                if (finalOrderNumber != 0) {
//                    int orderNumber = finalOrderNumber - 1;
//                    List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getOrderNumber().equals(orderNumber)).collect(Collectors.toList());
//                    if (teamHierarchyMappings.size() > 0) {
//                        currentTeamMapping = teamHierarchyMappings.get(0);
//                    } else {
//                        currentTeamMapping = nextTeamMapping;
//                    }
//                } else {
//                    if (teamHierarchyMappingList.size() == 1) {
//                        currentTeamMapping = teamHierarchyMappingList.get(0);
//                    }
//                }
//            }
//
//            if (currentTeamMapping != null && currentTeamMapping.getTeamAction() != null && isApproveRequest != null) {
//                if (isApproveRequest) {
//                    //Rabbitmq Call
//                    //workFlowQueryUtils.checkAction(currentTeamMapping.getTeamAction(), eventName, entity);
//                }
//            }
//
//            try {
//                if (nextTeamMapping != null) {
//                    boolean flag = true;
//                    List<StaffUserPojo> staffUsers = new ArrayList<>();
//                    if (nextTeamMapping.getQueryFieldList().size() > 0) {
//                        //Rabbitmq call
//                        flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
//                    }
//                    if (flag) {
//                        Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                        staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
//                    }
//                    int k = teamHierarchyMappingList.indexOf(nextTeamMapping);
//                    while (k < teamHierarchyMappingList.size() && staffUsers.size() == 0 && k >= 0) {
//                        flag = true;
//                        nextTeamMapping = teamHierarchyMappingList.get(k);
//                        Teams teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                        if (nextTeamMapping.getQueryFieldList().size() > 0) {
//                            //Rabbitmq call
//                            flag = workFlowQueryUtils.checkCondition(nextTeamMapping.getQueryFieldList(), eventName, entity);
//                        }
//                        if (flag) {
//                            nextTeamMapping = teamHierarchyMappingList.get(k);
//                            if (teams != null) {
//                                teams = teamsRepository.findById(Long.valueOf(nextTeamMapping.getTeamId())).orElse(null);
//                                staffUsers = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), buId, teams);
//                            }
//                        }
//                        if (isApproveRequest != null) {
//                            if (isApproveRequest || isCreateRequest) {
//                                k++;
//                            } else {
//                                k--;
//                            }
//                        } else {
//                            k++;
//                        }
//
//                    }
//                    if (staffUsers.size() != 0 && nextTeamMapping.getId() != 0) {
//                        map.put("assignableStaff", staffUsers);
//                        map.put("nextTeamHierarchyMappingId", nextTeamMapping.getId());
//                        map.put("tat_id", String.valueOf(nextTeamMapping.getTat_id()));
//                        map.put("current_tat_id", String.valueOf(currentTeamMapping != null ? currentTeamMapping.getTat_id() : nextTeamMapping.getTat_id()));
//                        map.put("workFlowId", String.valueOf(hierarchy.getId()));
//                        map.put("orderNo", String.valueOf(nextTeamMapping.getOrderNumber()));
//                    }
//                    map.put("eventId", 0);
//                    map.put("eventName", String.valueOf(eventName));
//
//                }
//                return map;
//            } catch (CustomValidationException e) {
//                ApplicationLogger.logger.error(e.getMessage());
//                throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return map;
//    }
//@Transactional
//    private Integer getFinalOrderNumber(Boolean isApproveRequest, boolean isCreateRequest, Object entity, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber) {
//        int teamHierarchyMappingId;
//        if (isCreateRequest) {
//            finalOrderNumber = 0;
//        } else {
//            if (Objects.nonNull(entity) && entity.getClass().equals(CaseDTO.class)) {
//                if (((CaseDTO) entity).getTeamHierarchyMappingId() != null) {
//                    teamHierarchyMappingId = ((CaseDTO) entity).getTeamHierarchyMappingId();
//                    finalOrderNumber = getFinalOrderNumber(isApproveRequest, teamHierarchyMappingList, finalOrderNumber, teamHierarchyMappingId);
//                }
//            }
//        }
//        return finalOrderNumber;
//    }
//@Transactional
//    private Integer getFinalOrderNumber(Boolean isApproveRequest, List<TeamHierarchyMapping> teamHierarchyMappingList, Integer finalOrderNumber,
//                                        int teamHierarchyMappingId) {
//        List<TeamHierarchyMapping> teamHierarchyMappings = teamHierarchyMappingList.stream().filter(teamHierarchyMapping -> teamHierarchyMapping.getId().equals(teamHierarchyMappingId)).collect(Collectors.toList());
//        if (teamHierarchyMappings.size() > 0) {
//            if (isApproveRequest) {
//                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() + 1;
//            } else {
//                finalOrderNumber = teamHierarchyMappings.get(0).getOrderNumber() - 1;
//            }
//        }
//        return finalOrderNumber;
//    }

    @Override
    public String getModuleNameForLog() {
        return "HierarchyService";
    }


//@Transactional
//    public List<StaffUserPojo> getStaffFromCurrentTeammapping(Integer id, Object entity) {
//        TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(id).orElse(null);
//        Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
//        if (entity instanceof CaseDTO) {
//            CaseDTO aCase = (CaseDTO) entity;
//            StaffUser staffUser = staffUserRepository.findById(aCase.getStaffId()).orElse(null);
//            if (staffUser != null) {
//                staffUserPojos = workFlowQueryUtils.assignCAFToStaffFromTeam(getServiceAreaFromEntity(entity), null, teams);
//            }
//        }
//        return staffUserPojos;
//    }
    @Transactional
    public List<StaffUserPojo> getStaffFromTeam(Integer teamId, Object entity) {
        List<Integer> staffListIds = teamUserMappingsRepository.findAllStaffIdsByTeamId(teamId);
        List<StaffUserPojo> staffUserPojos = new ArrayList<>();
        if(staffListIds!=null){
            List<StaffUser> staffUserList =staffUserRepository.findAllByIdInAndStatusEqualsIgnoreCase(staffListIds,"active");
            staffUserPojos= staffUserList.stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }

        return staffUserPojos;
    }
@Transactional
    public void sendWorkflowAssignActionMessage(String countryCode, String mobileNumber, String emailId, Integer
            mvnoId, String staffPersonName, String action,Long buId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.WORKFLOW_ASSIGN_ACTION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    // Set message in queue to send notification after opt generated successfully.
                    WorkflowTicketMessage workflowTicketMessage = new WorkflowTicketMessage(RMQConstants.WORKFLOW_ASSIGN_ACTION_MESSAGE, RMQConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY, optionalTemplate.get(), staffPersonName, mobileNumber, emailId, mvnoId, action,buId);
                    Gson gson = new Gson();
                    gson.toJson(workflowTicketMessage);
//                    messageSender.send(workflowTicketMessage, RMQConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE);
                    kafkaMessageSender.send(new KafkaMessageData(workflowTicketMessage, WorkflowTicketMessage.class.getSimpleName()));
                }
            }

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }





//    @Transactional
//    public GenericDataDTO assignFromStaffList(Integer nextAssignStaff, String eventName, Integer entityId,
//                                              boolean isApproveRequest) throws NoSuchFieldException {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//
//            StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//            StaffUser assignedToStaff = staffUserService.get(nextAssignStaff);
//            Customers customers = null;
//
//            CaseDTO caseDTO = null;
//
//            Map<String, String> map = new HashMap<>();
//
//            switch (eventName) {
//
//                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
//                    Case aCase = caseService.getRepository().findById(Long.valueOf(entityId)).orElse(null);
//                    CaseUpdateDTO caseUpdateDTO = new CaseUpdateDTO();
//                    caseUpdateDTO.setTicketId(aCase.getCaseId());
//                    if (isApproveRequest) {
//                        aCase.setCase_order(aCase.getCase_order() + 1);
//                    } else {
//                        aCase.setCase_order(aCase.getCase_order() - 1);
//                    }
//
//                    if (aCase != null) {
//                        map = getTeamForNextApproveForAuto(aCase.getCustomers().getMvnoId(), aCase.getCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, caseMapper.domainToDTO(aCase, new CycleAvoidingMappingContext()));
//                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                            caseUpdateDTO.setAssignee(assignedToStaff.getId());
//                            caseUpdateDTO.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                            if (!aCase.getCaseStatus().equalsIgnoreCase("Follow Up")) {
//                                caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(aCase);
//                            }
////                            caseUpdateDTO.setStatus(CaseConstants.STATUS_ASSIGNED);
//                            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Integer.valueOf(map.get("nextTatMappingId"))).orElse(null);
//                            Teams teams = teamsRepository.findById(Long.valueOf(teamHierarchyMapping.getTeamId())).orElse(null);
//                            String nextFollowupDate = aCase.getNextFollowupDate().toString();
//                            String nextFollwupTime = aCase.getNextFollowupTime().toString();//workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), staffUser.getId(), staffUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Approved By :- " + staffUser.getUsername());
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), assignedToStaff.getId(), assignedToStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedToStaff.getUsername());
//                        //    caseUpdateService.sendAssignTicketMessege(aCase.getCustomers().getUsername(), aCase.getCustomers().getMobile(), aCase.getCustomers().getEmail(), aCase.getCustomers().getMvnoId(), aCase.getCaseNumber(), teams.getName(), nextFollowupDate, aCase.getCustomers().getUsername(), nextFollwupTime);
//                            String action = CommonConstants.WORKFLOW_MSG_ACTION.TICKET + " with ticket Number : " + " ' " + aCase.getCaseNumber() + " ' " + "for " + "customer name : " + " ' " + aCase.getCustomers().getUsername() + " '";
//                            try {
//                                caseDTO = caseUpdateService.updateEntity(caseUpdateDTO, null, false);
//                            } catch (Exception e) {
//                                ApplicationLogger.logger.error("Error in update ticket " + aCase.getCaseNumber());
//                            }
//                            Long buId = null;
//                            if(assignedToStaff!=null){
//                                if(Objects.nonNull(assignedToStaff.getBusinessUnit())){
//                                    buId  = assignedToStaff.getBusinessUnit().getId();
//                                }
//                            }
//                            if(aCase.getCustomers().getBuId()!=null){
//                                sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), aCase.getCustomers().getMvnoId(), assignedToStaff.getFullName(), action,aCase.getCustomers().getBuId());
//                            }else{
//                                sendWorkflowAssignActionMessage(assignedToStaff.getCountryCode(), assignedToStaff.getPhone(), assignedToStaff.getEmail(), aCase.getCustomers().getMvnoId(), assignedToStaff.getFullName(), action,null);
//                            }
//
//                        }
//
//                    }
//                    break;
//                }
//
//
//
//            }
//            if (eventName.equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                if (assignedToStaff.getParentStaffId() != null && !CollectionUtils.isEmpty(map) && caseDTO != null && !map.get("eventId").equals("0") && !map.get("eventId").equals(null)) {
//                    tatUtils.saveOrUpdateTicketTatMatrix(caseDTO, map, assignedToStaff, false);
//                }
//            } else if (assignedToStaff.getParentStaffId() != null && !CollectionUtils.isEmpty(map)) {
//                map.put("entityId", String.valueOf(entityId));
//                tatUtils.saveOrUpdateDataForTatMatrix(map, assignedToStaff, entityId, null);
//            }
//        } catch (CustomValidationException e) {
//            genericDataDTO.setResponseMessage(e.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
//        } catch (Exception e) {
//            genericDataDTO.setResponseMessage(e.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//        }
//        return genericDataDTO;
//    }




}
