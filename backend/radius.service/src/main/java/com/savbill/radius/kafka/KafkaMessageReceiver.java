package com.savbill.radius.kafka;

import com.savbill.radius.CronJobs.PostPaidPlanExpireryJob;
import com.savbill.radius.aaa.data.CustomerCreateData;
import com.savbill.radius.aaa.server.RadiusAsyncUtility;
import com.savbill.radius.entity.CustPlanMappping;
import com.savbill.radius.entity.PostpaidPlan;
import com.savbill.radius.kafka.message.*;
import com.savbill.radius.mvno.Service.MvnoService;
import com.savbill.radius.rabbitmq.message.UpdateCustplanMappingMessage;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustQuotaDetailsRepository;
import com.savbill.radius.services.PrepaidInvoiceService;
import com.savbill.radius.services.RolesService;
import com.savbill.radius.services.impl.*;
import com.savbill.radius.utils.DateTimeUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
public class KafkaMessageReceiver implements Runnable {

    @Autowired
    private BusinessUnitServiceImpl businessUnitService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private CustomerServiceImpl customerServiceImpl;

    @Autowired
    private CustomersServiceImpl customersService;

    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    @Autowired
    private StaffUserServiceImpl staffUserService;

    @Autowired
    private ServiceAreaImpl serviceArea;

    @Autowired
    private CustPlanMappingServiceImpl custPlanMappingService;

    @Autowired
    private PostpaidPlanServiceImpl postpaidPlanService;

    @Autowired
    private QosPolicyServiceImpl qosPolicyService;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustomerTimeBasePolicyServiceImpl customerTimeBasePolicyService;

    @Autowired
    private TimeBasePolicyServiceImp timeBasePolicyService;

    @Autowired
    private TimeBasePolicyDetailServiceImpl timeBasePolicyDetailServiceImp;

    @Autowired
    private CustServiceChargeIPDtlsServiceImpl custServiceChargeIPDtlsServiceImpl;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private ConfigurationServicesImpl configurationServices;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private PostPaidPlanExpireryJob postPaidPlanExpireryJob;

    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    KafkaConsumerConfig consumerConfig;


    private static final Logger log = LoggerFactory.getLogger("savbillradiusqueue");

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public KafkaMessageReceiver() {
        /**Common Receiver started**/
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleSaveBusinessUnit);
        //messageHandlers.put("StaffUserMessage",this::handleSaveStaffUser);
        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleSaveStaffUser);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleSaveMvno);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleUpdateBusinessUnit);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleUpdateMvno);
        messageHandlers.put("UpdateMvnoData:IPS_TO_ISP", this::handleUpdateMvnoData);
        messageHandlers.put("CommonRoleMessage:CREATE_DATA_ROLE", this::handleSaveCommonRole);
        messageHandlers.put("CommonRoleMessage:DELETE_DATA_ROLE", this::handleDeleteCommonRole);
        messageHandlers.put("ServiceAreaMesseage", this::handleServiceArea);
        messageHandlers.put("SaveClientServMessge", this::handleSystemConfiguration);
        messageHandlers.put("UpdateClientServMessage:UPDATE_SERVICE_CONFIG", this::handleSystemConfiguration);
        messageHandlers.put("SaveStaffAssignmentMessage", this::handleStaffUserServiceAreaMapping);

        /**Common Receiver ended**/

        /**Cms Receiver started**/
        messageHandlers.put("CustomMessage:CUSTOMER_CREATE", this::handleCustomerCreate);
//        messageHandlers.put("CustomMessage", this::handleCustomerCreate);
        messageHandlers.put("CustomMessage:UPDATE_CUSTOMER_PASSWORD", this::handleCustomerPasswordUpdate);
        messageHandlers.put("QuotaCustomMessage", this::handleWifiCustomerUpdateQuota);
        messageHandlers.put("CustPackageRelMessage", this::handleSaveCustPlanMapping);
        messageHandlers.put("QosPolicyMessage", this::handleSaveCustQosPolicy);
        messageHandlers.put("PostpaidPlanMessage", this::handleSavePostpaidPlan);
        messageHandlers.put("PostpaidPlanMessage:BulkUpdate", this::handleSavePostpaidPlanInBulk);
        messageHandlers.put("CustMacMappingMessage", this::handleDeleteOrUpdateCustMacMapping);
        messageHandlers.put("CustomerTimeBasePolicyDetailsMessage", this::handleCustomerTimeBasePolicyDetails);
        messageHandlers.put("TimeBasePolicyMessage", this::handleTimeBasePolicy);
        messageHandlers.put("TimeBasePolicyDetailsListMessage", this::handleTimeBasePolicyDetails);
        messageHandlers.put("CustomerPackageRelMessage", this::handleUpdateCustPlanMapping);
        messageHandlers.put("CustServiceChargeIPDtlsMessage", this::handleSaveCustServiceChargeIPDtls);
        messageHandlers.put("CustServiceChargeIPDtlsMessage:UPDATE_CUST_SERVICE_CHARGE_IP_DTLS", this::handleUpdateCustServiceChargeIPDtls);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleUpdateCustomerShareData);
        messageHandlers.put("CustIPMessage:CUSTOMER_IP_TO_UPDATE_RADIUS", this::handleUpdateCustomerIP);
        messageHandlers.put("CustIPMessage:CUSTOMER_IP_TO_SAVE_RADIUS", this::handleSaveCustomerIP);
        messageHandlers.put("CustIPMessage:CUSTOMER_IP_TO_DELETE_RADIUS", this::handleDeleteCustomerIP);
        messageHandlers.put("MvnoStatusMessage", this::handleCustomerDeactivationWhenMvnoIsInActive);
        messageHandlers.put("CustomerUpdateMessage", this::handleCustomerUpdateStatus);
        messageHandlers.put("CaftoCustomerMessage", this::handleMessageCafToCustomere);
        messageHandlers.put("CaftoCustomerMessage:Service_Activation", this::handleCafToCustServiceActivation);
        messageHandlers.put("CustomerNextQuotaUpdateMessage", this::handleCustomerNextQuotaUpdate);
        messageHandlers.put("PlanUpdateMessage", this::handlePlanUpdateMessage);
        /**Cms Receiver ended**/

        /**Netconf Receiver started**/
        messageHandlers.put("CustomerCreateData:CUSTOMERS_CREATE", this::handleNetConfCustomerCreate);
        messageHandlers.put("Customers:CUSTOMERS_DEFOULTPROVISION", this::handleNetConfCustomerUpdate);
        messageHandlers.put("CustomerCreateData:CUSTOMERS_UPDATE", this::handleNetConfCustomerUpdateWithCustomer);
        messageHandlers.put("CustomerCreateData:CUSTOMERS_DEFOULTPROVISION", this::handleNetConfCustomerIPV4provision);
        messageHandlers.put("DefaultUpdate:CUSTOMERS_DEFOULTUPDATE", this::handleNetConfCustomerIPV4provisionUpdate);
        messageHandlers.put("CustomerStatusUpdateMessage", this::handleNetConfCustomerUpdateStatus);
        messageHandlers.put("DeleteCustomerMessage:CUSTOMERS_DEPROVISION", this::handleNetConfCustomerDelete);
        messageHandlers.put("DeleteCustomerMessage:CUSTOMERS_DEFOULTDEPROVISION", this::handleNetConfCustomerDeleteWithDefaultProvision);
        /**Netconf Receiver ended**/

        /**Revenue Receiver started**/
        messageHandlers.put("UpdateCprMessage", this::handleRevenueCPRUpdate);
        /**Revenue Receiver ended**/
        messageHandlers.put("UpdateCustplanMappingMessage", this::handleUpdateCustPlanMappinForVoidPlan);
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
//        try {
//            log.info("Enter in common microservice kafka receiver");
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            /** key with both dataType and eventType **/
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            /** handler with both dataType and eventType **/
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                /**  If not found, try to find the handler with only dataType **/
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
//            log.error("Radius Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC}, groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormPartnerMicroService(KafkaMessageData message) {
//        try {
//            if (message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Radius Service Receive Kafka Message From Partner-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Radius Service Receive Kafka Error Message From Partner-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC}, groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
//        try {
//            log.info("Enter in inventory microservice kafka receiver");
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            /** key with both dataType and eventType **/
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            /** handler with both dataType and eventType **/
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                /**  If not found, try to find the handler with only dataType **/
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
//            log.error("Radius Service Receive Kafka Error Message From Inventory-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC}, groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
//        try {
//            log.info("Enter in Revenue microservice kafka receiver");
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            /** key with both dataType and eventType **/
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            /** handler with both dataType and eventType **/
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                /**  If not found, try to find the handler with only dataType **/
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
//            log.error("Radius Service Receive Kafka Error Message From Revenue-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC}, groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        try {
//            if (message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Radius Service Receive Kafka Message From Notification-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Radius Service Receive Kafka Error Message From Notification-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCMSMicroService(KafkaMessageData message) {
//        try {
//            log.info("Enter in cms microservice kafka receiver");
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            /** key with both dataType and eventType **/
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            /** handler with both dataType and eventType **/
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                /**  If not found, try to find the handler with only dataType **/
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
//            log.error("Radius Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
//        }
//
//
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC}, groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
//        try {
//            if (message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Radius Service Receive Kafka Message From Ticket-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Radius Service Receive Kafka Error Message From Ticket-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NETCONFIG_TOPIC}, groupId = KafkaConstant.KAFKA_NETCONFIG_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNetConfigMicroService(KafkaMessageData message) {
//        try {
//            log.info("Enter in netconf microservice kafka receiver");
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            /** key with both dataType and eventType **/
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            /** handler with both dataType and eventType **/
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                /**  If not found, try to find the handler with only dataType **/
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
//            log.error("Radius Service Receive Kafka Error Message From Netconfig-Micro-Service : " + e.getMessage());
//        }
//    }

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

    /// /                handler.accept(message);
//                Consumer<KafkaMessageData> finalHandler = handler;
//                CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
//        }
//    }
    @Transactional
    @Override
    public void run() {
        // Create consumer factory with proper type information
        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig.packetDataPropsPrimary(),
                        new StringDeserializer(),
                        new JsonDeserializer<>(KafkaMessageData.class, false));

        // Create the consumer from factory
        try (KafkaConsumer<String, KafkaMessageData> primaryConsumer = (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {

            // Subscribe to topics
            primaryConsumer.subscribe(Collections.unmodifiableList(Arrays.asList(
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA,
                    KafkaConstant.KAFKA_NETCONFIG_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC,
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_COMMON_TOPIC)));

            long startTime = System.currentTimeMillis();

            while (true) {
                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
                log.debug(".......(Received Kafka records from topic. Number of records)........: " + records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {
                    try {
                        KafkaMessageData message = record.value();
                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        // key with both dataType and eventType
                        String keyWithEventType = dataType + ":" + eventType;
                        String keyWithoutEventType = dataType;


                        // handler with both dataType and eventType
                        Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);

                        if (handler == null) {
                            // If not found, try to find the handler with only dataType
                            handler = messageHandlers.get(keyWithoutEventType);
                        }

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                            // handler.accept(message);
                            Consumer<KafkaMessageData> finalHandler = handler;
                            CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
                        }
//                        else {
//                            log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//                        }

                        log.debug("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
                    } catch (Exception e) {
                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
                    }
                }

                long endTime = System.currentTimeMillis();
                log.debug(":::::::::::::::::: (Total time taken for per poll in consumer (ms)):::::::::::::: " + (endTime - startTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Radius-Micro-Service: " + e.getMessage(), e);
        }
    }

    @Async
    public void setUserProperties(CustomMessage message) {
        if (message.getCurrentUser() != null) MDC.put("userName", message.getCurrentUser());
        if (message.getTraceId() != null) MDC.put("traceId", message.getTraceId());
        if (message.getSpanId() != null) MDC.put("spanId", message.getSpanId());
    }


    @Async
    public void handleSaveBusinessUnit(KafkaMessageData message) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusinessUnitEntity(dataMessage);
            log.info("Handled SaveBusinessUnitSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }


    @Async
    protected void handleSaveStaffUser(KafkaMessageData message) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.saveStaffUserEntity(dataMessage);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Async
    public void handleStaffUser(KafkaMessageData message) {
        try {
            StaffUserMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), StaffUserMessage.class);
            staffUserService.savestaffUser(dataMessage);
            log.info("Handled StaffUserMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling StaffUserMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveMvno(KafkaMessageData message) {
        try {
//            SaveMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveMvnoSharedDataMessage.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Handled SaveMvnoSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBusinessUnit(KafkaMessageData message) {
        try {
            UpdateBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnitEntity(dataMessage);
            log.info("Handled UpdateBusinessUnitSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvno(KafkaMessageData message) {
        try {
            UpdateMvnoSharedDataMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("Handled UpdateMvnoSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvnoData(KafkaMessageData message) {
        try {
            UpdateMvnoData dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoData.class);
            customerServiceImpl.updateMvnoIdIsptoIsp(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Handled UpdateMvnoData successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCommonRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.saveRole(dataMessage);
            log.info("Handled CommonRoleMessage with create successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CommonRoleMessage with create: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteCommonRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.deleteRole(dataMessage);
            log.info("Handled CommonRoleMessage with delete successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CommonRoleMessage with delete: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleServiceArea(KafkaMessageData message) {
        try {
            ServiceAreaMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ServiceAreaMessage.class);
            serviceArea.saveServiceArea(dataMessage);
            log.info("Handled ServiceAreaMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ServiceAreaMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSystemConfiguration(KafkaMessageData message) {
        try {
            SaveClientServMessge dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveClientServMessge.class);
            configurationServices.addUpdateConfiguration(dataMessage);
            log.info("Handled SaveClientServMessge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveClientServMessge: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleStaffUserServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SaveStaffAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()), SaveStaffAssignmentMessage.class);
            serviceArea.assignStaffToServiceArea(dataMessage.getMappingList());
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }

    //cms handle event started
    @Async
    public void handleCustomerCreate(KafkaMessageData message) {
        try {
            RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
            radiusAsyncUtility.saveCustomer(message);
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER CREATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerPasswordUpdate(KafkaMessageData message) {
        try {
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            setUserProperties(dataMessage);
            customerService.updateBSSCustomerPassword(dataMessage);
            log.info("Handled CustomMessage for CUSTOMER PASSWORD UPDATE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER PASSWORD UPDATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleWifiCustomerUpdateQuota(KafkaMessageData message) {
        try {
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            log.info("Handled Update customer Quota successfully: " + message);
            setUserProperties(dataMessage);
            customerService.updateWifiCustomerQuota(dataMessage);
        } catch (Exception e) {
            log.error("Error handling Update Customer Quota: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustPlanMapping(KafkaMessageData message) {
        try {
            RadiusAsyncUtility radiusAsyncUtility = new RadiusAsyncUtility();
            LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) message.getData();
            if (linkedHashMap.containsKey("ignoreOnCreate") && Boolean.FALSE.equals(linkedHashMap.get("ignoreOnCreate")))
                radiusAsyncUtility.saveCustomerPlan(message);
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER PLAN MAPPING SAVE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustQosPolicy(KafkaMessageData message) {
        try {
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            qosPolicyService.save(dataMessage);

            log.info("Handled CustomMessage for CUSTOMER CREATE QOS POLICY successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CUSTOMER CREATE QOS POLICY: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSavePostpaidPlan(KafkaMessageData message) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Handled CustomMessage for CREATE PLAN successfully: " + message);
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            PostpaidPlan plan = updatePlan(dataMessage);
            long endTime = System.currentTimeMillis();
            log.debug("Time Taken to update Plan: " + (endTime - startTime));
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CREATE PLAN: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSavePostpaidPlanInBulk(KafkaMessageData message) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Handled CustomMessage for CREATE PLAN successfully: " + message);
            CustomMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomMessage.class);
            List<CustomMessage> customMessages = dataMessage.getPostpaidPlanMessages();
            List<PostpaidPlan> postpaidPlans = new ArrayList<>();
            for (CustomMessage msg : customMessages) {
                PostpaidPlan plan = updatePlan(msg);
                postpaidPlans.add(plan);
            }
            if (!CollectionUtils.isEmpty(postpaidPlans)) {
                custPlanMappingService.updateCustPlanOnPlanUpdateUsingJPQL(postpaidPlans);
                if (dataMessage.isTriggerCoaDm()) {
                    List<Integer> list = postpaidPlans.stream().map(PostpaidPlan::getId).collect(Collectors.toList());
                    custPlanMappingService.triggerCOADMonPlanQosUpdate(list);
                }
            }
            long endTime = System.currentTimeMillis();
            log.debug("Time Taken to update Plan: " + (endTime - startTime));
        } catch (Exception e) {
            log.error("Error handling CustomMessage for CREATE PLAN: " + e.getMessage(), e);
        }
    }

    public PostpaidPlan updatePlan(CustomMessage dataMessage) {
        PostpaidPlan plan = postpaidPlanService.save(dataMessage);
        if (plan != null && plan.getId() != null && dataMessage.isUpdateAllCustPlan()) {

        } else if (dataMessage.isUpdateAllCustPlan()) {
            log.error("Plan Not Updated");
        }
        return plan;
    }

    @Async
    public void handleDeleteOrUpdateCustMacMapping(KafkaMessageData message) {
        try {
            CustMacMappingMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustMacMappingMessage.class);
            boolean delete = (boolean) dataMessage.getData().get("isDelete");
            if (delete)
                customerService.deleteCustomerMACFromApigateway(dataMessage);
            else
                customerService.updateCustomerMacFromApiGTW(dataMessage);
            log.info("Handled CustMacMappingMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustMacMappingMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerTimeBasePolicyDetails(KafkaMessageData message) {
        try {
            CustomerTimeBasePolicyDetailsMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerTimeBasePolicyDetailsMessage.class);
            customerTimeBasePolicyService.save(dataMessage);
            log.info("Handled CustomerTimeBasePolicyDetailsMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerTimeBasePolicyDetailsMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleTimeBasePolicy(KafkaMessageData message) {
        try {
            TimeBasePolicyMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TimeBasePolicyMessage.class);
            timeBasePolicyService.save(dataMessage);
            log.info("Handled TimeBasePolicyMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling TimeBasePolicyMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleTimeBasePolicyDetails(KafkaMessageData message) {
        try {
            TimeBasePolicyDetailsListMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), TimeBasePolicyDetailsListMessage.class);
            timeBasePolicyDetailServiceImp.saveAll(dataMessage.getTimeBasePolicyDetailsMessageList());
            log.info("Handled TimeBasePolicyDetailsListMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling TimeBasePolicyDetailsListMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustPlanMapping(KafkaMessageData message) {
        try {
            CustomerPackageRelMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerPackageRelMessage.class);
//            log.info("Handled CustomerPackageRelMessage successfully: " + message);
            log.info("updateCustPlanEndDateInRadius consume to kafka with status for CHANGE_PLAN:: {} with time:: {}.", (String) dataMessage.getData().get("custPlanStatus"), dataMessage.getData().get("endDate"));
            custPlanMappingService.Update(dataMessage, dataMessage.getOperation());
        } catch (Exception e) {
            log.error("Error handling CustomerPackageRelMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustServiceChargeIPDtls(KafkaMessageData message) {
        try {
            CustServiceChargeIPDtlsMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustServiceChargeIPDtlsMessage.class);
            custServiceChargeIPDtlsServiceImpl.save(dataMessage);
            log.info("Handled CustServiceChargeIPDtlsMessage WITH SAVE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustServiceChargeIPDtlsMessage WITH SAVE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustServiceChargeIPDtls(KafkaMessageData message) {
        try {
            CustServiceChargeIPDtlsMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustServiceChargeIPDtlsMessage.class);
            custServiceChargeIPDtlsServiceImpl.update(dataMessage);
            log.info("Handled CustServiceChargeIPDtlsMessage WITH UPDATE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustServiceChargeIPDtlsMessage WITH UPDATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustomerShareData(KafkaMessageData message) {
        try {
            log.info("Handled UpdateCustomerShareDataMessage successfully: " + message);
            UpdateCustomerShareDataMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateCustomerShareDataMessage.class);
            customerService.updateCustomerdata(dataMessage);
        } catch (Exception e) {
            log.error("Error handling UpdateCustomerShareDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustomerIP(KafkaMessageData message) {
        try {
            CustIPMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustIPMessage.class);
            customerService.customerIpMappingUpdate(dataMessage);
            log.info("Handled CustIPMessage WITH UPDATE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustIPMessage WITH UPDATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustomerIP(KafkaMessageData message) {
        try {
            CustIPMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustIPMessage.class);
            customerService.customerIpMappingSave(dataMessage);
            log.info("Handled CustIPMessage WITH SAVE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustIPMessage WITH SAVE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteCustomerIP(KafkaMessageData message) {
        try {
            log.info("Handled CustIPMessage For DELETE : " + message);
            CustIPMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustIPMessage.class);
            customerService.customerIpMappingDelete(dataMessage);
        } catch (Exception e) {
            log.error("Error handling CustIPMessage WITH DELETE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerDeactivationWhenMvnoIsInActive(KafkaMessageData message) {
        try {
            MvnoStatusMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), MvnoStatusMessage.class);
            customerService.customerDeactivationWhenMvnoIsInActive(dataMessage);
            log.info("Handled MvnoStatusMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MvnoStatusMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerUpdateStatus(KafkaMessageData message) {
        try {
            log.info("Handled CustomerUpdateMessage successfully: " + message);
            CustomerUpdateMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerUpdateMessage.class);
            Map<String, Object> customerData = dataMessage.getCustomerData();
            String password = (String) customerData.get("password") != null ? (String) customerData.get("password") : null;
            String nasPortId = (String) customerData.get("nasPortId") != null ? (String) customerData.get("nasPortId") : null;
            String status = (String) customerData.get("status") != null ? (String) customerData.get("status") : null;
            String username = (String) customerData.get("username") != null ? (String) customerData.get("username") : null;
            if (nasPortId != null || password != null) {
                customerService.updateCustomerStatusSoap(Integer.valueOf(customerData.get("id").toString()), status, nasPortId, password, "Customer status update from CMS", true, username);
            } else {
                customerService.updateCustomerStatus(Integer.valueOf(customerData.get("id").toString()), customerData.get("status").toString(), "Customer status update from CMS", true, username);
            }
        } catch (Exception e) {
            log.error("Error handling CustomerUpdateMessage: " + e.getMessage(), e);
        }
    }

    public void handleMessageCafToCustomere(KafkaMessageData message) {
        try {
            CaftoCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CaftoCustomerMessage.class);
            prepaidInvoiceService.cafToCustomer(dataMessage);
            log.info("Handled receiveMessageCafToCustomere successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageCafToCustomere: " + e.getMessage(), e);
        }
    }

    public void handleCafToCustServiceActivation(KafkaMessageData message) {
        try {
            CaftoCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CaftoCustomerMessage.class);
            prepaidInvoiceService.cafToCustomerOnServiceActivation(dataMessage);
            log.info("Handled receiveMessageCafToCustomere successfully(Service Activation): " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageCafToCustomere for Service Activation: " + e.getMessage(), e);
        }
    }

    //Netconf handle method started
    @Async
    public void handleNetConfCustomerCreate(KafkaMessageData message) {
        try {
            CustomerCreateData dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerCreateData.class);
            customerService.addNewCustomers(dataMessage, dataMessage.getMvnoId(), true);
            log.info("Handled CustomerCreateData with CREATE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerCreateData with CREATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerUpdate(KafkaMessageData message) {
        try {
            CustomerCreateData dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerCreateData.class);
            customerService.updateCustomers(dataMessage, dataMessage.getMvnoId(), true);
            log.info("Handled CustomerCreateData with UPDATE successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerCreateData with UPDATE: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerUpdateWithCustomer(KafkaMessageData message) {
        try {
            CustomerCreateData dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerCreateData.class);
            customerService.updateCustomers(dataMessage, dataMessage.getMvnoId(), true);
            log.info("Handled CustomerCreateData with UPDATE different queue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerCreateData with UPDATE different queue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerIPV4provision(KafkaMessageData message) {
        try {
            CustomerCreateData dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerCreateData.class);
            customerService.defaultLeaseIPv4provision(dataMessage, true);
            log.info("Handled CustomerCreateData with defaultLeaseIPv4provision different queue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerCreateData with defaultLeaseIPv4provision different queue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerIPV4provisionUpdate(KafkaMessageData message) {
        try {
            DefaultUpdate dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), DefaultUpdate.class);
            CustomerCreateData customer = dataMessage.getCustomerCreateData();
            customerService.defaultLeaseIPv4Update(customer, dataMessage.getOldUsername(), true);
            log.info("Handled CustomerCreateData with defaultLeaseIPv4provision with update different queue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerCreateData with defaultLeaseIPv4provision  with update different queue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerUpdateStatus(KafkaMessageData message) {
        try {
            CustomerStatusUpdateMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerStatusUpdateMessage.class);
            customerService.updateCustomerStatus(dataMessage.getCustId(), dataMessage.getStatus(), dataMessage.getRemark(), true, null);
            log.info("Handled CustomerStatusUpdateMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerStatusUpdateMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerDelete(KafkaMessageData message) {
        try {
            DeleteCustomerMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), DeleteCustomerMessage.class);
            customerService.deleteCustomers(dataMessage.getCustid(), dataMessage.getMvnoId(), true);
            log.info("Handled DeleteCustomerMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling DeleteCustomerMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleNetConfCustomerDeleteWithDefaultProvision(KafkaMessageData message) {
        try {
            DeleteCustomerMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), DeleteCustomerMessage.class);
            customerService.defoultDeprovision(dataMessage.getUsername(), dataMessage.getGatewayIpBind(), true);
            log.info("Handled DeleteCustomerMessage with default provision successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling DeleteCustomerMessage: " + e.getMessage(), e);
        }
    }


    @Async
    public void handleRevenueCPRUpdate(KafkaMessageData message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            UpdateCprMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateCprMessage.class);
            if (dataMessage.getCustPackAndEndDatePair().size() > 0) {
                for (Map.Entry<Integer, String> custPackAndEndDate : dataMessage.getCustPackAndEndDatePair()) {
                    Optional<CustPlanMappping> mapping = custPlanMappingRepository.findById(custPackAndEndDate.getKey().longValue());
                    if (mapping.isPresent()) {
                        LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
                        mapping.get().setEndDate(endDate);
                        mapping.get().setExpiryDate(endDate);
                        custPlanMappingRepository.save(mapping.get());
                    }
                }
            }
        } catch (Exception e) {
            log.error("receiveMessage CPR update Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Async
    public void handleCustomerNextQuotaUpdate(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            CustomerNextQuotaUpdateMessage dataMessage = mapper.convertValue(message.getData(),CustomerNextQuotaUpdateMessage.class);
            customerService.updateCustomerNextQuota(dataMessage);
            log.info("Handled CustomerNextQuotaUpdateMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustomerNextQuotaUpdateMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePlanUpdateMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            PlanUpdateMessage planUpdateMessage = mapper.convertValue(message.getData(), PlanUpdateMessage.class);
            customerService.updatePlanWhileCafApproval(planUpdateMessage.getPlanUpdateCafApprovalMessages());
            log.info("Successfully handlePlanUpdateMessage: {}");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error handling handlePlanUpdateMessage: {}", message, e);
        }
    }

    @Async
    public void handleUpdateCustPlanMappinForVoidPlan(KafkaMessageData message) {
        try {
            UpdateCustplanMappingMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustplanMappingMessage.class);
            custPlanMappingService.updateCustPlanMapping(dataMessage);
            log.info("Handled UpdateCustPlanMappingForP2Pmessage: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCustPlanMappingForP2Pmessage: " + e.getMessage(), e);
        }
    }
}
