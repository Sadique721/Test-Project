package com.savbill.ticketmanagement.core.modules.Schedulars;


import com.savbill.ticketmanagement.core.constants.CaseConstants;
import com.savbill.ticketmanagement.core.modules.ClientServ.domain.ClientService;
import com.savbill.ticketmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.QTatMatrixWorkFlowDetails;
import com.savbill.ticketmanagement.core.modules.Matrix.domain.TatMatrixWorkFlowDetails;
import com.savbill.ticketmanagement.core.modules.Matrix.repository.TatMatrixWorkFlowDetailsRepo;
import com.savbill.ticketmanagement.core.modules.Template.domain.TemplateNotification;
import com.savbill.ticketmanagement.core.modules.Template.repository.NotificationTemplateRepository;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.ticketmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.ticketmanagement.core.modules.tickets.domain.Case;
import com.savbill.ticketmanagement.core.modules.tickets.repository.CaseRepository;
import com.savbill.ticketmanagement.core.modules.tickets.service.CaseService;
import com.savbill.ticketmanagement.core.modules.utils.CommonConstants;
import com.savbill.ticketmanagement.core.modules.utils.TatUtils;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import com.savbill.ticketmanagement.kafka.KafkaMessageData;
import com.savbill.ticketmanagement.kafka.KafkaMessageSender;
//import com.savbill.ticketmanagement.rabbitmq.MessageSender;
import com.savbill.ticketmanagement.rabbitmq.RabbitMqConstants;
import com.savbill.ticketmanagement.rabbitmq.messages.TicketTatOverDueNotification;
import com.savbill.ticketmanagement.rabbitmq.messages.TicketTatReminderNotification;
import com.savbill.ticketmanagement.rabbitmq.rqconstants.RMQConstants;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.savbill.ticketmanagement.core.constants.CaseConstants.TAT_OVERDUE_REMIDER_TIME_NAME;

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







    @Scheduled(cron = "${cronJobTimeForTatMatrix}")
    public void runSchedulerForTAT() {
        System.out.println("***** -------------Savbill Api gateway scheduler  for tat matrix action  Starting----------------- *****");


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
            for (TatMatrixWorkFlowDetails details : tatMatrixWorkFlowDetails) {
                StaffUser staffUser = new StaffUser();
                staffUser = staffUserRepository.findById(details.getStaffId()).orElse(null);
                StaffUser parentStaffUser = new StaffUser();
                if(details.getParentId()!=null) {
                    parentStaffUser = staffUserRepository.findById(details.getParentId()).orElse(null);
                }


                LocalTime time = LocalTime.now();
                time = LocalTime.of(time.getHour(), time.getMinute());
                LocalDateTime endDateTime = null;
                if (details.getEventName().equalsIgnoreCase(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                    Case casedataa = caseService.getRepository().findById(details.getEntityId().longValue()).orElse(null);
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
                        System.out.println("---------------------- Reminder Time ------------------------" + reminderTime + "LOCALDATE_TIME -> " + LocalDateTime.now());
                        if (reminderTime.truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)) || reminderTime.minusMinutes(1).truncatedTo(ChronoUnit.MINUTES).equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                            System.out.println("------------------- TAT_BREACH REMINDER INTIT---------------------------");
                            Long buId = null;
                            if(parentStaffUser!=null){
                                if (!staffUser.getBusinessUnitNameList().isEmpty()) {
                                    buId = staffUser.getBusinessUnitNameList().get(0).getId();
                                }
                                sendReminderforTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), casedataa.getMvnoId(),buId);
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
        System.out.println("***** Savbill Api gateway scheduler  for tat matrix action  End *****");
    }

    // @Scheduled(cron = "${cronJobTimeForTatOverDueMatrix}")
    public void runSchedulerForTATOverDue() {
        System.out.println("***** Savbill Api gateway scheduler  for TAT_OVERDUE matrix action  Starting *****");
        QTatMatrixWorkFlowDetails qTatMatrixWorkFlowDetails = QTatMatrixWorkFlowDetails.tatMatrixWorkFlowDetails;
        BooleanExpression booleanExpression = qTatMatrixWorkFlowDetails.isNotNull().and(qTatMatrixWorkFlowDetails.isActive.eq(false).and(qTatMatrixWorkFlowDetails.isOverDueReminder.eq(true)));
        List<TatMatrixWorkFlowDetails> tatMatrixWorkFlowDetails = (List<TatMatrixWorkFlowDetails>) tatMatrixWorkFlowDetailsRepo.findAll(booleanExpression);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!CollectionUtils.isEmpty(tatMatrixWorkFlowDetails)) {
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
                    //calculate reminder time and send notification before tat breach
                    //LocalDateTime reminderTime = calculateTatBreachedReminderTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    LocalDateTime overDueTime = calculateTatBreachedOverDueTime(endDateTime).truncatedTo(ChronoUnit.MINUTES);
                    if (overDueTime.equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                        Long buId = null;
                        if(parentStaffUser!=null) {
                            if (Objects.nonNull(parentStaffUser.getBusinessUnit())) {
                                buId = parentStaffUser.getBusinessUnit().getId();
                            }
                        }
                        sendReminderforOverDueTat(parentStaffUser.getPhone(), parentStaffUser.getEmail(), staffUser.getUsername(), parentStaffUser.getUsername(), casedataa.getCaseNumber(), parentStaffUser.getMvnoId(),buId);
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
        System.out.println("***** Savbill Api gateway scheduler  for TAT_OVERDUE matrix action  End *****");
    }




    public LocalDateTime calculateTatEndDate(TatMatrixWorkFlowDetails details) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = details.getStartDateTime();
        long hours = 0;
        long minutes = 0;
        switch (details.getMunit().toLowerCase(Locale.ROOT)) {
            case "day":
                endDate = startDate.plusDays(Long.valueOf(details.getMtime()));
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
                break;
        }
        return endDate;
    }




    public LocalDateTime calculateTatBreachedReminderTime(LocalDateTime endDateTime) {
        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(CaseConstants.TAT_BREACHED_REMIDER_TIME_NAME);
        String tatReminderBreachTime = null;
        if (clientService == null) {
            tatReminderBreachTime = String.valueOf("15-M");
        } else {
            tatReminderBreachTime = String.valueOf(clientService.getValue()).replaceAll("\\s+", "");
        }
        String reminderTimeMode = tatReminderBreachTime.substring(tatReminderBreachTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatReminderBreachTime.substring(0, tatReminderBreachTime.indexOf('-')));

        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.minusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "h":
                reminderTime = endDateTime.minusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "m":
                reminderTime = endDateTime.minusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
        }
        return reminderTime;
    }



    public void sendReminderforTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId,Long buId) {

        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RMQConstants.TICKET_TAT_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    TicketTatReminderNotification ticketTatReminderNotification = new TicketTatReminderNotification(RMQConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG,
                            optionalTemplate.get(),
                            RMQConstants.TICKET_TAT_REMINDER_NOTIFICATION_MSG,
                            mobileNumber,
                            emailId,
                            mvnoId,
                            staffName,
                            parentStaffName,
                            caseNumber,buId);
                    Gson gson = new Gson();
                    gson.toJson(ticketTatReminderNotification);
//                    messageSender.send(ticketTatReminderNotification, RMQConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER);
                    kafkaMessageSender.send(new KafkaMessageData(ticketTatReminderNotification, TicketTatReminderNotification.class.getSimpleName()));

                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    private void calculatedate(LocalDateTime endDateTime, TatMatrixWorkFlowDetails details, LocalDateTime currentDateTime) {
        if (endDateTime != null) {

            if (endDateTime.truncatedTo(ChronoUnit.MINUTES).equals(currentDateTime.truncatedTo(ChronoUnit.MINUTES)) || endDateTime.truncatedTo(ChronoUnit.MINUTES).isBefore(currentDateTime.truncatedTo(ChronoUnit.MINUTES))) {
                switch (details.getAction()) {
                    case CommonConstants.TICKET_ACTION.BOTH:
                        tatUtils.assignToNextApprovalStaff(details);
                        //tatUtils.sendNotificationToStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.REASSIGN:
                        tatUtils.assignToNextApprovalStaff(details);
                        break;
                    case CommonConstants.TICKET_ACTION.NOTIFICATION:
                        tatUtils.sendNotificationToStaff(details);
                        break;
                }
            }
        }
    }

    public LocalDateTime calculateTatBreachedOverDueTime(LocalDateTime endDateTime) {
        LocalDateTime reminderTime = null;
        ClientService clientService = clientServiceSrv.getByName(TAT_OVERDUE_REMIDER_TIME_NAME);
        String tatOverDueReminderTime = null;
        if (clientService == null) {
            tatOverDueReminderTime = String.valueOf("15-M");
        }
        tatOverDueReminderTime = String.valueOf(clientService.getValue());
        String reminderTimeMode = tatOverDueReminderTime.substring(tatOverDueReminderTime.indexOf('-') + 1);
        Integer remindetrTimeValue = Integer.valueOf(tatOverDueReminderTime.substring(0, tatOverDueReminderTime.indexOf('-')));

        switch (reminderTimeMode.toLowerCase(Locale.ROOT)) {
            case "d":
                reminderTime = endDateTime.plusDays(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "h":
                reminderTime = endDateTime.plusHours(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
            case "m":
                reminderTime = endDateTime.plusMinutes(Long.parseLong(String.valueOf(remindetrTimeValue)));
                break;
        }
        return reminderTime;
    }

    public void sendReminderforOverDueTat(String mobileNumber, String emailId, String staffName, String parentStaffName, String caseNumber, Integer mvnoId,Long buId) {

        try {

            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION);
            if (optionalTemplate.isPresent()) {
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
                }
            } else {
                // log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
                System.out.println("Ticket reminder not send to parent staff");
            }


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }





}
