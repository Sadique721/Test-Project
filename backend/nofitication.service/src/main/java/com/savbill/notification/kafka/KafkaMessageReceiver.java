package com.savbill.notification.kafka;

import brave.Span;
import brave.Tracer;
import com.savbill.notification.BusinessUnit.service.BusinessUnitService;
import com.savbill.notification.Mvno.domain.UpdateMvnoData;
import com.savbill.notification.Mvno.service.MvnoService;
import com.savbill.notification.entity.Template;
import com.savbill.notification.rabbitmq.RabbitMqConstants;
import com.savbill.notification.rabbitmq.message.*;
import com.savbill.notification.rabbitmq.message.*;
import com.savbill.notification.repository.TemplateRepository;
import com.savbill.notification.services.*;
import com.savbill.notification.services.*;
import com.savbill.notification.services.impl.CustomerServiceImpl;
import com.savbill.notification.services.impl.NotificationAuditServiceImpl;
import com.savbill.notification.utils.ThreadPoolUtilityClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;


@Component
public class KafkaMessageReceiver implements Runnable{

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SystemConfigService configService;


    @Autowired
    SmsService smsService;

    @Autowired
    private NotificationAuditServiceImpl notificationAuditService;

    @Autowired
    StaffService staffService;

    @Autowired
    RoleService roleService;

    @Autowired
    private Tracer tracer;

    @Autowired
    TemplateRepository templateRepository;
//    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);


    private static final Logger log = LoggerFactory.getLogger(KafkaMessageReceiver.class);


    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    public static String user = null;
    @Autowired
    KafkaConsumerConfig consumerConfig;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


//    ThreadPoolUtilityClass threadPoolUtilityClass = new ThreadPoolUtilityClass();
//    Integer realTimeThread = threadPoolUtilityClass.threadPoolSize;
//    ExecutorService executorService = Executors.newFixedThreadPool(realTimeThread);

    public KafkaMessageReceiver() {
        messageHandlers.put("CustomerMessage:LoginSuccess", this::handleLoginSuccess);  //not in use
        messageHandlers.put("CustomerMessage:LoginFailure", this::handleLoginFailure);  // not in use
        messageHandlers.put("CustomerMessage:RegistrationSuccess", this::handleRegistrationSuccess);  //not in use
        messageHandlers.put("CustomerMessage:RegistrationFailure", this::handleRegistrationFailure);  // not in use
        messageHandlers.put("OtpMessage:" + KafkaConstant.OPT_FOR_PORTAL, this::handleOtpGeneration);
        messageHandlers.put("OtpMessage:" + KafkaConstant.OPT_FOR_LOGIN_2FA, this::handleOtpGeneration);
        messageHandlers.put("OtpMessage:", this::handleOtpGeneration);
        messageHandlers.put("VoucherCodeMessage", this::handleSendVoucherCode);  //chcked
        messageHandlers.put("StaffMessage", this::handleStaffSuccess); //not in use
        messageHandlers.put("RoleMessage", this::handleRoleList);  //not in use
        messageHandlers.put("CustomerMessage:UsedQuota", this::handleUsedQuota); //not in use
        messageHandlers.put("ConfigMessage:AddConfiguration", this::handleConfigurationAdd); //not in use
        messageHandlers.put("ConfigMessage:UpdateConfiguration", this::handleConfigurationAdd); //not in use
        messageHandlers.put("CustApprovalMessage", this::handleBssCustomerApprovalSuccess); //checked
        messageHandlers.put("CustApprovalFailMessage", this::handleBssCustomerApprovalFail); //not in use
        messageHandlers.put("CustomerRenewalMessage", this::handleBssCustomerRenewalSuccess); // checked
        messageHandlers.put("CustomerRechargeMessage", this::handleBssCustomerRechargeSuccess); //not in use
        messageHandlers.put("CustomerRegistrationSuccessMsg", this::handleBssCustomerRegistrationSuccess);//checked
        messageHandlers.put("PaymentLinkMessage", this::handleBssCustomerPaymentLink);
        messageHandlers.put("PaymentSuccess", this::handleBssCustomerPaymentSuccess);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.TICKET_ASSIGN_TEAM_SUCCESS, this::handleTicketAssignTeamSuccess);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.TASK_ASSIGN_TEAM_SUCCESS, this::handleTicketAssignTeamSuccess);
        messageHandlers.put("CustomerDunningMessage:" + KafkaConstant.BSS_CUSTOMER_DUNNING, this::handleBssCustomerDunning);
        messageHandlers.put("CustomerDeactivationMessage", this::handleBssCustomerDeactivation);
        messageHandlers.put("CustomerOtpRegistrationMessage", this::handleCustomerOtpRegistration);
        messageHandlers.put("StaffExpiredMassage:" + KafkaConstant.CUSTOMER_DOCUMENT_DUNNING_TO_STAFF, this::handleBssDocumentDunningStaff);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF, this::handleSalesCrmsBssFollowUpReminderStaff);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER, this::handleSalesCrmsBssFollowUpReminderCustomer);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF, this::handleSalesCrmsBssFollowUpOverDueStaff);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF, this::handleSalesCrmsBssFollowUpOverDueParentStaff);
        messageHandlers.put("StaffStatusChangeMessage", this::handleStaffSendStatus);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF, this::handleSalesCrmsBssNoLeadFollowUpReminderStaff);

        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF, this::handleSalesCrmsBssNoLeadFollowUpReminderParentStaff);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF, this::handleSalesCrmsBssNoFollowUpReminderStaff);
        messageHandlers.put("FollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF, this::handleSalesCrmsBssNoFollowUpReminderParentStaff);
        messageHandlers.put("CustTicketStatusMessage", this::handleSendCustomerStatusChange);
        messageHandlers.put("WorkflowTicketMessage", this::handleWorkflowActionAssignMessage);
        messageHandlers.put("SendFollowUpRemarkMsg:" + KafkaConstant.QUEUE_SEND_FOLLOWUP_REMARK_MSG, this::handleSendFollowupRemark);
        //messageHandlers.put("SendFollowUpRemarkMsg:"+KafkaConstant.SEND_PROBLEM_DOMAIN_CHANGE_MSG, this::handleSendProblemDomainChange);
        messageHandlers.put("SendFollowUpRemarkMsg" + KafkaConstant.SEND_TASK_CATEGORY_CHANGE_MSG, this::handleSendTaskCategoryChange);
        messageHandlers.put("TicketETRMsg", this::handleTicketEtr);
      //  messageHandlers.put("TicketETRMsg", this::handleTaskEtr);
        messageHandlers.put("TicketETRMsg:"+KafkaConstant.TASK_ETR_MSG, this::handleTaskEtr);
        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF, this::handleBssCafFollowUpReminderStaff);
        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF, this::handleBssCafFollowUpReminderStaff); //  Ticket followup reminder for staff
        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER, this::handleBssCafFollowUpReminderCustomer);
        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF, this::handleBssCafFollowUpOverDueStaff);
        messageHandlers.put("BssCafFollowUpOverDueStaff:" + KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF, this::handleBssCafFollowUpOverDueParentStaff);
        messageHandlers.put("TicketFollowUpMessage:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF, this::handleTroubleTicketFollowUpReminderStaff);
//        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF, this::handleTroubleTicketFollowUpReminderStaff);// this will not work because it is converting caf object to ticket object
        messageHandlers.put("CafFollowUpMessage:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER, this::handleTroubleTicketFollowUpReminderCustomer);
        messageHandlers.put("TicketFollowUpMessage:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF, this::handleTroubleTicketFollowUpOverDueStaff);
        messageHandlers.put("TroubleTicketFollowUpOverDueStaff:" + KafkaConstant.TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF, this::handleTroubleTicketFollowUpOverDueParentStaff);
        messageHandlers.put("TicketCreationMessage", this::handleTicketCreationSuccess);
        messageHandlers.put("TicketCreationMessage:" + KafkaConstant.TASK_CREATION_NOTIFICATION, this::handleTaskCreationSuccess);
        messageHandlers.put("TicketRescheduleMsg", this::handleTicketRescheduleSuccessMsg);
        messageHandlers.put("TicketTatReminderNotifcation", this::handleTicketTatBreachedReminder);
        messageHandlers.put("CustomerDunningMessage:" + KafkaConstant.DUNNING_ADVANCE_NOTIFICATION, this::handleDunningAdvanceNotification);
        messageHandlers.put("PartnerExpiredDocumentMessage:" + KafkaConstant.PARTNER_DUNNING_DOCUMENT, this::handlePartnerDunningDocument);
        messageHandlers.put("PartnerExpiredDocumentMessage:" + KafkaConstant.PARTNER_DUNNING_DOCUMENT_DEACTIVATION, this::handlePartnerDunningDocumentDeactivation);
        messageHandlers.put("StaffExpiredMassage", this::handlePartnerDunningDocumentDeactivationStaff);
        messageHandlers.put("CustomerStatusInActiveMessage", this::handleCustomerStatusInactivateNotification);
        messageHandlers.put("CustDocumentVerificationMsg", this::handleCustomerDocumentVerificationNotification);
        messageHandlers.put("EmailMessage", this::handleEmailNotificationForCustomerWithLeadQuotation);
        messageHandlers.put("CustServiceActiveMsg", this::handleCustomerServiceActiveNotification);
        messageHandlers.put("CustServiceInActiveMsg", this::handleCustomerServiceInactiveNotification);
        messageHandlers.put("CustChangePasswordMsg", this::handleCustomerChangePasswordNotification);
        messageHandlers.put("CustomerExpiredDocumentMessage", this::handleCustomerDunningDocument);
        messageHandlers.put("CustAddressShiftingMsg:" + KafkaConstant.CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION, this::handleCustomerOpenAddressShiftingNotification);
        messageHandlers.put("CustAddressShiftingMsg:" + KafkaConstant.CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION, this::handleCustomerCloseAddressShiftingNotification);
        messageHandlers.put("CustPaymentVerificationMsg", this::handleCustomerPaymentVerificationNotification);
        messageHandlers.put("CustTicketCloseMsg", this::handleCustomerTicketCloseNotification);
        messageHandlers.put("CustomMessage", this::handleApigwCustomerNotification);
        messageHandlers.put("LeadCreationMsg", this::handleLeadCreationNotification);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.TICKET_TAT_SUCCESS_MESSAGE, this::handleTicketTatSuccessMessage);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.TASK_TAT_SUCCESS_MESSAGE, this::handleTaskTatSuccessMessage);
        messageHandlers.put("TicketPickMessageToTeam:" + KafkaConstant.TAT_SEND_PARENT_TO_TEAM, this::handleTicketNoResponseMessage);
        messageHandlers.put("TicketPickMessageToTeam:" + KafkaConstant.TAT_SEND_PARENT_TO_TEAM_FOR_TASK, this::handleTaskNoResponseMessage);
        messageHandlers.put("SendFollowUpRemarkMsg", this::handleTicketFollowupRemarkCustomer);
        messageHandlers.put("CustomerQuotaNotificationMessage:" + KafkaConstant.SEND_QUOTA_NOTIFICATION_CUSTOMER, this::handleSendQuotaNotificationCustomer);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleMvnoCreateDataShareNotificationMicroservice);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleMvnoUpdateDataShareNotificationMicroservice);
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleBusinessUnitCreateDataShareNotification);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleBusinessUnitUpdateDataShareNotification);
        messageHandlers.put("TicketExternalRemarkCustomerMessage:" + KafkaConstant.SEND_EXTERNAL_REMARK_FOR_TICKET, this::handleExternalTicketRemarkToCustomer);
        messageHandlers.put("TicketExternalRemarkCustomerMessage:" + KafkaConstant.SEND_EXTERNAL_REMARK_FOR_TASK, this::handleExternalTaskRemark);
        messageHandlers.put("CustomerInvoiceMessage", this::handleSendInvoiceToNotification);
        messageHandlers.put("CustomerQuotaNotificationMessage:" + KafkaConstant.SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER, this::handleCustQuotaExhaustNotification);
        messageHandlers.put("TicketAlertStaffMessage:" + KafkaConstant.SEND_TICKET_ALERT_REMARK, this::handleTicketAlertForStaffMessage);
        messageHandlers.put("TicketAlertStaffMessage:" + KafkaConstant.SEND_TASK_ALERT_REMARK, this::handleTaskAlertForStaffMessage); //task alert
        messageHandlers.put("ImmediateAttentionForUnRegisterCustomerMessage", this::handleImmediateAttentionForUnregisterCustomers);
        messageHandlers.put("ImmediateAttentionForRegisterCustomerMessage", this::handleImmediateAttentionForRegisterCustomers);
        messageHandlers.put("ImmediateAttentionForUnRegisterCustomerToStaffMessage", this::handleImmediateAttentionForUnRegisterCustomerToStaff);
        messageHandlers.put("UnPickTicketAlertStaffMessage:"+ KafkaConstant.SEND_TASK_ALERT_REMARK, this::handleUnpickedTickerAlertToStaff); // Unpicked Ticket alert for staff
        messageHandlers.put("InventoryApprovalSuccessMsg", this::handleInventoryApprovalSuccess);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.CAF_TAT_SUCCESS_MESSAGE, this::handleCafTatSuccess);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.TREMINATION_TAT_SUCCESS_MESSAGE, this::handleCafTerminationTatSuccess);
        messageHandlers.put("TicketAssignMessege:" + KafkaConstant.LEAD_TAT_SUCCESS_MESSAGE, this::handleLeadTatSuccess);
        messageHandlers.put("MvnoDocumentDunningMessage:" + KafkaConstant.SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION, this::handleMvnoDocumentDunningNotification);
        messageHandlers.put("MvnoExpiryMessage:" + KafkaConstant.SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION, this::handleMvnoDeactivationNotification);
        messageHandlers.put("MvnoPaymentDunningMessage:" + KafkaConstant.SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION, this::handleMvnoPaymentAdvanceNotification);
        messageHandlers.put("MvnoPaymentDunningMessage:" + KafkaConstant.SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION, this::handleMvnoPaymentReminderNotification);
        messageHandlers.put("UpdateMvnoData:" + KafkaConstant.IPS_TO_ISP, this::handleUpdateMvnoDetialsFromCommonToNotification);
        messageHandlers.put("PlanExpiryNotificationMessage", this::handlePlanExpiryNotification);
        messageHandlers.put("DevicePortNotificationMessage", this::handleDevicePortNotification);
        messageHandlers.put("ChangePlanNotification", this::handleChangePlanDataShareNotification);
        messageHandlers.put("InventoryRequestMessage", this::handleInventoryRequestSuccess);
        messageHandlers.put("InventoryThresholdMessage", this::handleInventoryThreshold);
        messageHandlers.put("InventoryFulfilmentMessage", this::handleInventoryFulfilmentSuccess);
        messageHandlers.put("SendFollowUpRemarkMsg:"+KafkaConstant.FOLLOWUP_TASK_MSG,this::handleTaskRemark);
        messageHandlers.put("SendFollowUpRemarkMsg",this::handleTaskRemark);
        messageHandlers.put("TicketCreationMessage:"+KafkaConstant.TASK_RESCHEDUKE_NOTIFICATION, this::handleTaskRescheduleSuccess);
        messageHandlers.put("TicketCreationMessage:"+KafkaConstant.TASK_FOR_CUSTOMER, this::handleTaskCustomerCreationSuccess);
        messageHandlers.put("TicketCreationMessage:"+KafkaConstant.TASK_FOR_STAFF, this::handleTaskCreationSuccess);
        messageHandlers.put("TaskCloseMessage:TASK_CLOSE_MESSAGE", this::handleTaskCloseMessage);
        messageHandlers.put("TaskUpdateMessage:"+KafkaConstant.TASK_FOR_STAFF , this::handleTaskUpdateForStaff);
        messageHandlers.put("TaskUpdateMessage:"+KafkaConstant.TASK_FOR_CUSTOMER , this::handleTaskUpdateForCustomer);
        messageHandlers.put("TaskUpdateMessage", this::handleTaskUpdateForCustomer);
        messageHandlers.put("TicketTatReminderNotification:"+ KafkaConstant.TATNOTIFICATION,  this::handleTicketTatBreachedReminderScheduler);
        messageHandlers.put("ChildCustomerRegistrationSuccessMsg", this::handleBssChildCustomerRegistrationSuccess);
        messageHandlers.put("CustInsufficientWalletMessage", this::handleInsufficientWalletMessage);
        messageHandlers.put("AutoRenewalPreferenceMessage", this::handleAutoRenewalPreferenceMessage);
    }


    @Transactional
    @Override
    public void run() {
        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig.packetDataPropsPrimary(),
                        new StringDeserializer(), deserializer);

        try (KafkaConsumer<String, KafkaMessageData> primaryConsumer = (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {
            primaryConsumer.subscribe(Arrays.asList(
                    KafkaConstant.KAFKA_COMMON_TOPIC,
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA,
                    KafkaConstant.KAFKA_SALES_CRM_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC,
                    KafkaConstant.KAFKA_TASK_TOPIC
            ));

            while (true) {
                long startTime = System.currentTimeMillis();

                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
                log.info("Kafka records received::::::::::::::: " + records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {
                    try {
                        KafkaMessageData message = record.value();
                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        String keyWithEventType = dataType + ":" + eventType;

                        Consumer<KafkaMessageData> handler = messageHandlers.getOrDefault(keyWithEventType, messageHandlers.get(dataType));

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
                            Consumer<KafkaMessageData> finalHandler = handler;
                            Span span = tracer.nextSpan().name("KafkaMessageProcessing").start();
                            CompletableFuture.runAsync(() -> {
                                try (Tracer.SpanInScope innerScope = tracer.withSpanInScope(span)) {
                                    finalHandler.accept(message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    span.error(e);
                                } finally {
                                    span.finish();
                                }
                            }, executor);
                        }

                        log.debug("Received message: dataType = " + dataType + ", eventType = " + eventType);
                    } catch (Exception e) {
                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
                    }
                }

                try {
                    primaryConsumer.commitSync(); // safer manual commit
                } catch (Exception e) {
                    log.error("Commit failed", e);
                }

                long endTime = System.currentTimeMillis();
                log.debug("Poll processing time (ms): " + (endTime - startTime));
            }
        } catch (Exception e) {
            log.error("Kafka Error: " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormSalseCRMMicroService(KafkaMessageData message) {
//
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
//
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCustomerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From CMS-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC}, groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormPartnerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Partner-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC}, groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Inventory-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC}, groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Revenue-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC}, groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Radius-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC}, groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Ticket-Micro-Service: " + e.getMessage(), e);
//        }
//    }


    public void setUserProperties(String traceId, String spanId) {
        if (traceId != null)
            MDC.put("traceId", traceId);
        if (spanId != null)
            MDC.put("spanId", spanId);
    }


    public void handleLoginSuccess(KafkaMessageData message) {
        try {
            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            //		//System.out.println("Received Message From RabbitMq : <" + message + ">");
            user = dataMessage.getCustomerData().get("userName").toString();
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("id"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LOGIN_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Customer Login Success", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleLoginFailure(KafkaMessageData message) {
        try {
            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("id"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LOGIN_FAILURE, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Customer Login Failure", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleRegistrationSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
//            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);
            objectMapper.registerModule(new JavaTimeModule());
            CustomerMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
//		//System.out.println("Received Message From RabbitMq : <" + message + ">");
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("id"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Customer Registration Success", LocalDateTime.now(), message.toString());
            }
//	smsService.sendSmsNotification(RabbitMqConstants.QUEUE_REGISTRATION_SUCCESS, message.getMessage(),
//			message.getCustomerData(), message.getSourceName(),message.getSmsTemplate(),message.getAppendUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleInsufficientWalletMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
//            PaymentSuccess dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PaymentSuccess.class);

            CustInsufficientWalletMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustInsufficientWalletMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("userName"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
          Optional<Template> template = templateRepository.findByTemplateNameAndMvnoId("Insufficient Wallet" , Integer.valueOf(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                System.out.println("***/**/*********** Not sufficient amount to send kafka***********/**/***"+dataMessage.getMessage());
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INSUFFICIENT_WALLET, dataMessage.getMessage(),
                        dataMessage.getCustomerData(),null, null,
                      null, null, template.get().isEmailEventConfigured(),
                        template.get().isSmsEventConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "insufficient wallet", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAutoRenewalPreferenceMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
//            PaymentSuccess dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PaymentSuccess.class);

            AutoRenewalPreferenceMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), AutoRenewalPreferenceMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("userName"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            Optional<Template> template = templateRepository.findByTemplateNameAndMvnoId("Auto-Renewal Preference Changed" , Integer.valueOf(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_AUTO_RENEWAL_PREFERENCE_CHANGED, dataMessage.getMessage(),
                        dataMessage.getCustomerData(),null, null,
                        null, null, template.get().isEmailEventConfigured(),
                        template.get().isSmsEventConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "auto renewal preference changed", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRegistrationFailure(KafkaMessageData message) {
        try {
            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("id"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_REGISTRATION_FAILURE, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Registration Failure", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleOtpGeneration(KafkaMessageData message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            OtpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), OtpMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_OTP_GENERATION_COMMON, dataMessage.getMessage(),
                    dataMessage.getOtpData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(), dataMessage.getSmsTemplate(),
                    dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(), dataMessage.isSmsConfigured());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void handleSendVoucherCode(KafkaMessageData message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //System.out.println("Received Message From RabbitMq : <" + message + ">");
        // MDC.put(NotificationConstants.USER_NAME, message.getCurrentUser());
        // MDC.put("traceId", message.getTraceId());
        // MDC.put("spanId", message.getSpanId());
        try {
            VoucherCodeMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), VoucherCodeMessage.class);

            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            smsService.sendSmsNotification(RabbitMqConstants.QUEUE_SEND_VOUCHERCODE, dataMessage.getMessage(),
                    dataMessage.getVoucherData(), dataMessage.getSourceName(), dataMessage.getSmsTemplate(),
                    dataMessage.getAppendUrl());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void handleStaffSuccess(KafkaMessageData message) {
        try {
            StaffMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), StaffMessage.class);
            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            staffService.saveRoleAndStaff(RabbitMqConstants.QUEUE_STAFF_SUCCESS, dataMessage.getStaff(),
                    dataMessage.getRoleScreenList(), dataMessage.isUpdate(), dataMessage.isDelete(), dataMessage.getActualName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRoleList(KafkaMessageData message) {
        try {
            RoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), RoleMessage.class);

            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            roleService.saveRole(RabbitMqConstants.QUEUE_ROLE_SUCCESS, dataMessage.getRole(), dataMessage.getRoleScreenList(),
                    dataMessage.isUpdate(), dataMessage.isDelete(), dataMessage.getRoleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUsedQuota(KafkaMessageData message) {
        try {
            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);

            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("id"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_USED_QUOTA, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Customer Used quota", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleConfigurationAdd(KafkaMessageData message) {
        try {
            ConfigMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ConfigMessage.class);

            setUserProperties(dataMessage.getTraceId(), dataMessage.getSpanId());
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            configService.saveAndUpdateSystemConfigFromCommon(dataMessage.getSystemConfig(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerApprovalSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustApprovalMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustApprovalMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            //System.out.println("approverName : " + message.getApproverTeam());
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Approval Success", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerApprovalFail(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustApprovalMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustApprovalMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            //System.out.println("username: " + message.getCustomerData().get("username"));
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_APPROVAL_FAIL, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Approval Fail", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerRenewalSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerRenewalMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerRenewalMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            //System.out.println("renew plan : " + message.getPlan());
            //System.out.println("plan type : " + message.getPurchaseType());
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Renewal Message", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerRechargeSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerRechargeMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerRechargeMessage.class);

            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Recharge", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerRegistrationSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerRegistrationSuccessMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerRegistrationSuccessMsg.class);

            try {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Registration Success", LocalDateTime.now(), message.toString());
                //}
            } catch (Exception e) {
                e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerPaymentLink(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            PaymentLinkMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PaymentLinkMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            //System.out.println("username: " + message.getCustomerData().get("username"));
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("customerName"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_LINK, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Payment Link", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerPaymentSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            PaymentSuccess dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PaymentSuccess.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("userName"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("userName"), "Customer Payment Success", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketAssignTeamSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketAssignMessege dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketAssignMessege.class);
            System.out.println("=========Task+Ticket received successfully ====="+dataMessage.getMessage());
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ASSIGN_TEAM_SUCCESS, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCustomerDunning(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerDunningMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerDunningMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DUNNING, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Dunning", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBssCustomerDeactivation(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerDeactivationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerDeactivationMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CUSTOMER_DEACTIVATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Dunning Deactivation", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerOtpRegistration(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerOtpRegistrationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerOtpRegistrationMessage.class);

            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "OTP Registartion", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBssDocumentDunningStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            StaffExpiredMassage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), StaffExpiredMassage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_DOCUMENT_DUNNING_STAFF, dataMessage.getMessage(),
                    dataMessage.getStaffUserData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSalesCrmsBssFollowUpReminderStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            System.out.println("**********Received follwup reminder for staff*************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleSalesCrmsBssFollowUpReminderCustomer(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            System.out.println("************ Followup reminder received for customers***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleSalesCrmsBssFollowUpOverDueStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            System.out.println("************ Followup over due reminder received for Staff***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleSalesCrmsBssFollowUpOverDueParentStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            System.out.println("************ Followup overdue reminder received for Parent Staff***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void handleStaffSendStatus(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            StaffStatusChangeMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), StaffStatusChangeMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_STAFF_SEND_STATUS, dataMessage.getMessage(), dataMessage.getStaffUserData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(), dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(), dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleSalesCrmsBssNoLeadFollowUpReminderStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            System.out.println("************ Lead Followup reminder received for Staff ***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleSalesCrmsBssNoLeadFollowUpReminderParentStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            System.out.println("************No Lead Followup reminder received for Parent Staff ***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleSalesCrmsBssNoFollowUpReminderStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);
            System.out.println("************ No Followup reminder received for Staff ***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleSalesCrmsBssNoFollowUpReminderParentStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);

            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleSendCustomerStatusChange(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            FollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), FollowUpMessage.class);

            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_CHANGE,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Ticket Status Change", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleWorkflowActionAssignMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            WorkflowTicketMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), WorkflowTicketMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleSendFollowupRemark(KafkaMessageData message) {
        try {
            SendFollowUpRemarkMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendFollowUpRemarkMsg.class);
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleSendProblemDomainChange(KafkaMessageData message) {
        try {
            SendFollowUpRemarkMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendFollowUpRemarkMsg.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_TASK_CATEGORY_CHANGE_MSG,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleSendTaskCategoryChange(KafkaMessageData message) {
        try {
            SendFollowUpRemarkMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendFollowUpRemarkMsg.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            System.out.println("************ Task Category change message received at notification service  ***************"+dataMessage.getMessage());
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_TASK_CATEGORY_CHANGE_MSG,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleTicketEtr(KafkaMessageData message) {
        try {
//            TicketETRMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketETRMsg.class);
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketETRMsg dataMessage=objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketETRMsg.class);
            System.out.println("***************Ticket ETR received at notification service****************"+ dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ETR,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTaskEtr(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketETRMsg dataMessage=objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketETRMsg.class);
            System.out.println("Received Task ETR Message From Kafka : <" + dataMessage + ">");
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TASK_ETR,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCafFollowUpReminderStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CafFollowUpMessage.class);
            System.out.println("***************CAF Followup reminder for staff received in notification service****************"+ dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCafFollowUpReminderCustomer(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CafFollowUpMessage.class);

            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                System.out.println("***************CAF Followup reminder for customer received in notification service****************"+ dataMessage);
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCafFollowUpOverDueStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CafFollowUpMessage.class);
            System.out.println("***************CAF Followup overdue reminder for staff received in notification service****************"+ dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleBssCafFollowUpOverDueParentStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CafFollowUpMessage.class);
            System.out.println("***************CAF Followup overdue reminder for Parent Staff received in notification service****************"+ dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTroubleTicketFollowUpReminderStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketFollowUpMessage.class);
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            System.out.println("*********************Ticket Follow up Reminder For Staff received at notification service****************************************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTroubleTicketFollowUpReminderCustomer(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CafFollowUpMessage.class);

            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                System.out.println("*********************Trouble Ticket Follow up Reminder For Customer received at notification service****************************************"+dataMessage);
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTroubleTicketFollowUpOverDueStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketFollowUpMessage.class);
            System.out.println("*********************Trouble Ticket Follow up overdue Reminder For Staff received at notification service****************************************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTroubleTicketFollowUpOverDueParentStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketFollowUpMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketFollowUpMessage.class);
            System.out.println("*********************Trouble Ticket Follow up overdue Reminder For Parent staff received at notification service****************************************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTicketCreationSuccess(KafkaMessageData message) {
        try {
            TicketCreationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketCreationMessage.class);
            System.out.println("*********************Ticket Creation notification received at notification service****************************************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_CREATION_SUCCESS,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTaskCreationSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketCreationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketCreationMessage.class);
            System.out.println("*************************Task Creation notification received*****************"+dataMessage);
            //TicketCreationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketCreationMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.TASK_CREATION_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTaskCloseMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
             TaskCloseMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TaskCloseMessage.class);

            //TicketCreationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketCreationMessage.class);
            System.out.println("******************Task close notification for assinee staff/Linked Customer/staff****************************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.TASK_CLOSED_EVENT,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketRescheduleSuccessMsg(KafkaMessageData message) {
        try {
            TicketRescheduleMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketRescheduleMsg.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketTatBreachedReminder(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketTatReminderNotifcation dataMessage =objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketTatReminderNotifcation.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_TAT_BREACHED_REMINDER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDunningAdvanceNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerDunningMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerDunningMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_DUNNING_ADVANCE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Dunning Advance Notification", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePartnerDunningDocument(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            PartnerExpiredDocumentMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PartnerExpiredDocumentMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT, dataMessage.getMessage(),
                    dataMessage.getPartnerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePartnerDunningDocumentDeactivation(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            PartnerExpiredDocumentMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PartnerExpiredDocumentMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION, dataMessage.getMessage(),
                    dataMessage.getPartnerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handlePartnerDunningDocumentDeactivationStaff(KafkaMessageData message) {
        try {
            StaffExpiredMassage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), StaffExpiredMassage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF, dataMessage.getMessage(),
                    dataMessage.getStaffUserData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleCustomerStatusInactivateNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerStatusInActiveMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerStatusInActiveMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Status InActive", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerDocumentVerificationNotification(KafkaMessageData message) {
        try {
            CustDocumentVerificationMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustDocumentVerificationMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Document Verify", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleEmailNotificationForCustomerWithLeadQuotation(KafkaMessageData message) {
        try {
            EmailMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), EmailMessage.class);
            emailService.sendEmailNotificationWithAttachment(RabbitMqConstants.EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerServiceActiveNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustServiceActiveMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustServiceActiveMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Service Activation", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerServiceInactiveNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustServiceInActiveMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustServiceInActiveMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Service Inactivation", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerChangePasswordNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustChangePasswordMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustChangePasswordMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Change Password", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerDunningDocument(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerExpiredDocumentMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerExpiredDocumentMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsername((String) dataMessage.getPartnerData().get("partnerName"));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_DUNNING_DOCUMENT, dataMessage.getMessage(),
                        dataMessage.getPartnerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getPartnerData().get("partnerName"), "Customer Document Dunning", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerOpenAddressShiftingNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustAddressShiftingMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustAddressShiftingMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Open Address Shifting", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerCloseAddressShiftingNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustAddressShiftingMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustAddressShiftingMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Close Address Shifting", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerPaymentVerificationNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustPaymentVerificationMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustPaymentVerificationMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            System.out.println("****************** Customer payment varification received**********"+dataMessage);
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Payment Verify", LocalDateTime.now(), message.toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCustomerTicketCloseNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustTicketCloseMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustTicketCloseMsg.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Ticket Close", LocalDateTime.now(), message.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleApigwCustomerNotification(KafkaMessageData message) {
        try{
            ThreadPoolUtilityClass threadPoolUtilityClass = new ThreadPoolUtilityClass();
            threadPoolUtilityClass.sacveCustomerProcess(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void handleLeadCreationNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            LeadCreationMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), LeadCreationMsg.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LEAD_CREATION_NOTIFICATION, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
            notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("firstname"), "Lead Creation Success", LocalDateTime.now(), message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketTatSuccessMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketAssignMessege dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketAssignMessege.class);
            System.out.println("*****************Ticket TAT Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_TAT_SUCCESS_MESSAGE, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTaskTatSuccessMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketAssignMessege dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketAssignMessege.class);
            System.out.println("***************** TAT Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TASK_TAT_SUCCESS_MESSAGE, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTicketNoResponseMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketPickMessageToTeam dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketPickMessageToTeam.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTaskNoResponseMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketPickMessageToTeam dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketPickMessageToTeam.class);
            System.out.println("*******************TATNOTIFICATIONTOTEAMFORTASK successfully received in notification service*************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTicketFollowupRemarkCustomer(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendFollowUpRemarkMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendFollowUpRemarkMsg.class);
            System.out.println("*****************Ticket followup remark for customers Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSendQuotaNotificationCustomer(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerQuotaNotificationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerQuotaNotificationMessage.class);
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMvnoCreateDataShareNotificationMicroservice(KafkaMessageData message) {
        //System.out.println("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            //System.out.println("MVNO Created Successfully From Rms");
        } catch (Exception e) {
            //System.out.println("receiveMessageCreateMVNO Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void handleMvnoUpdateDataShareNotificationMicroservice(KafkaMessageData message) {
        //System.out.println("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            //System.out.println("MVNO Updated Successfully From Rms");
        } catch (Exception e) {
            //System.out.println("receiveMessageUpdateMVNO Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void handleBusinessUnitCreateDataShareNotification(KafkaMessageData message) {
        //System.out.println("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveBusinessUnitSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusineeUnit(dataMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBusinessUnitUpdateDataShareNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateBusinessUnitSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnit(dataMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleExternalTicketRemarkToCustomer(KafkaMessageData message) {
        try {
            TicketExternalRemarkCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketExternalRemarkCustomerMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleExternalTaskRemark(KafkaMessageData message) {
        try {
            TicketExternalRemarkCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketExternalRemarkCustomerMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_EXTERNAL_TASK_REMARK,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSendInvoiceToNotification(KafkaMessageData message) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerInvoiceMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerInvoiceMessage.class);
            emailService.sendEmailNotificationWithAttachmentForAllEvents(RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleCustQuotaExhaustNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerQuotaNotificationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerQuotaNotificationMessage.class);
            //System.out.println("Received Message From RabbitMq : <" + message + ">");
            Boolean flag = customerService.isCustomerNotificationEnableByUsernameAndMvnoId((String) dataMessage.getCustomerData().get("username"), Long.parseLong(dataMessage.getCustomerData().get("mvnoId").toString()));
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleTicketAlertForStaffMessage(KafkaMessageData message) {
        try {
            TicketAlertStaffMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketAlertStaffMessage.class);
            System.out.println("*****************Ticket alert for staff Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TICKET_ALERT_TO_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleTaskAlertForStaffMessage(KafkaMessageData message) {
        try {
            TicketAlertStaffMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketAlertStaffMessage.class);
            System.out.println("*****************Task alert for staff Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TASK_ALERT_TO_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleImmediateAttentionForUnregisterCustomers(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ImmediateAttentionForUnRegisterCustomerMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ImmediateAttentionForUnRegisterCustomerMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleImmediateAttentionForRegisterCustomers(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ImmediateAttentionForRegisterCustomerMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ImmediateAttentionForRegisterCustomerMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleImmediateAttentionForUnRegisterCustomerToStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ImmediateAttentionForUnRegisterCustomerToStaffMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ImmediateAttentionForUnRegisterCustomerToStaffMessage.class);
            //		System.out.println("Received Message From RabbitMq : <" + message + ">");
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleUnpickedTickerAlertToStaff(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UnPickTicketAlertStaffMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UnPickTicketAlertStaffMessage.class);
            System.out.println("*****************Unpicked Ticket alert for staff Notification successfully received**************"+dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_UNPICK_TICKET_ALERT_TO_STAFF,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleInventoryApprovalSuccess(KafkaMessageData message) {
        try {
            InventoryApprovalSuccessMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryApprovalSuccessMsg.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleInventoryRequestSuccess(KafkaMessageData message) {
        try {
            InventoryRequestMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryRequestMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INVENTORY_REQUEST_TO_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleInventoryThreshold(KafkaMessageData message) {
        try {
            InventoryThresholdMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryThresholdMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INVENTORY_THRESHOLD_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null, null, null,
                    true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleInventoryFulfilmentSuccess(KafkaMessageData message) {
        try {
            InventoryFulfilmentMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), InventoryFulfilmentMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_INVENTORY_FULFILMENT_TO_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), null,
                    null, null, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleCafTatSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketAssignMessege dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketAssignMessege.class);
//            TicketAssignMessege dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketAssignMessege.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CAF_TAT_SUCCESS_MESSAGE, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleCafTerminationTatSuccess(KafkaMessageData message) {
        try {
            TicketAssignMessege dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketAssignMessege.class);

            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleLeadTatSuccess(KafkaMessageData message) {
        try {
            TicketAssignMessege dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketAssignMessege.class);

            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_LEAD_TAT_SUCCESS_MESSAGE, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleMvnoDocumentDunningNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            MvnoDocumentDunningMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), MvnoDocumentDunningMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
            notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Mvno Document Dunning", LocalDateTime.now(), message.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMvnoDeactivationNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            MvnoExpiryMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), MvnoExpiryMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
            notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Mvno Document Dunning", LocalDateTime.now(), message.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void handleMvnoPaymentAdvanceNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            MvnoPaymentDunningMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), MvnoPaymentDunningMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
            notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "MVNO Payment", LocalDateTime.now(), message.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleMvnoPaymentReminderNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            MvnoPaymentDunningMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), MvnoPaymentDunningMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION, dataMessage.getMessage(),
                    dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
            notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "MVNO Payment", LocalDateTime.now(), message.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUpdateMvnoDetialsFromCommonToNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateMvnoData dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoData.class);
            mvnoService.updateMvnoIdIsptoIsp(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
        } catch (Exception e) {
            log.error("RecievedMessage update failed: " + message);
            e.printStackTrace();
        }
    }


    public void handlePlanExpiryNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            PlanExpiryNotificationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), PlanExpiryNotificationMessage.class);
            System.out.println("Received Message From kafka for Plan Expiry Notification : <" + dataMessage + ">");
            System.out.println("username: " + dataMessage.getCustomerData().get("username"));
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("custId"));
            System.out.println("***************Plan expiry notification received*******************"+dataMessage);
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleDevicePortNotification(KafkaMessageData message) {
        System.out.println("Received Message From kafka : <" + message + ">");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            DevicePortNotificationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), DevicePortNotificationMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getStaffData(), dataMessage.getSourceName(), null,
                    null, null, true, false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("receiveDeviceInputPortUsedNotificationForStaffMsg Failed:- " + e.getMessage());
        }
    }


    public void handleChangePlanDataShareNotification(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ChangePlanNotification dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ChangePlanNotification.class);
            System.out.println("Received Message From kafka for Plan Expiry Notification at the time of Change Plan : <" + dataMessage + ">");
            System.out.println("username: " + dataMessage.getCustomerData().get("username"));
            Boolean flag = customerService.isCustomerNotificationEnable((Integer) dataMessage.getCustomerData().get("custId"));
            System.out.println("***************Plan expiry notification at the time of Change Plan received*******************"+dataMessage);
            if (flag) {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION, dataMessage.getMessage(),
                        dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageForCustomerData(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TASK_TOPIC}, groupId = KafkaConstant.KAFKA_TASK_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageForTaskData(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task  Receive Kafka Error Message From Task-Micro-Service : " + e.getMessage());
//        }
//    }
    private void handleTaskRemark(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendFollowUpRemarkMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendFollowUpRemarkMsg.class);
            emailService.sendEmailNotification(RabbitMqConstants.FOLLOWUP_TASK_MSG,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTaskRescheduleSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketCreationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketCreationMessage.class);
            System.out.println("************************Task Reschedule received in Notification service*********************"+ dataMessage);
            emailService.sendEmailNotification(RabbitMqConstants.TASK_RESCHEDUKE_NOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleTaskCustomerCreationSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketCreationMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketCreationMessage.class);

            //TicketCreationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketCreationMessage.class);
            emailService.sendEmailNotification(RabbitMqConstants.TASK,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleTaskUpdateForCustomer(KafkaMessageData message)  {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                TaskUpdateMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TaskUpdateMessage.class);
                System.out.println("****************taskUpdateMessage for customer received in notification service*********************"+ dataMessage);
                //TicketCreationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TicketCreationMessage.class);
                emailService.sendEmailNotification(RabbitMqConstants.TASK_UPDATE,
                        dataMessage.getMessage(), dataMessage.getData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
            } catch (Exception e) {
                e.getMessage();
            }

    }

        private void handleTaskUpdateForStaff (KafkaMessageData message){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    TaskUpdateMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TaskUpdateMessage.class);
                    System.out.println("****************taskUpdateMessage for staff received in notification service*********************"+ dataMessage);
                    emailService.sendEmailNotification(RabbitMqConstants.TASK,
                            dataMessage.getMessage(), dataMessage.getData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                            dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                            dataMessage.isSmsConfigured());
                } catch (Exception e) {
                    e.getMessage();
                }
    }


    public void handleTicketTatBreachedReminderScheduler(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TicketTatReminderNotifcation dataMessage =objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TicketTatReminderNotifcation.class);
            emailService.sendEmailNotification(KafkaConstant.TATNOTIFICATION,
                    dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                    dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                    dataMessage.isSmsConfigured());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBssChildCustomerRegistrationSuccess(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ChildCustomerRegistrationSuccessMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ChildCustomerRegistrationSuccessMsg.class);

            try {
                emailService.sendEmailNotification(RabbitMqConstants.QUEUE_BSS_CHILD_CUSTOMER_REGISTRATION_SUCCESS,
                        dataMessage.getMessage(), dataMessage.getCustomerData(), dataMessage.getSourceName(), dataMessage.getEmailTemplate(),
                        dataMessage.getSmsTemplate(), dataMessage.getAppendUrl(), dataMessage.isEmailConfigured(),
                        dataMessage.isSmsConfigured());
                notificationAuditService.saveNotificationAudit((String) dataMessage.getCustomerData().get("username"), "Customer Registration Success", LocalDateTime.now(), message.toString());
                //}
            } catch (Exception e) {
                e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
