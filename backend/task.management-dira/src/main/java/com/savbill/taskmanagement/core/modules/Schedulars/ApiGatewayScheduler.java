package com.savbill.taskmanagement.core.modules.Schedulars;


import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.taskmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.taskmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.modules.utils.CommonConstants;
import com.savbill.taskmanagement.core.modules.utils.TatUtils;
import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.taskmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.taskmanagement.rabbitmq.messages.TicketTatOverDueNotification;
import com.savbill.taskmanagement.rabbitmq.messages.TicketTatReminderNotification;
import com.savbill.taskmanagement.rabbitmq.rqconstants.RMQConstants;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.savbill.taskmanagement.core.constants.CaseConstants.TAT_OVERDUE_REMIDER_TIME_NAME;

@Component
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class ApiGatewayScheduler {


    @Autowired
    TatMatrixWorkFlowDetailsRepo tatMatrixWorkFlowDetailsRepo;


    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CaseService caseService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    NotificationTemplateRepository templateRepository;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    TatUtils tatUtils;

    @Autowired
    CaseRepository caseRepository;


    @Value("${cronJobTimeForTaskExpiry}")
    private String cronJobTimeForTaskExpiry;
    @Autowired
    TeamsRepository teamsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayScheduler.class);
    public String getModuleNameForLog() {
        return "[ApiGatewayScheduler]";
    }

    @Scheduled(cron = "${cronJobTimeForTatMatrix}")
    public void runSchedulerForTAT() {
        String SUBMODULE = getModuleNameForLog() + " [runSchedulerForTAT()] ";
        System.out.println("***** -------------Savbill Api gateway scheduler  for tat matrix action  Starting----------------- *****");
        logger.info("Savbill Api gateway scheduler  for tat matrix action  Starting; Module : {};",SUBMODULE);

        List<GrantedAuthority> role_name = new ArrayList<>();
        role_name.add(new SimpleGrantedAuthority("ADMIN"));
        LoggedInUser user = new LoggedInUser("admin", "admin@123", true, true, true, true, role_name, "admin", "admin", LocalDateTime.now(), 2, 1, "ADMIN", null, 2, null, 2, new ArrayList<Long>(), false, new ArrayList<String>(),new ArrayList<Long>(),"admin",null,null);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetailsList = tatMatrixWorkFlowDetailsRepo.findAllByIsActive(Boolean.TRUE);

        //filtering unwanted tickets which are closed or the tat has already gone
//        for (int i = 0; i < tatMatrixWorkFlowDetailsList.size(); i++) {
//            Case checkCase = caseRepository.findById(tatMatrixWorkFlowDetailsList.get(i).getEntityId().longValue()).orElse(null);
//            if (!checkCase.getCaseStatus().equalsIgnoreCase("Closed") || !checkCase.getCaseStatus().equalsIgnoreCase("Raise And Close")) {
//                LocalDateTime endDate = calculateTatEndDate(tatMatrixWorkFlowDetailsList.get(i));
//                if(endDate.isBefore(LocalDateTime.now())){
//                    tatMatrixWorkFlowDetailsList.get(i).setIsActive(false);
//                    tatMatrixWorkFlowDetailsRepo.save(tatMatrixWorkFlowDetailsList.get(i));
//                }
//
//            }
//        }
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = tatMatrixWorkFlowDetailsRepo.findAllByIsActive(Boolean.TRUE);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            logger.debug("found total total matrix work flow details : {}; Module : {}; ",tatMatrixWorkFlowDetails.size(),SUBMODULE);
            for (TatMatrixWorkFlowDetails details : tatMatrixWorkFlowDetails) {
                StaffUser staffUser = new StaffUser();
                staffUser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                StaffUser parentStaffUser = new StaffUser();
                if (details.getParentId() != null) {
                    parentStaffUser = staffUserRepository.findById(details.getParentId()).orElse(null);
                }


                LocalTime time = LocalTime.now();
                time = LocalTime.of(time.getHour(), time.getMinute());
                LocalDateTime endDateTime = null;
                if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                    Case casedataa = caseRepository.findById(details.getEntityId().longValue()).get();
                    if (Objects.nonNull(casedataa)) {
                        // if (casedataa.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
//                        LocalDate date = LocalDate.now();
//                        LocalDateTime localDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
//                        if (date.isEqual(casedataa.getNextFollowupDate())) {
//                            if (time.equals(casedataa.getNextFollowupTime()) || time.isBefore(casedataa.getNextFollowupTime())) {
//                                endDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
//                            }
//                        } else if (date.isAfter(casedataa.getNextFollowupDate())) {
//                            details.setIsActive(false);
//                            tatMatrixWorkFlowDetailsRepo.save(details);
//                        }
//                    }
                        //else {
                        endDateTime = calculateTatEndDate(details);
                        // }
                        //calculate reminder time and send notification before tat breach
                        if (endDateTime != null) {
                            LocalDateTime reminderTime = calculateTatBreachedReminderTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                            logger.debug("Tat Breached Reminder Time : {}; Local time : {}; Module : {};",reminderTime,LocalDateTime.now(),SUBMODULE);
                            System.out.println("---------------------- Reminder Time ------------------------" + reminderTime + "LOCALDATE_TIME -> " + LocalDateTime.now());
                            if (reminderTime.truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.minusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                                logger.debug("reminder time : {}; and Local time are same : {}; Module : {};",reminderTime,LocalDateTime.now(),SUBMODULE);
                                System.out.println("------------------- TAT_BREACH REMINDER INTIT---------------------------");
                                logger.info("TAT_BREACH REMINDER INTIT START for Case : {}; reminder time : {}; Module : {};",casedataa.getCaseNumber(),reminderTime,SUBMODULE);
                                Long buId = null;
                                if (parentStaffUser != null) {
                                    if (!staffUser.getBusinessUnitNameList().isEmpty()) {
                                        buId = staffUser.getBusinessUnitNameList().get(0).getId();
                                    }
                                    sendReminderforTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), casedataa.getMvnoId(), buId,casedataa.getCreatedate());
                                    logger.info("sent Reminder for Tat breach Reminder, parentSTaff : {}; StaffUser : {}; Case : {}; Reminder time : {}; Module : {};",parentStaffUser.getUsername(),staffUser.getUsername(),casedataa.getCaseNumber(),reminderTime,SUBMODULE);
                                }
                                System.out.println("------------------------ TAT_BREACH REMINDER END-----------------------------");
                            }
                            details.setNextFollowUpDate(endDateTime);
                            calculatedate(endDateTime, details, currentDateTime);
                        }
                    } else {
                        endDateTime = calculateTatEndDate(details);
                        details.setNextFollowUpDate(endDateTime);
                        calculatedate(endDateTime, details, currentDateTime);
                    }
                    logger.debug("setting next followup date for caseId : {}; Module : {};",details.getEntityId(),SUBMODULE);
//                if (endDateTime != null) {
//
//                    if (endDateTime.equals(currentDateTime) || endDateTime.isBefore(currentDateTime)) {
//                        switch (details.getAction()) {
//                            case CommonConstants.TICKET_ACTION.BOTH:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.REASSIGN:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.NOTIFICATION:
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                        }
//                    }
//                }
                }
            }
        }
        logger.info("Savbill Api gateway scheduler  for tat matrix action  End; Module : {};",SUBMODULE);
        System.out.println("***** Savbill Api gateway scheduler  for tat matrix action  End *****");
    }

    // @Scheduled(cron = "${cronJobTimeForTatOverDueMatrix}")
    public void runSchedulerForTATOverDue() {
        String SUBMODULE = getModuleNameForLog() + " [runSchedulerForTATOverDue()] ";
        System.out.println("***** Savbill Api gateway scheduler  for TAT_OVERDUE matrix action  Starting *****");
        logger.info("starting Savbill Api gateway scheduler  for TAT_OVERDUE matrix action; Module : {};",SUBMODULE);
        QTatMatrixWorkFlowDetails qTatMatrixWorkFlowDetails = QTatMatrixWorkFlowDetails.tatMatrixWorkFlowDetails;
        BooleanExpression booleanExpression = qTatMatrixWorkFlowDetails.isNotNull().and(qTatMatrixWorkFlowDetails.isActive.eq(false).and(qTatMatrixWorkFlowDetails.isOverDueReminder.eq(true)));
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = (List<TatMatrixWorkFlowDetails>) tatMatrixWorkFlowDetailsRepo.findAll(booleanExpression);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
            logger.debug("found total total matrix work flow details : {}; Module : {}; ",tatMatrixWorkFlowDetails.size(),SUBMODULE);
            for (TatMatrixWorkFlowDetails details : tatMatrixWorkFlowDetails) {
                StaffUser staffUser = new StaffUser();
                staffUser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                StaffUser parentStaffUser = new StaffUser();
                parentStaffUser = staffUserRepository.findById(details.getParentId()).orElse(null);

                LocalTime time = LocalTime.now();
                time = LocalTime.of(time.getHour(), time.getMinute());
                LocalDateTime endDateTime = null;
                if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                    Case casedataa = caseRepository.findById(details.getEntityId().longValue()).get();
                    if (casedataa.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)) {
                        logger.debug("found case status : {} for Case {}; Module : {}; ",casedataa.getCaseStatus(),casedataa.getCaseNumber(),SUBMODULE);
                        LocalDate date = LocalDate.now();
                        LocalDateTime localDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
                        if (date.isEqual(casedataa.getNextFollowupDate())) {
                            if (time.equals(casedataa.getNextFollowupTime()) || time.isBefore(casedataa.getNextFollowupTime())) {
                                endDateTime = LocalDateTime.of(casedataa.getNextFollowupDate(), casedataa.getNextFollowupTime());
                            }
                        }
                    } else {
                        endDateTime = calculateTatEndDate(details);
                    }
                    logger.debug("setting end date to {}; for Case : {}; Module : {};",endDateTime,casedataa.getCaseNumber(),SUBMODULE);
                    //calculate reminder time and send notification before tat breach
                    //LocalDateTime reminderTime = calculateTatBreachedReminderTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    LocalDateTime overDueTime = calculateTatBreachedOverDueTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    if (overDueTime.equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                        logger.debug("local time and overDueTime  are same for Case : {}; Module : {};",casedataa.getCaseNumber(),SUBMODULE);
                        Long buId = null;
                        if(parentStaffUser!=null) {
                            if (Objects.nonNull(parentStaffUser.getBusinessUnit())) {
                                buId = parentStaffUser.getBusinessUnit().getId();
                            }
                        }
                        sendReminderforOverDueTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), parentStaffUser.getMvnoId(),buId);
                        logger.info("reminder sent for overDue TAT for parentStaff : {}; staffUser : {}; Case : {}; Module : {};",parentStaffUser.getUsername(),staffUser.getUsername(),casedataa.getCaseNumber(),SUBMODULE);
                        details.setIsOverDueReminder(false);
                    }
                    //details.setNextFollowUpDate(endDateTime);
                    //calculatedate(endDateTime, details, currentDateTime);
                } else {
                    endDateTime = calculateTatEndDate(details);
                    //details.setNextFollowUpDate(endDateTime);
                    //calculatedate(endDateTime, details, currentDateTime);
                }
                tatMatrixWorkFlowDetailsRepo.save(details);
                logger.info("saved TAT matrix workflow details for Case : {}; Module : {};",details.getEntityId(),SUBMODULE);
//                if (endDateTime != null) {
//
//                    if (endDateTime.equals(currentDateTime) || endDateTime.isBefore(currentDateTime)) {
//                        switch (details.getAction()) {
//                            case CommonConstants.TICKET_ACTION.BOTH:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.REASSIGN:
//                                tatUtils.assignToNextApprovalStaff(details);
//                                break;
//                            case CommonConstants.TICKET_ACTION.NOTIFICATION:
//                                if (details.getParentId() != null)
//                                    tatUtils.sendNotificationToStaff(details);
//                                break;
//                        }
//                    }
//                }
            }
        }
        logger.info("Savbill Api gateway scheduler  for TAT_OVERDUE matrix action  End; Module : {};",SUBMODULE);
        System.out.println("***** Savbill Api gateway scheduler  for TAT_OVERDUE matrix action  End *****");
    }




    public LocalDateTime calculateTatEndDate(TatMatrixWorkFlowDetails details) {
        String SUBMODULE = getModuleNameForLog() + " [calculateTatEndDate()] ";
        logger.debug("Calculating TAT end date for Details : {}; Module : {};",(details.getId()!=null)?details.getId():" - ",SUBMODULE);
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = details.getStartDateTime();
        long hours = 0;
        long minutes = 0;
        switch (details.getMunit().toLowerCase(Locale.ROOT)) {
            case "day":
                endDate = startDate.plusDays(Long.valueOf(details.getMtime()));
                logger.debug("setting endDate using start date : {}; plus days :{}; Module : {}; ",startDate,details.getMtime(),SUBMODULE);
                break;
            case "hour":
                hours = Long.valueOf(details.getMtime());
                if (hours >= 24) {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    endDate = startDate.plusDays(days).plusHours(remainingHours);
                } else {
                    endDate = startDate.plusHours(hours);
                }
                logger.debug("setting endDate using start date : {}; plus hours :{}; Module : {}; ",startDate,details.getMtime(),SUBMODULE);
                break;
            case "min":
                minutes = Long.valueOf(details.getMtime());
                if (minutes >= 60) {
                    long remainingMinutes = minutes % 60;
                    hours += minutes / 60;  // Use the existing 'hours' variable
                    if (hours >= 24) {
                        long days = hours / 24;
                        long remainingHours = hours % 24;
                        endDate = startDate.plusDays(days).plusHours(remainingHours).plusMinutes(remainingMinutes);
                    } else {
                        endDate = startDate.plusHours(hours).plusMinutes(remainingMinutes);
                    }
                } else {
                    endDate = startDate.plusMinutes(minutes);
                }
                logger.debug("setting endDate using start date : {}; plus minutes :{}; Module : {}; ",startDate,details.getMtime(),SUBMODULE);
                break;
        }
        logger.info("Calculated TAT end date: {}; for caseID : {}; Module: {}", endDate,details.getEntityId(),SUBMODULE);
        return endDate;
    }




    public LocalDateTime calculateTatBreachedReminderTime(LocalDateTime endDateTime) {
        String SUBMODULE = getModuleNameForLog() + " [calculateTatBreachedReminderTime()] ";
        logger.debug("Calculating TAT Breached Reminder Time for endDateTime: {}; Module: {}", endDateTime, SUBMODULE);
        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(CaseConstants.TAT_BREACHED_REMIDER_TIME_NAME);
        String tatReminderBreachTime = null;
        if (clientService == null) {
            tatReminderBreachTime = String.valueOf("15-M");
            logger.warn("ClientService not found for TAT_BREACHED_REMIDER_TIME_NAME= [tatBreachedReminderTime];. Using value: {}; Module: {}",tatReminderBreachTime,SUBMODULE);
        } else {
            tatReminderBreachTime = String.valueOf(clientService.getValue()).replaceAll("\\s+", "");
            logger.debug("TAT Reminder Breach Time from ClientService: {}; Module: {}", tatReminderBreachTime, SUBMODULE);
        }
        String reminderTimeMode = tatReminderBreachTime.substring(tatReminderBreachTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatReminderBreachTime.substring(0, tatReminderBreachTime.indexOf('-')));
        logger.debug("Reminder Time Mode: {}, Value: {}; Module: {}", reminderTimeMode, remindetrTimeValue, SUBMODULE);
        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.minusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("Reminder Time set endDateTime : {}; minus : {}; days before endDateTime; reminderTime : {}; Module: {}",endDateTime,remindetrTimeValue,reminderTime,SUBMODULE);
                break;
            case "h":
                reminderTime = endDateTime.minusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("Reminder Time set endDateTime : {}; minus : {}; hours before endDateTime; reminderTime : {}; Module: {}",endDateTime,remindetrTimeValue,reminderTime,SUBMODULE);
                break;
            case "m":
                reminderTime = endDateTime.minusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("Reminder Time set endDateTime : {}; minus : {}; minutes before endDateTime; reminderTime : {}; Module: {}",endDateTime,remindetrTimeValue,reminderTime,SUBMODULE);
                break;
        }
        logger.info("Calculated TAT Breached Reminder time: {}; Module: {}", reminderTime, SUBMODULE);
        return reminderTime;
    }



    public void sendReminderforTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId,Long buId,LocalDateTime assignedTime) {
        String SUBMODULE = getModuleNameForLog() + " [sendReminderforTat()] ";
        try {
            logger.debug("sending reminder for tat staffName : {}; caseNumber : {}; Module : {};", staffName, caseNumber,SUBMODULE);
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_TAT_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketTatReminderNotification ticketTatReminderNotification = new TicketTatReminderNotification(RMQConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG,
                            optionalTemplate.get(),
                            RMQConstants.TATNOTIFICATION,
                            mobileNumber,
                            emailId,
                            mvnoId,
                            staffName,
                            parentStaffName,
                            caseNumber,buId,assignedTime);
                    Gson gson = new Gson();
                    gson.toJson(ticketTatReminderNotification);
//                    messageSender.send(ticketTatReminderNotification, RMQConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER);
                    kafkaMessageSender.send(new KafkaMessageData(ticketTatReminderNotification, TicketTatReminderNotification.class.getSimpleName(), RMQConstants.TATNOTIFICATION));
                    logger.info("reminder for Tat Breach Overdue Reminder sent notification for CaseNumber : {}; parentStaff : {}; staffName : {}; Module ; {};",caseNumber,parentStaffName,staffName,SUBMODULE);
                }
            } else {
                logger.warn("TICKET_TAT_REMINDER_NOTIFICATION template not found! Ticket reminder not send to parent staff : {}; CaseNumber : {}; Module : {}; ",parentStaffName,caseNumber,SUBMODULE);
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    private void calculatedate(LocalDateTime endDateTime, TatMatrixWorkFlowDetails details, LocalDateTime currentDateTime) {
        String SUBMODULE = getModuleNameForLog() + " [calculatedate()] ";
        if (endDateTime != null) {
            logger.debug("Calculating date for endDateTime: {}, entity currentDateTime: {}; entity Id  {}; Module: {}", endDateTime, currentDateTime,details.getEntityId(),SUBMODULE);
            if (endDateTime.truncatedTo(ChronoUnit.MINUTES).equals(currentDateTime.truncatedTo(ChronoUnit.MINUTES)) || endDateTime.truncatedTo(ChronoUnit.MINUTES).isBefore(currentDateTime.truncatedTo(ChronoUnit.MINUTES))) {
                logger.info("EndDateTime is equal to or before currentDateTime; endDateTime : {}; currentDateTime : {}; Module: {}",endDateTime,currentDateTime, SUBMODULE);
                switch (details.getAction()) {
                    case CommonConstants.TICKET_ACTION.BOTH:
                        logger.debug("BOTH actions for details: {}; Module: {}", details, SUBMODULE);
                        tatUtils.assignToNextApprovalStaff(details);
                        //tatUtils.sendNotificationToStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.REASSIGN:
                        logger.info("REASSIGN action for details: {}; Module: {}", details, SUBMODULE);
                        tatUtils.assignToNextApprovalStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.NOTIFICATION:
                        logger.info("NOTIFICATION action for details: {}; Module: {}", details, SUBMODULE);
                        tatUtils.sendNotificationToStaff(details);
                        break;
                    default:
                        logger.warn("Unknown action type: {}; Module: {}", details.getAction(), SUBMODULE);
                }
            }
        }else {
            logger.warn("EndDateTime is null. Module: {}", SUBMODULE);
        }
    }

    public LocalDateTime calculateTatBreachedOverDueTime(LocalDateTime endDateTime) {
        String SUBMODULE = getModuleNameForLog() + " [calculateTatBreachedOverDueTime()] ";
        logger.debug("Calculating TAT breached overdue time for endDateTime: {}; Module: {}", endDateTime, SUBMODULE);

        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(TAT_OVERDUE_REMIDER_TIME_NAME);
        String tatOverDueReminderTime = null;
        if (clientService == null) {
            tatOverDueReminderTime = String.valueOf("15-M");
            logger.warn("ClientService not found. Using default value: {}; Module: {}", tatOverDueReminderTime, SUBMODULE);
        }
        tatOverDueReminderTime = String.valueOf(clientService.getValue());
        logger.debug("TAT overdue reminder time from ClientService tatOverDueReminderTime: {}; Module: {}", tatOverDueReminderTime, SUBMODULE);
        String reminderTimeMode = tatOverDueReminderTime.substring(tatOverDueReminderTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatOverDueReminderTime.substring(0, tatOverDueReminderTime.indexOf('-')));
        logger.debug("Reminder time mode: {}, reminder Time Value: {}; Module: {}", reminderTimeMode, remindetrTimeValue, SUBMODULE);
        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.plusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("reminder time set to enddate time: {}; plus reminder value : {} days; Module : {};",endDateTime,remindetrTimeValue, SUBMODULE);
                break;
            case "h":
                reminderTime = endDateTime.plusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("reminder time set to enddate time: {}; plus reminder value : {} hours; Module : {};",endDateTime,remindetrTimeValue, SUBMODULE);
                break;
            case "m":
                reminderTime = endDateTime.plusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                logger.debug("reminder time set to enddate time: {}; plus reminder value : {} minutes; Module : {};",endDateTime,remindetrTimeValue, SUBMODULE);
                break;
        }
        logger.info("Calculated TAT breached overdue time: {}; Module: {}", reminderTime, SUBMODULE);
        return reminderTime;
    }

    public void sendReminderforOverDueTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId,Long buId) {
        String SUBMODULE = getModuleNameForLog() + " [sendReminderforOverDueTat()] ";
        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
                logger.debug("Template found for Tat Breach Overdue Reminder; Module : {};",SUBMODULE);
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketTatOverDueNotification ticketTatOverDueNotification = new TicketTatOverDueNotification(RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_GATEWAY,
                            optionalTemplate.get(),
                            RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG,
                            mobileNumber,
                            emailId,
                            mvnoId,
                            staffName,
                            parentStaffName,
                            caseNumber,buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketTatOverDueNotification);
                    // messageSender.send(ticketTatOverDueNotification, RabbitMqConstants.QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER);
                    logger.info("sending Reminder for overdue TAT notification for Case: {}; Module : {};", caseNumber,SUBMODULE);
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                logger.warn("Ticket reminder not send to parent staff; Case: {}; parent staffName : {}; Module : {};", caseNumber,parentStaffName,SUBMODULE);
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            logger.error("exception occurred while sending reminder for Over due TAT; Error Message : {}; Module : {};",e.getMessage(),SUBMODULE);
            throw new RuntimeException(e.getMessage());
        }

    }




    @Scheduled(cron = "${cronJobTimeForTaskExpiry}")
    public void runSchedulerTaskExpiry() {
        String SUBMODULE = getModuleNameForLog() + " [runSchedulerTaskExpiry()] ";
        try {
            // Parse the cron expression
            logger.info("Calender Task reschedule started; Module : {};",SUBMODULE);
            System.out.println("****************************Calender  Task reschedule started *****************************");
            CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
            Cron cron = parser.parse(cronJobTimeForTaskExpiry);
            ExecutionTime executionTime = ExecutionTime.forCron(cron);

            // Get the current time
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            logger.info("Calender Task reschedule Current execution time : {}; Module : {};",now,SUBMODULE);
            // Get the previous execution time explicitly
            Optional<ZonedDateTime> previousExecutionTime = executionTime.lastExecution(now.minusSeconds(1));
            if (previousExecutionTime.isPresent()) {
                ZonedDateTime previousExecution = previousExecutionTime.get();

                // Log the execution times
                logger.info("Calender Task reschedule Previous execution time : {}; Module : {};", previousExecution, SUBMODULE);
                System.out.println("Previous execution time: " + previousExecution);
                System.out.println("Current time: " + now);

                // Convert to LocalDateTime for database query
                LocalDateTime previousExecutionLocal = previousExecution.toLocalDateTime();
                LocalDateTime midnight = now.toLocalDate().atStartOfDay();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTimeStr = previousExecutionLocal.format(formatter);
                String midnightStr = midnight.format(formatter);
                String nowStr = LocalDateTime.now().format(formatter);
                List<Case> getCaseList = caseRepository.findCasesByStatusAndDateRange(formattedDateTimeStr,nowStr);
                List<Case> getCaseList1 = caseRepository.findCasesByStatusAndDateRangeForMidnight(midnightStr);
                getCaseList.addAll(getCaseList1);
                if (getCaseList != null && !getCaseList.isEmpty()) {
                    for (Case cases : getCaseList) {
                        if (Objects.nonNull(cases.getCurrentAssignee()) && cases.getCurrentAssignee().getParentStaffId() != null) {
                            Optional<StaffUser> parentStaff = staffUserRepository.findById(cases.getCurrentAssignee().getParentStaffId());
                            Teams teams=teamsRepository.findById(Long.valueOf(cases.getTeamId())).get() ;
                            parentStaff.ifPresent(staff -> {
                                logger.info("Email is Sent for staff: " +staff.getUsername()+" task id : "+cases.getCaseNumber() );
                                sendReminderforTat(parentStaff.get().getPhone(),parentStaff.get().getEmail(), staff.getUsername(), parentStaff.get().getUsername(), cases.getCaseNumber(), cases.getMvnoId(), cases.getBuId(),cases.getStartDate());

                                Optional<StaffUser> createter=staffUserRepository.findById(cases.getCreatedById());
                                createter.ifPresent(staff1 -> {
                                    logger.info("Email is Sent for staff: " +staff.getUsername()+" task id : "+cases.getCaseNumber() );
                                    sendReminderforTat(staff1.getPhone(),staff1.getEmail(), staff.getUsername(), parentStaff.get().getUsername(), cases.getCaseNumber(), cases.getMvnoId(), cases.getBuId(),cases.getStartDate());
                                });
                            });


                            cases.setIs_processed(true);
                        }
                        }

                    caseRepository.saveAll(getCaseList);
                    System.out.println("****************************Calender  Task reschedule Ended *****************************");
                } else {
                    logger.warn("Calender Task reschedule Current No cases found for the specified time range; midnight : {}; Module : {};",midnight,SUBMODULE);
                    System.out.println("No cases found for the specified time range.");
                }
            } else {
                logger.warn("Calender Task reschedule Current No previous execution time found", SUBMODULE);
                System.out.println("No previous execution time found.");
            }
        } catch (Exception e) {
            // Log the exception in case of error
            logger.error("Error occurred while executing scheduler task; Error Message : {}; Module: {}; ",e.getMessage(),SUBMODULE);
            System.err.println("Error occurred while executing scheduler task: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
