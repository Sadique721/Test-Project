package com.savbill.ticketmanagement.core.modules.utils;


import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.modules.Customers.domain.Customers;
import com.savbill.ticketmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.Matrix;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.MatrixDetails;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.ticketmanagement.core.modules.Matrix.repository.MatrixRepository;
import com.savbill.ticketmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.ticketmanagement.core.modules.Teams.domain.QueryFieldMapping;
import com.savbill.ticketmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.ticketmanagement.core.modules.Teams.domain.Teams;
import com.savbill.ticketmanagement.core.modules.Teams.repository.HierarchyRepository;
import com.savbill.ticketmanagement.core.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.ticketmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.ticketmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.ticketmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.ticketmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.ticketmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.CaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseDTO;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseUpdateDTO;
import com.savbill.ticketmanagement.core.modules.tickets.repository.*;
import com.savbill.ticketmanagement.core.modules.tickets.repository.*;
import com.savbill.ticketmanagement.core.modules.tickets.service.CaseService;
import com.savbill.ticketmanagement.kafka.KafkaConstant;
import com.savbill.ticketmanagement.kafka.KafkaMessageData;
import com.savbill.ticketmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.ticketmanagement.rabbitmq.messages.TicketAssignMessege;
import com.savbill.ticketmanagement.rabbitmq.messages.TicketPickMessageToTeam;
import com.savbill.ticketmanagement.rabbitmq.rqconstants.RMQConstants;
import com.google.gson.Gson;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class TatUtils {

    private static final Logger logger = LoggerFactory.getLogger(TatUtils.class);

    @Autowired
    private NotificationTemplateRepository templateRepository;

//    @Autowired
//    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    private TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;

    @Autowired
    private MatrixRepository matrixRepository;

    @Autowired
    private HierarchyRepository hierarchyRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;


    @Autowired
    private CustomerRepository customersRepository;


    @Autowired
    private StaffUserService staffUserService;



    @Autowired
    private TicketReasonCategoryTATMappingRepo ticketReasonCategoryTATMappingRepo;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private TicketReasonSubCategoryRepo ticketReasonSubCategoryrepo;

    @Autowired
    private TicketTatMatrixRepository ticketTatMatrixRepository;


    @Autowired
    private TicketSubCategoryTatMappingRepo ticketSubCategoryTatMappingRepo;

    @Autowired
    private TatQueryFieldMappingRepo tatQueryFieldMappingRepo;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseMapper caseMapper;

    @Autowired
    private TatAuditRepository tatAuditRepository;

    @Autowired
    private WorkFlowQueryUtils workFlowQueryUtils;


    public void sendNotificationToStaff(TatMatrixWorkFlowDetails details) {
        StaffUser staffUser = staffUserRepository.findById(details.getStaffId()).get();//staffUserService.get(details.getStaffId());
        if (details.getParentId() != null) {
            StaffUser parentUser = staffUserRepository.findById(details.getParentId()).get();//staffUserService.get(details.getParentId());


            TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(),details);

            if (details.getNotificationType() != null && details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
                Teams team = new Teams();
                String caseNumber = null;
                if (details.getTeamId() != null) {
                    team = teamsService.getById(details.getTeamId());
                }
                Optional<Case> caseObj = caseRepository.findById(Long.valueOf(details.getEntityId()));
                if (caseObj.isPresent()) {
                    caseNumber = caseObj.get().getCaseNumber();
                }
                Long buId = null;
                if(!parentUser.getBusinessUnitNameList().isEmpty()){
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }
                if(!caseObj.get().getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                    String customerName = "";
                    String caseTitle = "";
                    String casePriority ="";
                    if(caseObj.isPresent()){
                        customerName = caseObj.get().getCustomers().getUsername();
                        caseTitle = caseObj.get().getCaseTitle();
                        casePriority = caseObj.get().getPriority();
                    }
                    sendTatNotificationTypeTeam(parentUser.getUsername(), team.getName(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), caseObj.get().getMvnoId(), caseNumber, tatAudits,buId,customerName,caseTitle,casePriority);
                }
                System.out.println("---------------------Notification for Response Time Breach sent to ParentStaff for team : "+ team.getName()+"------------------------");

            } else {
                Optional<Case> caseObj = caseRepository.findById(Long.valueOf(details.getEntityId()));
                String caseNumber = "";
                String caseTitle = "";
                String team = "";
                String casePriority ="";
                String customerName = "";
                if(caseObj.isPresent()){
                    caseNumber = caseObj.get().getCaseNumber();
                    customerName =caseObj.get().getCustomers().getUsername();
                    caseTitle = caseObj.get().getCaseTitle();
                    casePriority = caseObj.get().getPriority();
                }
                if(staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()){
                    Set<Teams> teams = staffUser.getTeam();
                    team = new ArrayList<>(teams).get(0).getName().toString();
                }

                Long buId = null;
                if(!parentUser.getBusinessUnitNameList().isEmpty()){
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }
                String ticketCreatorStaffEmail = "";
                List<String> ccEmailList = new ArrayList<>();
                if(caseObj.get().getCreatedById() != null){
                    Optional<StaffUser> ticketCreatorStaff = staffUserRepository.findById(caseObj.get().getCreatedById());
                    if(ticketCreatorStaff.isPresent()) {
                        ticketCreatorStaffEmail = ticketCreatorStaff.get().getEmail();
                        ccEmailList.add(ticketCreatorStaffEmail);
                    }

                }else{
                    ccEmailList.add(staffUser.getEmail());
                }


                sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(),tatAudits,buId,caseNumber,caseTitle,casePriority,team , customerName , ccEmailList);
                System.out.println("---------------------Notification for TAT Time Breach sent to ParentStaff for staff : "+ staffUser.getUsername()+"------------------------");

            }
            details.setStartDateTime(LocalDateTime.now());
            details.setParentId(null);

            if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                try {
                    Optional<Case> caseEntity = caseRepository.findById(Long.valueOf(details.getEntityId()));
                    if (details.getNotificationType() != null && !details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
                        if (caseEntity.isPresent()) {
                            TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(caseEntity.get(), new CycleAvoidingMappingContext()));
                            if (ticketTatMatrix != null) {
                                Case case1;
                                Optional<TicketTatMatrixMapping> ticketTatMatrix1 = ticketTatMatrix.getTatMatrixMappings().stream().filter(p -> p.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                if (ticketTatMatrix1 != null && ticketTatMatrix1.isPresent()) {
                                    Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix1.get().getMtime1()));
                                    if (caseEntity.get().getPriority().equals("High")) {
                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime1()));
                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), Nextvalue);
                                    } else if (caseEntity.get().getPriority().equals("Medium")) {
                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime2()));
                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), ticketTatMatrix1.get().getMtime2().intValue());
                                    } else {
                                        details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime3()));
                                        case1 = UpdateDateTime(ticketTatMatrix1.get(), caseEntity.get(), ticketTatMatrix1.get().getMtime3().intValue());
                                    }
                                    caseRepository.save(case1);
                                    details.setMunit(ticketTatMatrix1.get().getMunit());
                                    details.setAction(ticketTatMatrix1.get().getAction());
                                    details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
                                    details.setLevel(ticketTatMatrix1.get().getLevel());
                                    details.setStaffId(staffUser.getId());
                                   // StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                                    if(parentUser.getParentStaffId()!=null){
                                        details.setParentId(parentUser.getParentStaffId());
                                    }
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }
                            } else {
                                details.setIsActive(false);
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
                        }
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                    }

                } catch (Exception ex) {
                    logger.error("Error during find caseDTO: " + ex.getMessage());
                }
            } else {
                Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
                if (matrixDetails.isPresent()) {
                    Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();

                    if (newMatrixDetails.isPresent()) {
                        details.setMtime(newMatrixDetails.get().getMtime());
                        details.setAction(newMatrixDetails.get().getAction());
                        details.setMunit(newMatrixDetails.get().getMunit());
                        details.setOrderNo(newMatrixDetails.get().getOrderNo());
                        details.setLevel(newMatrixDetails.get().getLevel());
                        //details.setStaffId(details.getParentId());
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                    }

                }
            }
        } else {
            details.setIsActive(false);
            tatMatrixWorkFlowDetailsRepo.save(details);
        }
        tatMatrixWorkFlowDetailsRepo.save(details);

    }

    public void sendTatNotification(String parentStaffPersonName, String staffPersonName, String eventName, String assigndatetime, String mobileNumber, String emailId, Integer mvnoId, TicketTatAudits tatAudits,Long buId ,String caseNumber ,  String caseTitle,String casePriority , String team,String customerName,List<String> ccEmailList) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TAT_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(mobileNumber, emailId, RMQConstants.TAT_SUCCESS, optionalTemplate.get(), parentStaffPersonName, staffPersonName, assigndatetime, eventName, mvnoId, tatAudits,buId, caseNumber, caseTitle, casePriority, team, customerName,ccEmailList);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege,TicketAssignMessege.class.getSimpleName(), KafkaConstant.TICKET_TAT_SUCCESS_MESSAGE));

                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Case updateticketTatMatrix(CaseDTO caseDTO) {

        Case updateCase = new Case();
        if (caseDTO.getCurrentAssigneeId() != null) {
            StaffUser currentAssignStaff = new StaffUser();
            currentAssignStaff = staffUserRepository.findById(caseDTO.getCurrentAssigneeId()).orElse(null);
            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
                    findByStaffIdAndEntityIdAndEventNameAndIsActive(caseDTO.getCurrentAssigneeId(), caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true);

            if (tatMatrixWorkFlowDetails != null) {
                Integer prevoiusOrderNo = Math.toIntExact(tatMatrixWorkFlowDetails.getOrderNo());
                String previousLevel = tatMatrixWorkFlowDetails.getLevel();
                TatMatrixWorkFlowDetails newTatMatrixWorkFlowDetails = new TatMatrixWorkFlowDetails();
                tatMatrixWorkFlowDetails.setIsActive(false);
                tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                //TicketReasonCategoryTATMapping mapping = ticketReasonCategoryTATMappingRepo.findById(caseDTO.getTatMappingId()).orElse(null);
                TicketTatMatrix masterTicketTat = caseService.getTicketTatMatrixFromSubReasonId(caseDTO);
                if (masterTicketTat != null) {

                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    Case newcase = caseMapper.dtoToDomain(caseDTO, new CycleAvoidingMappingContext());
                    for (int i = 0; i < tatMatrixMappings.size(); i++) {
                        if (tatMatrixMappings.get(i).getOrderNo() == prevoiusOrderNo.intValue() ) {

                            if (caseDTO.getPriority().equalsIgnoreCase("high") ) {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime1()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime1()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
                               updateCase =  caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            } else if (caseDTO.getPriority().equalsIgnoreCase("medium")) {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime2()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime2()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
                               updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            } else {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime3()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
                                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime3()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
                               updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            }
                        }
                    }
                }


//                TatMatrixWorkFlowDetails newTatMatrixWorkFlowDetails =
//                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(mapping).getOrderNumber(), mapping.getLevel(), caseDTO.getCurrentAssigneeId(),
//                                tatMatrixWorkFlowDetails.getWorkFlowId(), null, tatMatrixWorkFlowDetails.getStaffId(), LocalDateTime.now(), String.valueOf(mapping.getTime()), mapping.getTimeUnit(), mapping.getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null);
//                if(caseDTO.getPriority().equalsIgnoreCase("High")) {
//                    newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getEscalatedTime()));
//                }else if(caseDTO.getPriority().equalsIgnoreCase("Medium")) {
//                    if(mapping.getMediumTime() != null)
//                        newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getMediumTime()));
//                }else if(caseDTO.getPriority().equalsIgnoreCase("Low")) {
//                    newTatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.getTime()));
//                }
                tatMatrixWorkFlowDetailsRepo.save(newTatMatrixWorkFlowDetails);
            }
        }
        return updateCase;
    }


    public Case UpdateDateTime(TicketTatMatrixMapping tatMatrixMappings, Case newcase, Integer Nextvalue) {
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Day")) {
            newcase.setNextFollowupDate(LocalDate.now().plusDays(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Min")) {
            newcase.setNextFollowupTime(LocalTime.now().plusMinutes(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Hour")) {
            newcase.setNextFollowupTime(LocalTime.now().plusHours(Nextvalue));
        }
        return newcase;
    }


    public void changeTicketTatStatus(CaseDTO caseDTO, boolean status) {
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
                findAllByStaffIdAndEntityIdAndEventNameAndIsActive(caseDTO.getCurrentAssigneeId(), caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true);
        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetails.stream()
                    .peek(tatMatrix -> {
                        tatMatrix.setIsActive(false);
                        tatMatrix.setIsOverDueReminder(false);
                    })
                    .collect(Collectors.toList());
            tatMatrixWorkFlowDetailsRepo.saveAll(tatMatrixWorkFlowDetails);
        }
    }

    public void changeTicketTatAssignee(CaseDTO caseDTO, StaffUser assignedStaff, boolean isNotificationTypeTeam, Boolean isPickeddUp) {
        try {
            Map<String, String> map = new HashMap<>();
            Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(caseDTO.getTeamHierarchyMappingId().intValue());
            if (teamHierarchyMapping.isPresent()) {
                map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
                map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.CASE);
                map.put("eventId", caseDTO.getCaseId().toString());
                map.put("teamId", teamHierarchyMapping.get().getTeamId().toString());
                if (isPickeddUp)
                    map.put("fromPickedUp", "true");
                else
                    map.put("fromPickedUp", "false");
                saveOrUpdateTicketTatMatrix(caseDTO, map, assignedStaff, isNotificationTypeTeam);
                // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
            }
        } catch (Exception ex) {
            logger.error("Exception at change assignee on ticket tat: " + ex.getMessage());
        }
    }

    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, CaseDTO caseDTO) {
        List<QueryFieldMapping> queryFieldMappingList = new ArrayList<>();
        for (TatQueryFieldMapping queryFieldMapping : tatQueryFieldMappingList) {
            QueryFieldMapping mapping = new QueryFieldMapping();
            mapping.setQueryOperator(queryFieldMapping.getQueryOperator());
            mapping.setQueryCondition(queryFieldMapping.getQueryCondition());
            mapping.setQueryValue(queryFieldMapping.getQueryValue());
            mapping.setQueryField(queryFieldMapping.getQueryField());
            queryFieldMappingList.add(mapping);
        }
        if (CollectionUtils.isEmpty(queryFieldMappingList))
            return false;

        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);

    }

    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, CaseUpdateDTO caseDTO) {
        List<QueryFieldMapping> queryFieldMappingList = new ArrayList<>();
        for (TatQueryFieldMapping queryFieldMapping : tatQueryFieldMappingList) {
            QueryFieldMapping mapping = new QueryFieldMapping();
            mapping.setQueryOperator(queryFieldMapping.getQueryOperator());
            mapping.setQueryCondition(queryFieldMapping.getQueryCondition());
            mapping.setQueryValue(queryFieldMapping.getQueryValue());
            mapping.setQueryField(queryFieldMapping.getQueryField());
            queryFieldMappingList.add(mapping);
        }
        if (CollectionUtils.isEmpty(queryFieldMappingList))
            return false;

        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);

    }

    public void saveOrUpdateTicketTatMatrix(CaseDTO caseDTO, Map<String, String> map, StaffUser assignedStaff, boolean isNotificationTypeTeam) {
        if (caseDTO.getReasonSubCategoryId() != null) {
            Optional<TicketReasonSubCategory> ticketSubReasonCategory = ticketReasonSubCategoryrepo.findById(caseDTO.getReasonSubCategoryId());
            if (ticketSubReasonCategory.isPresent()) {
                if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList())) {
                    List<TicketSubCategoryTatMapping> ticketSubCategoryTatMappings = ticketSubReasonCategory.get().getTicketSubCategoryTatMappingList();
                    for (TicketSubCategoryTatMapping ticketSubCategoryTatMapping : ticketSubCategoryTatMappings) {
                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(ticketSubCategoryTatMapping.getId().intValue()));
                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                            //If query not matched then skip
                            if (!checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                                continue;
                        }
                        if (!isNotificationTypeTeam) {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(map.get("workFlowId")), Integer.valueOf(map.get("eventId")), map.get("eventName"), true);
                            if (!CollectionUtils.isEmpty(preTatMatrixWorkFlowDetails) && !isNotificationTypeTeam) {
                                preTatMatrixWorkFlowDetails = preTatMatrixWorkFlowDetails.stream()
                                        .peek(tatMatrixWorkFlowDetails -> {
                                            tatMatrixWorkFlowDetails.setIsActive(false);
                                            tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                                        })
                                        .collect(Collectors.toList());
                                tatMatrixWorkFlowDetailsRepo.saveAll(preTatMatrixWorkFlowDetails);
                            }
                        }
                        else {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowList = tatMatrixWorkFlowDetailsRepo.findAllByWorkFlowIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(map.get("workFlowId")), Integer.valueOf(map.get("eventId")), map.get("eventName"), true);
                            if (map.get("fromPickedUp").equals("true")) {
                                for (int i = 0; i < preTatMatrixWorkFlowList.size(); i++) {
                                    if (preTatMatrixWorkFlowList.get(i) != null && isNotificationTypeTeam) {
                                        preTatMatrixWorkFlowList.get(i).setIsActive(false);
                                        preTatMatrixWorkFlowList.get(i).setIsOverDueReminder(false);
                                        tatMatrixWorkFlowDetailsRepo.save(preTatMatrixWorkFlowList.get(i));
                                    }
                                }
                            }

                        }
                        TicketTatMatrix masterTicketTat = ticketSubCategoryTatMapping.getTicketTatMatrix();
                        List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                        if (!isNotificationTypeTeam) {
                            if (!CollectionUtils.isEmpty(tatMatrixMappings)) {
                                Optional<TicketTatMatrixMapping> mapping = tatMatrixMappings.stream().filter(ticketTatMatrixMapping -> ticketTatMatrixMapping.getOrderNo().equals(1l)).findFirst();
                                if (mapping.isPresent()) {
                                    if (mapping != null && assignedStaff.getParentStaffId() != null) {
                                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                                new TatMatrixWorkFlowDetails(mapping.get().getOrderNo(), mapping.get().getLevel(), assignedStaff.getId(),
                                                        Long.valueOf(map.get("workFlowId")), null,
                                                        assignedStaff.getParentStaffId(), LocalDateTime.now(),
                                                        String.valueOf(mapping.get().getMtime3()), mapping.get().getMunit(), mapping.get().getAction(), true, null,
                                                        caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")), CommonConstants.NOTIFICATION_TYPE_STAFF,
                                                        null, true);
                                        if (caseDTO.getPriority().equalsIgnoreCase("High")) {
                                            tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime1()));
                                        } else if (caseDTO.getPriority().equalsIgnoreCase("Medium")) {
                                            if (mapping.get().getMunit() != null)
                                                tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime2()));
                                        } else if (caseDTO.getPriority().equalsIgnoreCase("Low")) {
                                            tatMatrixWorkFlowDetails.setMtime(String.valueOf(mapping.get().getMtime3()));
                                        }
                                        tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                        break;
                                    }
                                }
                            }
                        }
                        else if (isNotificationTypeTeam) {
                            if (masterTicketTat != null && assignedStaff.getParentStaffId()!= null) {
                            //boolean flag=  caseService.updateTicketLevel(caseDTO,  map,masterTicketTat);
                           // if(!flag) {
//                                TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
//                                        new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
//                                                Long.valueOf(map.get("workFlowId")), null,
//                                                assignedStaff.getStaffUserparent().getId(), LocalDateTime.now(),
//                                                String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), masterTicketTat.getTatMatrixMappings().get(0).getAction(), true, null,
//                                                caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
//                                                CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
//                                tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                if(!caseDTO.getCaseStatus().equalsIgnoreCase("Follow Up")){
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
                                                    Long.valueOf(map.get("workFlowId")), null,
                                                    assignedStaff.getParentStaffId(), LocalDateTime.now(),
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")),true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                }else{
                                    LocalDateTime followUpDateTime = LocalDateTime.of(caseDTO.getNextFollowupDate(),caseDTO.getNextFollowupTime());
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(),
                                                    Long.valueOf(map.get("workFlowId")), null,
                                                    assignedStaff.getParentStaffId(), followUpDateTime,
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")),true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                }
                                break;
                            //}
                            }
                        }

                    }
                }
            }
        }
    }


    public void saveOrUpdateDataForTatMatrix(Map<String, String> map, StaffUser assignedToStaff, Integer entityId, Long preTatMatrixId) {
        try {
            if (!CollectionUtils.isEmpty(map)) {
                if (map.get("tat_id") != null && map.get("tat_id") != "null") {
                    TeamHierarchyMapping prevTeamHierarchyMapping = teamHierarchyMappingRepo.findByOrderNumberAndHierarchyId(Integer.valueOf(map.get("orderNo")) - 1, Integer.valueOf(map.get("workFlowId")));
                    Optional<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowDetails = null;//Optional.of(new TatMatrixWorkFlowDetails());
                    Long preOrderNo = 0l;
                    if (prevTeamHierarchyMapping != null) {
                        if (preTatMatrixId != null) {
                            preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findById(preTatMatrixId);
                        } else {
                            preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByWorkFlowIdAndCurrentTeamHeirarchyMappingIdAndIsActive(Long.valueOf(prevTeamHierarchyMapping.getHierarchyId()), prevTeamHierarchyMapping.getId(), true);
                        }
                    }
                    if (preTatMatrixWorkFlowDetails != null && preTatMatrixWorkFlowDetails.isPresent()) {
                        preTatMatrixWorkFlowDetails.get().setIsActive(false);
                        preTatMatrixWorkFlowDetails.get().setIsOverDueReminder(false);
                        preOrderNo = preTatMatrixWorkFlowDetails.get().getOrderNo();
                        tatMatrixWorkFlowDetailsRepo.save(preTatMatrixWorkFlowDetails.get());
                    }
                    inActivateTatWorkflowMapping(map);
                    Optional<Matrix> matrixDetails = matrixRepository.findById(Long.valueOf(map.get("tat_id")));
                    if (matrixDetails.isPresent()) {
                        Optional<MatrixDetails> details = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted()).findFirst();

                        if (details.isPresent()) {
                            TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                    new TatMatrixWorkFlowDetails(details.get().getOrderNo(), details.get().getLevel(), assignedToStaff.getId(),
                                            Long.valueOf(map.get("workFlowId")), matrixDetails.get().getId(),
                                            assignedToStaff.getParentStaffId(), LocalDateTime.now(),
                                            details.get().getMtime(), details.get().getMunit(), details.get().getAction(), true, map.get("nextTatMappingId") != null ? Integer.valueOf(map.get("nextTatMappingId")) : null,
                                            entityId, map.get("eventName"), map.get("eventId") != null ? Integer.valueOf(map.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null,true);
                            tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);

                        } else {
                            logger.error("Active tat matrix details not found for id: " + matrixDetails.get().getId());
                        }
                    } else {
                        logger.error("Tat matrix not found for id: " + matrixDetails.get().getId());
                    }
                } else {
                    inActivateTatWorkflowMapping(map);
                }
            }
        } catch (Exception ex) {
            logger.error("error while saving tat matrix data: " + ex.getMessage());
        }
    }

    public void inActivateTatWorkflowMapping(Map<String, String> map) {
        if (map.containsKey("eventId") && map.containsKey("entityId")) {
            QTatMatrixWorkFlowDetails qTatMatrixWorkFlowDetails = QTatMatrixWorkFlowDetails.tatMatrixWorkFlowDetails;
            BooleanExpression expression = qTatMatrixWorkFlowDetails.isNotNull().and(qTatMatrixWorkFlowDetails.isActive.eq(true))
                    .and(qTatMatrixWorkFlowDetails.eventId.eq(Integer.valueOf(map.get("eventId"))))
                    .and(qTatMatrixWorkFlowDetails.entityId.eq(Integer.valueOf(map.get("entityId"))));
            List<TatMatrixWorkFlowDetails> details = (List<TatMatrixWorkFlowDetails>) tatMatrixWorkFlowDetailsRepo.findAll(expression);
            if (!CollectionUtils.isEmpty(details)) {
                details.forEach(tatMatrixWorkFlowDetails -> {
                    tatMatrixWorkFlowDetails.setIsActive(false);
                    tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                });
                tatMatrixWorkFlowDetailsRepo.saveAll(details);
            }
        }
    }

    public void assignToNextApprovalStaff(TatMatrixWorkFlowDetails details) {
        try {
            System.out.println("------------TAT Notification and Reassign Started ------------------------");
            Customers customersCaf = null;
//            PostpaidPlan plan = null;
//            CreditDocument creditDocument = null;
            Map<String, String> map;
            StaffUser staffUser = staffUserRepository.findById(details.getStaffId()).get();

            switch (details.getEventName()) {

                case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
                    Case aCase = caseService.getRepository().findById(Long.valueOf(details.getEntityId())).orElse(null);
                    if (staffUser.getParentStaffId() != null) {
                        StaffUser currentAssignee = new StaffUser();
                        currentAssignee = staffUserRepository.findById(staffUser.getParentStaffId()).orElse(null);
                        aCase.setCurrentAssignee(currentAssignee);
                        caseRepository.save(aCase);
                        //Audit for TAT
                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);

                        System.out.println("------------------------TAT Reassign done for Action - Both--------------------------");
                        details.setStartDateTime(LocalDateTime.now());

                        // send notification if action is "BOTH"
                        if (details.getAction().equalsIgnoreCase("both")) {
                            if (details.getParentId() != null) {
                                String caseNumber ="";
                                String caseTitle ="";
                                String team = "";
                                String casePriority ="";
                                String customerName ="";
                                if(aCase != null){
                                    caseNumber = aCase.getCaseNumber();
                                    caseTitle = aCase.getCaseTitle();
                                    casePriority = aCase.getPriority();
                                    customerName = aCase.getCustomers().getUsername();
                                    if(staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()){
                                        Set<Teams> teams = staffUser.getTeam();
                                        team = new ArrayList<>(teams).get(0).getName().toString();
                                    }
                                }

                                StaffUser parentUser = staffUserRepository.findById(staffUser.getParentStaffId()).get();
                                Long buId = null;
                                if(parentUser.getBusinessUnitNameList() != null && !parentUser.getBusinessUnitNameList().isEmpty()){
                                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                                }
                                String ticketCreatorStaffEmail = "";
                                List<String> ccEmailList = new ArrayList<>();
                                if(aCase.getCreatedById() != null){
                                    Optional<StaffUser> ticketCreatorStaff = staffUserRepository.findById(aCase.getCreatedById());
                                    if(ticketCreatorStaff.isPresent()) {
                                        ticketCreatorStaffEmail = ticketCreatorStaff.get().getEmail();
                                        ccEmailList.add(ticketCreatorStaffEmail);
                                    }
                                }
                                ccEmailList.add(staffUser.getEmail());
                                sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), aCase.getMvnoId(), tatAudits,buId, caseNumber , caseTitle,casePriority , team,customerName,ccEmailList);

                                System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
                            }
                        }
                        details.setParentId(null);
                        //changing level to next level

                        if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                            try {
                                // Case caseEntity = caseRepository.findById(Long.valueOf(details.getEntityId())).orElse(null);
                                if (details.getNotificationType() != null && !details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
                                    if (aCase != null) {
                                        TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
                                        if (ticketTatMatrix != null) {
                                            //Case case1;
                                            Optional<TicketTatMatrixMapping> ticketTatMatrix1 = ticketTatMatrix.getTatMatrixMappings().stream().filter(p -> p.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                            if (ticketTatMatrix1 != null && ticketTatMatrix1.isPresent()) {
                                                Integer Nextvalue = Integer.parseInt(String.valueOf(ticketTatMatrix1.get().getMtime1()));
                                                if (aCase.getPriority().equals("High")) {
                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime1()));
                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
                                                } else if (aCase.getPriority().equals("Medium")) {
                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime2()));
                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
                                                } else {
                                                    details.setMtime(String.valueOf(ticketTatMatrix1.get().getMtime3()));
                                                    aCase = UpdateDateTime(ticketTatMatrix1.get(), aCase, Nextvalue);
                                                }
                                                details.setMunit(ticketTatMatrix1.get().getMunit());

                                                //Setting followup time for next level
                                                if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Min")) {
                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate());
                                                    aCase.setNextFollowupTime(LocalTime.now().plusMinutes(Long.parseLong(details.getMtime())));
                                                } else if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Hours")) {
                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate());
                                                    aCase.setNextFollowupTime(LocalTime.now().plusHours(Long.parseLong(details.getMtime())));

                                                } else if (ticketTatMatrix1.get().getMunit().equalsIgnoreCase("Day")) {
                                                    aCase.setNextFollowupDate(LocalDateTime.now().toLocalDate().plusDays(Long.parseLong(details.getMtime())));
                                                    aCase.setNextFollowupTime(LocalTime.now());
                                                }
//                                                caseRepository.save(case1);
                                                caseRepository.save(aCase);
                                                details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
                                                details.setLevel(ticketTatMatrix1.get().getLevel());
                                                details.setAction(ticketTatMatrix1.get().getAction());
                                                details.setStaffId(staffUser.getParentStaffId());
                                                details.setAction(ticketTatMatrix1.get().getAction());
                                                StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                                                details.setParentId(newStaffuser.getParentStaffId());
                                            } else {
                                                details.setIsActive(false);
                                                tatMatrixWorkFlowDetailsRepo.save(details);
                                            }
                                        } else {
                                            details.setIsActive(false);
                                            tatMatrixWorkFlowDetailsRepo.save(details);
                                        }
                                    }
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }

                                tatMatrixWorkFlowDetailsRepo.save(details);

                            } catch (Exception ex) {
                                logger.error("Error during find caseDTO: " + ex.getMessage());
                            }
                        } else {
                            Optional<Matrix> matrixDetails = matrixRepository.findById(details.getTatMatrixId());
                            if (matrixDetails.isPresent()) {
                                Optional<MatrixDetails> newMatrixDetails = matrixDetails.get().getMatrixDetailsList().stream().filter(dtl -> !dtl.getIsDeleted() && dtl.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();

                                if (newMatrixDetails.isPresent()) {
                                    details.setMtime(newMatrixDetails.get().getMtime());
                                    details.setMunit(newMatrixDetails.get().getMunit());
                                    details.setOrderNo(newMatrixDetails.get().getOrderNo());
                                    details.setLevel(newMatrixDetails.get().getLevel());
                                    //details.setStaffId(details.getParentId());
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
                        }
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                    }
                    break;
                }
            }
//            if(!CollectionUtils.isEmpty(map) && map.containsKey("assignableStaff"))
//                hierarchyService.assignFromStaffList(map.get("assignableStaff").get(0).getId(), details.getEventName(), details.getEntityId(), true);
        } catch (Exception ex) {
            logger.error("Error while assign next staff by Tat schedualar: " + ex.getMessage());
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public void sendTatNotificationTypeTeam(String parentStaffPersonName, String teamName, String eventName, String assigndatetime, String mobileNumber, String emailId, Integer mvnoId, String ticketNumber, TicketTatAudits tatAudits,Long buId,String customerName , String caseTitle , String casePriority) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TAT_SEND_PARENT_TO_TEAM);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {

                    TicketPickMessageToTeam ticketAssignMessege = new TicketPickMessageToTeam(mobileNumber, emailId, RMQConstants.TAT_NO_RESPONSE_TAKEN, optionalTemplate.get(), parentStaffPersonName, assigndatetime, eventName, mvnoId, teamName, ticketNumber,tatAudits,buId, customerName, caseTitle, casePriority);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege,TicketPickMessageToTeam.class.getSimpleName(),RMQConstants.TAT_SEND_PARENT_TO_TEAM));
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public TicketTatAudits saveTatDetails(Integer aCaseId, TatMatrixWorkFlowDetails details){
        //Fetching the details of case

        Case ticketCase = new Case();
        ticketCase = caseRepository.findById(aCaseId.longValue()).orElse(null);

        TicketTatAudits tatAudits = new TicketTatAudits();

        if(!details.getAction().equalsIgnoreCase(CommonConstants.TICKET_ACTION.REASSIGN)){
            if(ticketCase!=null){
                tatAudits.setCaseId(ticketCase.getCaseId().intValue());
                tatAudits.setSlaTime(ticketCase.getCaseSlaTime());
                tatAudits.setSlaUnit(ticketCase.getCaseSlaUnit());
                tatAudits.setCaseStatus(ticketCase.getCaseStatus());

                if(details!=null){
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if(details.getNotificationType().equalsIgnoreCase("team")){
                        tatAudits.setNotificationFor("Response Time Breach");
                    }else if(details.getNotificationType().equalsIgnoreCase("staff")){
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }
        }else{
            if(ticketCase!=null){
                tatAudits.setCaseId(ticketCase.getCaseId().intValue());
                tatAudits.setSlaTime(ticketCase.getCaseSlaTime());
                tatAudits.setSlaUnit(ticketCase.getCaseSlaUnit());
                tatAudits.setCaseStatus(ticketCase.getCaseStatus());

                if(details!=null){
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if(details.getNotificationType().equalsIgnoreCase("team")){
                        tatAudits.setNotificationFor("Response Time Breach");
                    }else if(details.getNotificationType().equalsIgnoreCase("staff")){
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }
            TicketTatAudits savedTatAudit = tatAuditRepository.save(tatAudits);
        }

        return  tatAudits;

    }


}
