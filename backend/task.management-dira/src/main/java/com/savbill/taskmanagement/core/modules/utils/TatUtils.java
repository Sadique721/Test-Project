package com.savbill.taskmanagement.core.modules.utils;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Matrix.domain.Matrix;
import com.savbill.taskmanagement.core.modules.Matrix.domain.MatrixDetails;
import com.savbill.taskmanagement.core.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.repository.MatrixRepository;
import com.savbill.taskmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.taskmanagement.core.modules.Teams.domain.QueryFieldMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.HierarchyRepository;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrixMapping;
import com.savbill.taskmanagement.core.modules.TicketTatMatrix.Repository.TicketTatMatrixRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseUpdateDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.taskmanagement.kafka.KafkaConstant;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.TicketAssignMessege;
import com.savbill.taskmanagement.rabbitmq.messages.TicketPickMessageToTeam;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
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
    private CaseRepository caseRepository;

//    @Autowired
//    private TicketReasonSubCategoryRepo ticketReasonSubCategoryrepo;

    @Autowired
    private CaseCategoryRepository caseCategoryRepository;

    @Autowired
    private TicketTatMatrixRepository ticketTatMatrixRepository;


    @Autowired
    private CaseCategoryTatMappingRepo caseCategoryTatMappingRepo;

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
    @Autowired
    private WorkflowAuditService workflowAuditService;
    @Autowired
    HierarchyService hierarchyService;


    public void sendNotificationToStaff(TatMatrixWorkFlowDetails details) {
        String SUBMODULE = getModuleNameForLog() + " [sendNotificationToStaff] ";
        logger.debug("calaculate date TAT matrix workflow for Id: {}; Module: {}", details.getId(), SUBMODULE);
        StaffUser staffUser = staffUserRepository.findById(details.getStaffId()).get();//staffUserService.get(details.getStaffId());
        if (details.getParentId() != null) {
            StaffUser parentUser = staffUserRepository.findById(details.getParentId()).get();//staffUserService.get(details.getParentId());

            TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
            logger.info("TAT details saved for case: {}; Module: {}", tatAudits.getCaseId(), SUBMODULE);
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
                if (!parentUser.getBusinessUnitNameList().isEmpty()) {
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }
                if (!caseObj.get().getCaseStatus().equalsIgnoreCase("Raise and Close")) {
                    String staffName = "";
                    String caseTitle = "";
                    String casePriority = "";
                    if (caseObj.isPresent()) {
                        //staffName = caseObj.get().getStaffUser().getUsername();
                        caseTitle = caseObj.get().getCaseTitle();
                        casePriority = caseObj.get().getPriority();
                    }
                    sendTatNotificationTypeTeam(parentUser.getUsername(), team.getName(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), caseObj.get().getMvnoId(), caseNumber, tatAudits, buId, staffName, caseTitle, casePriority);
                }
                System.out.println("---------------------Notification for Response Time Breach sent to ParentStaff for team : " + team.getName() + "------------------------");
                logger.info("Notification for Response Time Breach sent to Parent Staff for Team: {}; Module: {}", team.getName(), SUBMODULE);
            }
            else {
                Optional<Case> caseObj = caseRepository.findById(Long.valueOf(details.getEntityId()));
                String caseNumber = "";
                String caseTitle = "";
                String team = "";
                String casePriority = "";
                String staffName = "";
                if (caseObj.isPresent()) {
                    caseNumber = caseObj.get().getCaseNumber();
                    //staffName =caseObj.get().getStaffUser().getUsername();
                    caseTitle = caseObj.get().getCaseTitle();
                    casePriority = caseObj.get().getPriority();
                }
                if (staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()) {
                    Set<Teams> teams = staffUser.getTeam();
                    team = new ArrayList<>(teams).get(0).getName().toString();
                }

                Long buId = null;
                if (!parentUser.getBusinessUnitNameList().isEmpty()) {
                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                }
                String ticketCreatorStaffEmail = "";
                List<String> ccEmailList = new ArrayList<>();
                if (caseObj.get().getCreatedById() != null) {
                    Optional<StaffUser> ticketCreatorStaff = staffUserRepository.findById(caseObj.get().getCreatedById());
                    if (ticketCreatorStaff.isPresent()) {
                        ticketCreatorStaffEmail = ticketCreatorStaff.get().getEmail();
                        ccEmailList.add(ticketCreatorStaffEmail);
                        logger.debug("Added Ticket Creator Email to CC List: {}; Module: {}", ticketCreatorStaffEmail, SUBMODULE);
                    }

                } else {
                    ccEmailList.add(staffUser.getEmail());
                    logger.debug("Added Staff Email to CC List: {}; Module: {}", staffUser.getEmail(), SUBMODULE);
                }


                sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), parentUser.getMvnoId(), tatAudits, buId, caseNumber, caseTitle, casePriority, team, staffName, ccEmailList);
                System.out.println("---------------------Notification for TAT Time Breach sent to ParentStaff for staff : " + staffUser.getUsername() + "------------------------");
                logger.debug("Notification for TAT Time Breach sent to Parent Staff for Staff: {}; Module: {}", staffUser.getUsername(), SUBMODULE);
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
                                    logger.info("ticket Tat Matrix updated and saved for case: {} with munit: {} ; module: {}", case1.getCaseNumber(),ticketTatMatrix1.get().getMunit(),SUBMODULE);
                                    details.setMunit(ticketTatMatrix1.get().getMunit());
                                    details.setAction(ticketTatMatrix1.get().getAction());
                                    details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
                                    details.setLevel(ticketTatMatrix1.get().getLevel());
                                    details.setStaffId(staffUser.getId());
                                    // StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                                    if (parentUser.getParentStaffId() != null) {
                                        details.setParentId(parentUser.getParentStaffId());
                                    }
                                } else {
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                    logger.debug("No next workflow level found; setting workflow as inactive for Id: {}; Module: {}", details.getId(),SUBMODULE);
                                }
                            } else {
                                details.setIsActive(false);
                                tatMatrixWorkFlowDetailsRepo.save(details);
                                logger.debug("No TAT Matrix found; setting workflow as inactive for Id: {}; Module: {}", details.getId(),SUBMODULE);
                            }
                        }
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                        logger.debug("No TAT Matrix found; setting workflow as inactive for Id: {}; Module: {}", details.getId(),SUBMODULE);
                    }

                } catch (Exception ex) {
                    logger.error("Error during find caseDTO: " + ex.getMessage());
                    logger.error("Error during case processing: {}; Module: {}", ex.getMessage(), SUBMODULE);
                    ex.printStackTrace();
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
                        logger.debug("Details updated with next Matrix level; Module: {}", SUBMODULE);
                    } else {
                        details.setIsActive(false);
                        tatMatrixWorkFlowDetailsRepo.save(details);
                        logger.debug("No next Matrix level found; Deactivating details; Module: {}", SUBMODULE);
                    }

                }
            }
        } else {
            details.setIsActive(false);
            tatMatrixWorkFlowDetailsRepo.save(details);
            logger.debug("Parent ID not found; setting workflow as inactive for Id: {}; Module: {}", details.getId(),SUBMODULE);
        }
        tatMatrixWorkFlowDetailsRepo.save(details);
        logger.info("Final TAT Matrix Workflow Details saved: {}; Module: {}", details, SUBMODULE);

    }

    public void sendTatNotification(String parentStaffPersonName, String staffPersonName, String eventName, String assigndatetime, String mobileNumber, String emailId, Integer mvnoId, TicketTatAudits tatAudits, Long buId, String caseNumber, String caseTitle, String casePriority, String team, String customerName, List<String> ccEmailList) {
        String SUBMODULE = getModuleNameForLog() + " [sendTatNotification] ";
        try {
            logger.info("Sending TAT Notification for Case: {}; Team: {}; module: {}", caseNumber, team,SUBMODULE);
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TAT_SUCCESS);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketAssignMessege ticketAssignMessege = new TicketAssignMessege(mobileNumber, emailId, RMQConstants.TAT_SUCCESS, optionalTemplate.get(), parentStaffPersonName, staffPersonName, assigndatetime, eventName, mvnoId, tatAudits, buId, caseNumber, caseTitle, casePriority, team, customerName, ccEmailList);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE);
                    System.out.println("***********************TAT Notification successfully sent******************"+ticketAssignMessege);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege, TicketAssignMessege.class.getSimpleName(), KafkaConstant.TASK_TAT_SUCCESS_MESSAGE));
                    logger.info("TAT Notification successfully sent for Case: {}; Team: {}; module: {}", caseNumber, team,SUBMODULE);
                }
            } else {
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                logger.error("TAT Template  not found for sending Tat notification for Case: {}; module: {};",caseNumber,SUBMODULE);
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            logger.error("Error occurred while sending TAT Notification for Case: {}; Module: {}; Error Message : {};",caseNumber, SUBMODULE, e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Case updateticketTatMatrix(CaseDTO caseDTO) {
        String SUBMODULE=getModuleNameForLog()+"[ updateTicketTatMatrix ]";
        logger.debug("Updating Ticket TAT Matrix for updateEntity for Case: {}; module: {}", caseDTO.getCaseNumber(), SUBMODULE);
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
                logger.debug("Setting  TAT Matrix Workflow Details for Case: {}", caseDTO.getCaseNumber());
                TicketTatMatrix masterTicketTat = caseService.getTicketTatMatrixFromSubReasonId(caseDTO);
                if (masterTicketTat != null) {

                    List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                    Case newcase = caseMapper.dtoToDomain(caseDTO, new CycleAvoidingMappingContext());
                    for (int i = 0; i < tatMatrixMappings.size(); i++) {
                        if (tatMatrixMappings.get(i).getOrderNo() == prevoiusOrderNo.intValue()) {

                            if (caseDTO.getPriority().equalsIgnoreCase("high")) {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime1()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId()
                                                , null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime1()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
                                updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            } else if (caseDTO.getPriority().equalsIgnoreCase("medium")) {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime2()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
                                                null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime2()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
                                updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            } else {
                                Integer Nextvalue = Integer.parseInt(String.valueOf(tatMatrixMappings.get(i).getMtime3()));
                                newTatMatrixWorkFlowDetails =
                                        new TatMatrixWorkFlowDetails(Objects.requireNonNull(tatMatrixMappings.get(i).getOrderNo()), tatMatrixMappings.get(i).getLevel(), caseDTO.getCurrentAssigneeId(),
                                                null, currentAssignStaff.getParentStaffId(), LocalDateTime.now(), String.valueOf(tatMatrixMappings.get(i).getMtime3()), tatMatrixMappings.get(i).getMunit(), tatMatrixMappings.get(i).getAction(), true, null, caseDTO.getCaseId().intValue(), tatMatrixWorkFlowDetails.getEventName(), tatMatrixWorkFlowDetails.getEventId(), CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
                                updateCase = caseRepository.save(UpdateDateTime(tatMatrixMappings.get(i), newcase, Nextvalue));
                            }
                        }
                    }
                    logger.info("Ticket Tat Matrix updated for Case: {} with priority: {} ; module: {}", caseDTO.getCaseNumber(),caseDTO.getPriority(),SUBMODULE);
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
                logger.info("New TAT Matrix Workflow Details saved for Case: {}; module: {}", caseDTO.getCaseNumber(),SUBMODULE);
            }
        }
        logger.info("Ticket Tat Matrix updated successfully  for Case: {}; Module: {}", caseDTO.getCaseNumber(), SUBMODULE);
        return updateCase;
    }


    public Case UpdateDateTime(TicketTatMatrixMapping tatMatrixMappings, Case newcase, Integer Nextvalue) {
        String submodule = getModuleNameForLog() + "[ updateDateTime ]";
        logger.debug("Updating follow-up date/time for Case ID: {}; Module: {}", newcase.getCaseId(), submodule);
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Day")) {
            newcase.setNextFollowupDate(LocalDate.now().plusDays(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Min")) {
            newcase.setNextFollowupTime(LocalTime.now().plusMinutes(Nextvalue));
        }
        if (tatMatrixMappings.getMunit().equalsIgnoreCase("Hour")) {
            newcase.setNextFollowupTime(LocalTime.now().plusHours(Nextvalue));
        }
        logger.debug("Next follow-up date/time updated successfully for Case ID: {}; Module: {}", newcase.getCaseId(), submodule);
        return newcase;
    }


    public void changeTicketTatStatus(CaseDTO caseDTO, boolean status) {
        String SUBMODULE = getModuleNameForLog()+"[ changeTicketTatStatus ]";
        logger.debug("change Ticket Tat Status for Case : {}; Module : {};",caseDTO.getCaseNumber(),SUBMODULE);
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.
                findAllByStaffIdAndEntityIdAndEventNameAndIsActive(caseDTO.getCurrentAssigneeId(), caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true);
        logger.debug("Found All TatMatrixWorkFlowDetails By StaffId: {} And EntityId: {} And EventName: {} And IsActive: {}; module: {};",caseDTO.getCurrentAssigneeId(),caseDTO.getCaseId().intValue(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, true,SUBMODULE);
        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetails.stream()
                    .peek(tatMatrix -> {
                        tatMatrix.setIsActive(false);
                        tatMatrix.setIsOverDueReminder(false);
                    })
                    .collect(Collectors.toList());
            tatMatrixWorkFlowDetailsRepo.saveAll(tatMatrixWorkFlowDetails);
            logger.info("Successfully savedAll tatMatrixWorkFlowDetails & Changed Ticket Tat Status for Case : {}; Module : {};",caseDTO.getCaseNumber(),SUBMODULE);
        }
    }

    public void changeTicketTatAssignee(CaseDTO caseDTO, StaffUser assignedStaff, boolean isNotificationTypeTeam, Boolean isPickeddUp) {
        String SUBMODULE = getModuleNameForLog()+"[ changeTicketTatAssignee ]";
        try {
            Map<String, String> map = new HashMap<>();
            //Optional<TeamHierarchyMapping> teamHierarchyMapping = teamHierarchyMappingRepo.findById(caseDTO.getTeamHierarchyMappingId().intValue());
            //if (teamHierarchyMapping.isPresent()) {
            //map.put("workFlowId", teamHierarchyMapping.get().getHierarchyId().toString());
            logger.debug("change Ticket Tat Assignee for Case : {}; assignedStaff : {}; Module : {};",caseDTO.getCaseNumber(), assignedStaff.getUsername(),SUBMODULE);
            map.put("eventName", CommonConstants.WORKFLOW_EVENT_NAME.CASE);
            map.put("eventId", caseDTO.getCaseId().toString());
            map.put("teamId", String.valueOf(caseDTO.getTeamId()));
            if (isPickeddUp)
                map.put("fromPickedUp", "true");
            else
                map.put("fromPickedUp", "false");
            saveOrUpdateTicketTatMatrix(caseDTO, map, assignedStaff, isNotificationTypeTeam);
            logger.info("Successfully changed Ticket Tat Assignee for Case : {}; assignedStaff : {}; Module : {};",caseDTO.getCaseNumber(), assignedStaff.getUsername(),SUBMODULE);
            // caseService.updateFollowUpDateAndTimeForTicketAfterPickedUp(caseMapper.dtoToDomain(caseDTO,new CycleAvoidingMappingContext()));
            // }
        } catch (Exception ex) {
            logger.error("Exception while changing Ticket Tat Assignee for Case: {}; Module: {}; Error Message: {}",caseDTO.getCaseNumber(), SUBMODULE, ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, CaseDTO caseDTO) {
        String SUBMODULE = getModuleNameForLog()+"[ checkTicketTatCondition ]";
        logger.debug("checking Ticket Tat Condition for Case : {}; Module : {};",caseDTO.getCaseNumber(),SUBMODULE);
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

        logger.debug("Ticket Tat condition result checked  for Case: {}; module: {}", caseDTO.getCaseNumber(), SUBMODULE);
        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);

    }

    public Boolean checkTicketTatCondition(List<TatQueryFieldMapping> tatQueryFieldMappingList, CaseUpdateDTO caseDTO) {
        String SUBMODULE = getModuleNameForLog()+"[ checkTicketTatCondition ]";
        logger.debug("checking Ticket Tat Condition to get Ticket TatMatrix For UpdateCase : {}; Module : {};",caseDTO.getCaseCategoryId(),SUBMODULE);
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

        logger.debug("Ticket Tat condition result checked to get Ticket TatMatrix For UpdateCase: {}; module: {}", caseDTO.getCaseCategoryId(), SUBMODULE);
        return workFlowQueryUtils.checkCondition(queryFieldMappingList, CommonConstants.WORKFLOW_EVENT_NAME.CASE, caseDTO);

    }

    public void saveOrUpdateTicketTatMatrix(CaseDTO caseDTO, Map<String, String> map, StaffUser assignedStaff, boolean isNotificationTypeTeam) {
        String SUBMODULE = getModuleNameForLog()+"[ saveOrUpdateTicketTatMatrix ]";
        logger.debug("saving Or Updating Ticket Tat Matrix to change Ticket Tat Assignee for Case : {}; assignedStaff : {}; Module : {};",caseDTO.getCaseNumber(), assignedStaff.getUsername(),SUBMODULE);
        if (caseDTO.getCaseCategoryId() != null) {
            logger.debug("Fetching CaseCategory for CaseCategoryId: {}; Module: {}", caseDTO.getCaseCategoryId(), SUBMODULE);
            Optional<CaseCategory> ticketSubReasonCategory = caseCategoryRepository.findById(caseDTO.getCaseCategoryId());
            if (ticketSubReasonCategory.isPresent()) {
                if (!CollectionUtils.isEmpty(ticketSubReasonCategory.get().getCaseCategoryTatMappingList())) {
                    List<CaseCategoryTatMapping> caseCategoryTatMappings = ticketSubReasonCategory.get().getCaseCategoryTatMappingList();
                    logger.debug("Found {} Case Categories Tat Mappings for CaseCategoryId: {}; Module: {}", caseCategoryTatMappings.size(), caseDTO.getCaseCategoryId(), SUBMODULE);
                    for (CaseCategoryTatMapping caseCategoryTatMapping : caseCategoryTatMappings) {
                        QTatQueryFieldMapping qTatQueryFieldMapping = QTatQueryFieldMapping.tatQueryFieldMapping;
                        BooleanExpression exp = qTatQueryFieldMapping.isNotNull().and(qTatQueryFieldMapping.tatMappingId.eq(caseCategoryTatMapping.getId().intValue()));
                        List<TatQueryFieldMapping> tatQueryFieldMappingList = (List<TatQueryFieldMapping>) tatQueryFieldMappingRepo.findAll(exp);
                        if (!CollectionUtils.isEmpty(tatQueryFieldMappingList)) {
                            //If query not matched then skip
                            if (!checkTicketTatCondition(tatQueryFieldMappingList, caseDTO))
                                continue;
                        }
                        if (!isNotificationTypeTeam) {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findAllByTeamIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(map.get("teamId")), Integer.valueOf(map.get("eventId")), map.get("eventName"), true);
                            if (!CollectionUtils.isEmpty(preTatMatrixWorkFlowDetails) && !isNotificationTypeTeam) {
                                preTatMatrixWorkFlowDetails = preTatMatrixWorkFlowDetails.stream()
                                        .peek(tatMatrixWorkFlowDetails -> {
                                            tatMatrixWorkFlowDetails.setIsActive(false);
                                            tatMatrixWorkFlowDetails.setIsOverDueReminder(false);
                                        })
                                        .collect(Collectors.toList());
                                tatMatrixWorkFlowDetailsRepo.saveAll(preTatMatrixWorkFlowDetails);
                            }
                        } else {
                            List<TatMatrixWorkFlowDetails> preTatMatrixWorkFlowList = tatMatrixWorkFlowDetailsRepo.findAllByTeamIdAndEntityIdAndEventNameAndIsActive(Long.valueOf(Long.parseLong(map.get("teamId"))), Integer.valueOf(Integer.parseInt(map.get("eventId"))), map.get("eventName"), true);
                            if (map.get("fromPickedUp").equals("true")) {
                                logger.info("Setting previous workflow details for 'fromPickedUp' condition; Module: {}", SUBMODULE);
                                for (int i = 0; i < preTatMatrixWorkFlowList.size(); i++) {
                                    if (preTatMatrixWorkFlowList.get(i) != null && isNotificationTypeTeam) {
                                        preTatMatrixWorkFlowList.get(i).setIsActive(false);
                                        preTatMatrixWorkFlowList.get(i).setIsOverDueReminder(false);
                                        tatMatrixWorkFlowDetailsRepo.save(preTatMatrixWorkFlowList.get(i));
                                    }
                                }
                            }

                        }
                        TicketTatMatrix masterTicketTat = caseCategoryTatMapping.getTicketTatMatrix();
                        List<TicketTatMatrixMapping> tatMatrixMappings = masterTicketTat.getTatMatrixMappings();
                        if (!isNotificationTypeTeam) {
                            if (!CollectionUtils.isEmpty(tatMatrixMappings)) {
                                Optional<TicketTatMatrixMapping> mapping = tatMatrixMappings.stream().filter(ticketTatMatrixMapping -> ticketTatMatrixMapping.getOrderNo().equals(1l)).findFirst();
                                if (mapping.isPresent()) {
                                    if (mapping != null && assignedStaff.getParentStaffId() != null) {
                                        logger.debug("Creating Tat Matrix Work Flow Details for Case: {}; Module: {}", caseDTO.getCaseNumber(), SUBMODULE);
                                        TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                                new TatMatrixWorkFlowDetails(mapping.get().getOrderNo(), mapping.get().getLevel(), assignedStaff.getId(), null,
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
                                        logger.info("Tat Matrix Work Flow Details created and saved for Case: {}; Module: {}", caseDTO.getCaseNumber(), SUBMODULE);
                                        break;
                                    }
                                }
                            }
                        } else if (isNotificationTypeTeam) {
                            logger.debug("Processing TatMatrix for team notification; Module: {}", SUBMODULE);
                            if (masterTicketTat != null && assignedStaff.getParentStaffId() != null) {
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
                                if (!caseDTO.getCaseStatus().equalsIgnoreCase("Follow Up")) {
                                    logger.debug("Creating new Tat Matrix Work Flow Details for team notification; Module: {}", SUBMODULE);
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(), null,
                                                    assignedStaff.getParentStaffId(), LocalDateTime.now(),
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                    logger.info("Tat Matrix Work Flow Details created and saved for team notification; Case: {}; Module: {}", caseDTO.getCaseNumber(), SUBMODULE);
                                } else {
                                    LocalDateTime followUpDateTime = LocalDateTime.of(caseDTO.getNextFollowupDate(), caseDTO.getNextFollowupTime());
                                    TatMatrixWorkFlowDetails tatMatrixWorkFlowDetails =
                                            new TatMatrixWorkFlowDetails(new Long(1), "Level 1", assignedStaff.getId(), null,
                                                    assignedStaff.getParentStaffId(), followUpDateTime,
                                                    String.valueOf(masterTicketTat.getRtime()), masterTicketTat.getRunit(), "Notification", true, null,
                                                    caseDTO.getCaseId().intValue(), map.get("eventName"), Integer.valueOf(map.get("eventId")),
                                                    CommonConstants.NOTIFICATION_TYPE_TEAM, Long.valueOf(map.get("teamId")), true);
                                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetails);
                                    logger.info("Follow-up Tat Matrix Work Flow Details created and saved for Case: {}; Module: {}", caseDTO.getCaseNumber(), SUBMODULE);
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
                            //preTatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findByWorkFlowIdAndCurrentTeamHeirarchyMappingIdAndIsActive(Long.valueOf(prevTeamHierarchyMapping.getHierarchyId()), prevTeamHierarchyMapping.getId(), true);
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
                                    new TatMatrixWorkFlowDetails(details.get().getOrderNo(), details.get().getLevel(), assignedToStaff.getId(), matrixDetails.get().getId(),
                                            assignedToStaff.getParentStaffId(), LocalDateTime.now(),
                                            details.get().getMtime(), details.get().getMunit(), details.get().getAction(), true, map.get("nextTatMappingId") != null ? Integer.valueOf(map.get("nextTatMappingId")) : null,
                                            entityId, map.get("eventName"), map.get("eventId") != null ? Integer.valueOf(map.get("eventId")) : null, CommonConstants.NOTIFICATION_TYPE_STAFF, null, true);
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
        String SUBMODULE = getModuleNameForLog() + "[ inActivateTatWorkflowMapping ]";
        logger.debug("inactivation of TAT Workflow Mapping for Event ID: {}; Entity ID: {}; module: {};", Integer.valueOf(map.get("eventId")) , Integer.valueOf(map.get("entityId")), SUBMODULE);
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
                logger.info("Successfully inactivated TAT workflow mappings & saved all tatMatrixWorkFlowDetails for Event ID: {}; Entity ID: {}; module: {};", Integer.valueOf(map.get("eventId")) , Integer.valueOf(map.get("entityId")), SUBMODULE);
            } else {
                logger.debug("No active TAT workflow mappings found for Event ID: {}; Entity ID: {}; module: {}", Integer.valueOf(map.get("eventId")) , Integer.valueOf(map.get("entityId")), SUBMODULE);
            }
        }
    }

    public void assignToNextApprovalStaff(TatMatrixWorkFlowDetails details) {
        String SUBMODULE = getModuleNameForLog() + "[ assignToNextApprovalStaff ]";
        logger.debug("Assigning to next approval staff for TAT Workflow Mapping ID: {}; Entity ID: {}; Event ID: {}; module: {};", details.getId(), details.getEntityId(), details.getEventId(), SUBMODULE);
        try {
            logger.debug("TAT Notification and Reassign process started; module: {}",SUBMODULE);
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
                        StaffUser parentUser = staffUserRepository.findById(staffUser.getParentStaffId()).get();
                        StaffUser currentAssignee = new StaffUser();
                        currentAssignee = staffUserRepository.findById(staffUser.getParentStaffId()).orElse(null);
                        aCase.setCurrentAssignee(currentAssignee);
                        caseRepository.save(aCase);
                        logger.info("Case saved and reassigned to parent staff: {}; parent Id: {}; module: {}};", parentUser.getUsername(),staffUser.getParentStaffId(),SUBMODULE);
                        //Audit for TAT
                        TicketTatAudits tatAudits = saveTatDetails(details.getEntityId(), details);
                        logger.debug("TAT details saved for case Id: {}; staff Id: {};parent Id: {}; module: {}", tatAudits.getCaseId(),tatAudits.getAssignStaffId(),tatAudits.getAssignStaffParentId(),SUBMODULE);
                        System.out.println("------------------------TAT Reassign done for Action - Both--------------------------");
                        logger.debug("TAT Reassign done for Action - Both; module: {};",SUBMODULE);
                        details.setStartDateTime(LocalDateTime.now());

                        // send notification if action is "BOTH"
                        if (details.getAction().equalsIgnoreCase("both")) {
                            if (details.getParentId() != null) {
                                String caseNumber = "";
                                String caseTitle = "";
                                String team = "";
                                String casePriority = "";
                                String staffName = "";
                                if (aCase != null) {
                                    caseNumber = aCase.getCaseNumber();
                                    caseTitle = aCase.getCaseTitle();
                                    casePriority = aCase.getPriority();
                                    //staffName = aCase.getStaffUser().getUsername();
                                    if (staffUser.getTeam() != null && !staffUser.getTeam().isEmpty()) {
                                        Set<Teams> teams = staffUser.getTeam();
                                        team = new ArrayList<>(teams).get(0).getName().toString();
                                    }
                                }


                                Long buId = null;
                                if (parentUser.getBusinessUnitNameList() != null && !parentUser.getBusinessUnitNameList().isEmpty()) {
                                    buId = parentUser.getBusinessUnitNameList().get(0).getId();
                                }
                                String ticketCreatorStaffEmail = "";
                                List<String> ccEmailList = new ArrayList<>();
                                if (aCase.getCreatedById() != null) {
                                    Optional<StaffUser> ticketCreatorStaff = staffUserRepository.findById(aCase.getCreatedById());
                                    if (ticketCreatorStaff.isPresent()) {
                                        ticketCreatorStaffEmail = ticketCreatorStaff.get().getEmail();
                                        ccEmailList.add(ticketCreatorStaffEmail);
                                    }
                                }
                                ccEmailList.add(staffUser.getEmail());
                                sendTatNotification(parentUser.getUsername(), staffUser.getUsername(), details.getEventName(), details.getNextFollowUpDate().toString(), parentUser.getPhone(), parentUser.getEmail(), aCase.getMvnoId(), tatAudits, buId, caseNumber, caseTitle, casePriority, team, staffName, ccEmailList);
                                hierarchyService.sendWorkflowAssignActionMessage(staffUser.getCountryCode(),staffUser.getPhone(),staffUser.getEmail(),staffUser.getMvnoId(),staffUser.getUsername(),"Assign", (staffUser.getBusinessUnit() != null) ? staffUser.getBusinessUnit().getId() : null);
                                logger.info("TAT Notification sent for 'BOTH' action; parentUsername: {}, staffUsername: {}, EventName: {}; module: {}",parentUser.getUsername(), staffUser.getUsername(), details.getEventName(),SUBMODULE);
                                System.out.println("------------------------TAT Notifiaction sent for Action - Both--------------------------");
                            }
                        }
                        details.setParentId(null);
                        //changing level to next level
                        workflowAuditService.saveAudit(details.getEventId(), CommonConstants.WORKFLOW_EVENT_NAME.CASE, Math.toIntExact(aCase.getCaseId()), aCase.getCaseNumber(), parentUser.getId(), parentUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Reasign to Staff :- "+parentUser.getUsername());

                        if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                            try {
                                logger.debug("Fetching TicketTatMatrix for case: {} ;handling Datetime for event : {} status and Setting followup time for next level; module: {};",aCase.getCaseNumber(),details.getEventName(),SUBMODULE);
                                // Case caseEntity = caseRepository.findById(Long.valueOf(details.getEntityId())).orElse(null);
                                if (details.getNotificationType() != null && !details.getNotificationType().equals(CommonConstants.NOTIFICATION_TYPE_TEAM)) {
                                    if (aCase != null) {
                                        TicketTatMatrix ticketTatMatrix = caseService.getTicketTatMatrixFromSubReasonId(caseService.getMapper().domainToDTO(aCase, new CycleAvoidingMappingContext()));
                                        if (ticketTatMatrix != null) {
                                            //Case case1;
                                            Optional<TicketTatMatrixMapping> ticketTatMatrix1 = ticketTatMatrix.getTatMatrixMappings().stream().filter(p -> p.getOrderNo().equals(details.getOrderNo() + 1)).findFirst();
                                            if (ticketTatMatrix1 != null && ticketTatMatrix1.isPresent()) {
                                                logger.info("Updating next follow-up time for Case: {}; module: {}", aCase.getCaseNumber(), SUBMODULE);
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
                                                logger.info("Ticket Tat Matrix updated date time for Case: {} with priority: {} ; module: {}", aCase.getCaseNumber(),aCase.getPriority(),SUBMODULE);
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
                                                logger.info("ticket Tat Matrix updated and saved for case: {} with munit: {} ; module: {}", aCase.getCaseNumber(),ticketTatMatrix1.get().getMunit(),SUBMODULE);
                                                details.setOrderNo(ticketTatMatrix1.get().getOrderNo());
                                                details.setLevel(ticketTatMatrix1.get().getLevel());
                                                details.setAction(ticketTatMatrix1.get().getAction());
                                                details.setStaffId(staffUser.getParentStaffId());
                                                details.setAction(ticketTatMatrix1.get().getAction());
                                                StaffUser newStaffuser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                                                details.setParentId(newStaffuser.getParentStaffId());
                                            } else {
                                                details.setParentId(null);
                                                details.setIsActive(false);
                                                tatMatrixWorkFlowDetailsRepo.saveAndFlush(details);
                                                logger.error("No next level tat Matrix mapping found for case: {}; moudle: {}", aCase.getCaseNumber(),SUBMODULE);
                                            }
                                        } else {
                                            details.setIsActive(false);
                                            tatMatrixWorkFlowDetailsRepo.save(details);
                                            logger.error("No tat Matrix Work Flow Details found, setting workflow as inactive for case: {}; moudle: {}", aCase.getCaseNumber(),SUBMODULE);
                                        }
                                    }
                                } else {
                                    logger.info("No parent staff found, setting workflow as inactive for Case ID: {}; module: {}", details.getEntityId(), SUBMODULE);
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                }

                                tatMatrixWorkFlowDetailsRepo.saveAndFlush(details);
                                logger.info("Workflow details successfully saved and flushed for TatMatrixWorkFlowDetails ID: {}; module: {}", details.getId(), SUBMODULE);
                            } catch (Exception ex) {
                                logger.error("Error while assigning next staff by Tat scheduler for Case ID: {}; Error: {}", details.getId(), ex.getMessage(), ex);
                                ex.printStackTrace();
                            }
                        } else {
                            logger.debug("Fetching Matrix details for TatMatrix ID: {}; module: {}", details.getTatMatrixId(), SUBMODULE);
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
                                    logger.warn("No next level MatrixDetails found for current OrderNo: {}; Setting workflow as inactive; module: {}", details.getOrderNo(), SUBMODULE);
                                    details.setIsActive(false);
                                    tatMatrixWorkFlowDetailsRepo.save(details);
                                    logger.info("Workflow details updated and saved for TatMatrixWorkFlowDetails ID: {}; module: {}", details.getId(), SUBMODULE);
                                }
                                tatMatrixWorkFlowDetailsRepo.save(details);
                            }
                        }
                    } else {
                        logger.error("No Matrix found for TatMatrix ID: {}; Marking workflow as inactive; module: {}", details.getTatMatrixId(), SUBMODULE);
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
        String SUBMODULE = getModuleNameForLog()+" [sendTatNotificationTypeTeam] ";
        try {
            logger.debug("sending Tat Notification to Team for team name: {}; ticket: {}; module: {}",teamName,ticketNumber,SUBMODULE);
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TAT_SEND_PARENT_TO_TEAM_FOR_TASK);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {

                    TicketPickMessageToTeam ticketAssignMessege = new TicketPickMessageToTeam(mobileNumber, emailId, RMQConstants.TAT_NO_RESPONSE_TAKEN, optionalTemplate.get(), parentStaffPersonName, assigndatetime, eventName, mvnoId, teamName, ticketNumber,tatAudits,buId, customerName, caseTitle, casePriority);
                    Gson gson = new Gson();
                    gson.toJson(ticketAssignMessege);
//                    messageSender.send(ticketAssignMessege, RMQConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM);
                    System.out.println("******************** TATNOTIFICATIONTOTEAMFORTASK successfully sent to Notification service***************** "+ticketAssignMessege);
                    kafkaMessageSender.send(new KafkaMessageData(ticketAssignMessege,TicketPickMessageToTeam.class.getSimpleName(),RMQConstants.TAT_SEND_PARENT_TO_TEAM_FOR_TASK));
                    logger.info("Notification Successfully sent to Assign Team : {}; module: {}",teamName,SUBMODULE);
                }
            } else {
                logger.error("No event configured TAT NOTIFICATION TO TEAM FOR TASK Template not available!; module: {};", SUBMODULE);
//                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("TAT Template not available.");
            }


        } catch (Throwable e) {
            logger.error("Error occurred while sending TAT Notification for Team: {}; Ticket: {}; Error: {}",teamName, ticketNumber, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public TicketTatAudits saveTatDetails(Integer aCaseId, TatMatrixWorkFlowDetails details) {
        //Fetching the details of case
        String SUBMODULE = getModuleNameForLog() + "[ saveTatDetails ]";
        if(aCaseId != null)
            logger.debug("saving TAT detail saving process for Case ID: {}; Module: {}", aCaseId, SUBMODULE);
        Case ticketCase = new Case();
        ticketCase = caseRepository.findById(aCaseId.longValue()).orElse(null);

        TicketTatAudits tatAudits = new TicketTatAudits();

        if (!details.getAction().equalsIgnoreCase(CommonConstants.TICKET_ACTION.REASSIGN)) {
            logger.debug("Tat matrix workflow details action equals : {} is not REASSIGN for Case ID: {}; module: {};", details.getAction(),ticketCase.getCaseNumber(),SUBMODULE);
            if (ticketCase != null) {
                tatAudits.setCaseId(ticketCase.getCaseId().intValue());
                tatAudits.setSlaTime(ticketCase.getCaseSlaTime());
                tatAudits.setSlaUnit(ticketCase.getCaseSlaUnit());
                tatAudits.setCaseStatus(ticketCase.getCaseStatus());

                if (details != null) {
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if (details.getNotificationType().equalsIgnoreCase("team")) {
                        tatAudits.setNotificationFor("Response Time Breach");
                    } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }
        } else {
            logger.debug("Tat matrix workflow details action equals : {} is REASSIGN for Case ID: {}; module: {};", details.getAction(),ticketCase.getCaseNumber(),SUBMODULE);
            if (ticketCase != null) {
                tatAudits.setCaseId(ticketCase.getCaseId().intValue());
                tatAudits.setSlaTime(ticketCase.getCaseSlaTime());
                tatAudits.setSlaUnit(ticketCase.getCaseSlaUnit());
                tatAudits.setCaseStatus(ticketCase.getCaseStatus());

                if (details != null) {
                    tatAudits.setTatTime(Integer.valueOf(details.getMtime()));
                    tatAudits.setTatUnit(details.getMunit());
                    tatAudits.setTatAction(details.getAction());
                    tatAudits.setTatStartTime(String.valueOf(details.getStartDateTime()));
                    tatAudits.setAssignStaffId(details.getStaffId());
                    tatAudits.setAssignStaffParentId(details.getParentId());
                    tatAudits.setCaseLevel(details.getLevel());
                    tatAudits.setIsTatBreached("Yes");
                    if (details.getNotificationType().equalsIgnoreCase("team")) {
                        tatAudits.setNotificationFor("Response Time Breach");
                    } else if (details.getNotificationType().equalsIgnoreCase("staff")) {
                        tatAudits.setNotificationFor("Tat Time Breach");

                    }

                }

            }

        }
        TicketTatAudits savedTatAudit = tatAuditRepository.save(tatAudits);
        logger.info("TAT details saved successfully for Case ID: {}; Audit ID: {};module: {}", aCaseId, savedTatAudit.getId(),SUBMODULE);
        return tatAudits;

    }
    public String getModuleNameForLog() {
        return "[TatUtils]";
    }

}
