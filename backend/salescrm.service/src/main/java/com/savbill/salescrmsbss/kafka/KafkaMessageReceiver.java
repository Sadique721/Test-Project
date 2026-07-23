package com.savbill.salescrmsbss.kafka;

import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.entity.*;
import com.savbill.salescrmsbss.rabbitMq.ClientServiceMessage;
import com.savbill.salescrmsbss.rabbitMq.message.*;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.service.*;
import com.savbill.salescrmsbss.rabbitMq.message.*;
import com.savbill.salescrmsbss.repository.*;
import com.savbill.salescrmsbss.service.*;
import com.savbill.salescrmsbss.service.Impl.LeadMasterServiceImpl;
import com.savbill.salescrmsbss.service.Impl.PlanGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

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
    private MvnoRepository mvnoRepository;
    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private RolesService roleService;

    private static Logger log = LoggerFactory.getLogger(KafkaMessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomACLEntryRepository customACLEntryRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private TeamUserMappingRepository teamUserMappingRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private LeadMasterServiceImpl leadMasterService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private LeadQuotationService leadQuotationService;

    @Autowired
    private PlanGroupService planGroupService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    private PincodeService pincodeService;

    @Autowired
    private AreaService areaService;

    @Autowired
    KafkaConsumerConfig consumerConfig;
    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public KafkaMessageReceiver() {

        //Common
        messageHandlers.put("SaveCountrySharedDataMessage", this::handleCountry);
        messageHandlers.put("UpdateCountrySharedDataMessage", this::handleCountryUpdates);
        messageHandlers.put("SaveStateSharedDataMessage", this::handleState);
        messageHandlers.put("UpdateStateSharedDataMessage", this::handleStateUpdates);
        messageHandlers.put("SaveCitySharedDataMessage", this::handleCity);
        messageHandlers.put("UpdateCitySharedDataMessage", this::handleCityUpdates);
        messageHandlers.put("SavePincodeSharedDataMessage", this::handlePinCode);
        messageHandlers.put("UpdatePincodeSharedDataMessage", this::handlePinCodeUpdates);
        messageHandlers.put("SaveAreaSharedDataMessage", this::handleArea);
        messageHandlers.put("UpdateAreaSharedDataMessage", this::handleAreaUpdates);
        messageHandlers.put("ServiceareaMessage", this::handleServiceArea);
        //messageHandlers.put("roleMessage", this::handleRole);  not in use
        //messageHandlers.put("UserMessage",this::handleUser);  not in use
        messageHandlers.put("BusinessUnitMessage", this::handleBusinessUnit);

        // CMS
        messageHandlers.put("SendApproverForLeadMsg", this::handleLeadApproval);
        messageHandlers.put("SendUpdatedLeadInfo", this::handleUpdateLeadInfo);
        messageHandlers.put("BranchMessage", this::handleBranch);
        messageHandlers.put("CustomerMessage", this::handleCustomers);
        messageHandlers.put("LeadStatusMessage", this::handleCustomerStatus);
        messageHandlers.put("ClientServiceMessage", this::handleClientService);
        messageHandlers.put("MvnoMessage", this::handleMvno);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleSaveMvno);
        messageHandlers.put("PopManagementMessage", this::handlePopManagement);
        messageHandlers.put("TeamsMessage", this::handleTeamMessage);
        messageHandlers.put("SendLeadAssignMessage", this::handleLeadAssign);
        messageHandlers.put("UpdatePlanPricesMessage", this::handlePlanPrice);
        messageHandlers.put("LeadMasterPojoMessage", this::handleCafConversion);
        messageHandlers.put("SendLeadQuotationMessage", this::handleLeadQuotation);
        messageHandlers.put("PlanGroupMsg:" + KafkaConstant.SAVE_PLAN_GROUP, this::handleSavePlanGroup);      //done
        messageHandlers.put("PlanGroupMsg:" + KafkaConstant.UPDATE_PLAN_GROUP, this::handleUpdatePlanGroup);  //done
        messageHandlers.put("SaveClientServMessge", this::handleClientServiceCreate);
        messageHandlers.put("UpdateClientServMessage", this::handleClientServiceUpdate);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.CREATE_ROLE, this::handleRoleCreate);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.DELETE_ROLE, this::handleRoleUpdate);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleCustomerUpdate);
        messageHandlers.put("UpdateMvnoData", this::handleMvnoUpdate);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleUpdateMvno);

        //Partner
        messageHandlers.put("SavePartnerSharedDataMessage:" + KafkaConstant.CREATE_PARTNER, this::handlePartnerCreate);  //done
        messageHandlers.put("UpdatePartnerSharedDataMessage:" + KafkaConstant.UPDATE_PARTNER, this::handlePartnerUpdate); //done

        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleSaveStaffUserSharedDataMessage);
        messageHandlers.put("UpdateStaffUserSharedDataMessage", this::handleUpdateStaffUser);
        messageHandlers.put("UpdateBranchSharedData", this::handleUpdateBranch);

    }


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
                    KafkaConstant.KAFKA_COMMON_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC,
                    KafkaConstant.KAFKA_NETCONFIG_TOPIC,
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_INTEGRATION_TOPIC)));

            long startTime = System.currentTimeMillis();

            while (true) {
                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
                log.info(".......(Received Kafka records from topic. Number of records)........: " + records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {
                    try {
                        KafkaMessageData message = record.value();
                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        // key with both dataType and eventType
                        String keyWithEventType = dataType + ":" + eventType;


                        // handler with both dataType and eventType
                        Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);

                        if (handler == null) {
                            handler = messageHandlers.get(dataType);
                        }

                        if (handler != null) {
                            log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
                            // accept message in future task
                            Consumer<KafkaMessageData> finalHandler = handler;
                            CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
                        }
                        log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
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


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
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
//
//
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
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//
//
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
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//
//
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC}, groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
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
//
//
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC}, groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
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
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//
//
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
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//
//
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
//            log.error("Kafka Error Message Receive From Common-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//
//
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NETCONFIG_TOPIC}, groupId = KafkaConstant.KAFKA_NETCONFIG_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNETConfigMicroService(KafkaMessageData message) {
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
//
//
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INTEGRATION_TOPIC}, groupId = KafkaConstant.KAFKA_INTEGRATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormIntegrationMicroService(KafkaMessageData message) {
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
//
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCMSMicroService(KafkaMessageData message) {
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


    public void handleCountry(KafkaMessageData message) {
        try {
            SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


    public void handleCountryUpdates(KafkaMessageData message) {
        try {
            UpdateCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCountrySharedDataMessage.class);
            countryService.updateCountry(dataMessage);
            log.info("Handled UpdateCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleState(KafkaMessageData message) {
        try {
            SaveStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStateSharedDataMessage.class);
            stateService.saveStateEntity(dataMessage);
            log.info("Handled SaveStateSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleStateUpdates(KafkaMessageData message) {
        try {
            UpdateStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStateSharedDataMessage.class);
            stateService.updateStateEntity(dataMessage);
            log.info("Handled UpdateStateSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleCity(KafkaMessageData message) {
        try {
            SaveCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCitySharedDataMessage.class);
            cityService.saveCityEntity(dataMessage);
            log.info("Handled SaveCitySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleCityUpdates(KafkaMessageData message) {
        try {
            UpdateCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCitySharedDataMessage.class);
            cityService.updateCityEntity(dataMessage);
            log.info("Handled UpdateCitySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handlePinCode(KafkaMessageData message) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.savePincode(dataMessage);
            log.info("Handled CityMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CityMessage: " + e.getMessage(), e);
        }
    }

    public void handlePinCodeUpdates(KafkaMessageData message) {
        try {
            UpdatePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePincodeSharedDataMessage.class);
            pincodeService.updatePincode(dataMessage);
            log.info("Handled CityMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CityMessage: " + e.getMessage(), e);
        }
    }

    public void handleArea(KafkaMessageData message) {
        try {
            Pincode pincode = null;
            log.info("Received Message From kafka : <" + message + ">");
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveAreaSharedDataMessage.class);
            Optional<Pincode> findById = this.pincodeRepository.findById(dataMessage.getPincode().getId().longValue());
            if (findById.isPresent()) pincode = findById.get();
            areaService.saveAreaEntity(dataMessage);
            log.info("Handled AreaMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling AreaMessage: " + e.getMessage(), e);
        }
    }


    public void handleAreaUpdates(KafkaMessageData message) {
        try {
            Pincode pincode = null;
            log.info("Received Message From kafka : <" + message + ">");
            UpdateAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateAreaSharedDataMessage.class);
            Optional<Pincode> findById = this.pincodeRepository.findById(dataMessage.getPincode().getId().longValue());
            if (findById.isPresent()) pincode = findById.get();
            areaService.updateAreaEntity(dataMessage);
            log.info("Handled AreaMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling AreaMessage: " + e.getMessage(), e);
        }
    }

    public void handleServiceArea(KafkaMessageData message) {
        try {
            Area area = null;
            log.info("Received Message From kafka : <" + message + ">");
            ServiceAreaMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ServiceAreaMessage.class);
//            Optional<Area> findById = this.areaRepository.findById(dataMessage.getAreaId());
//            if(findById.isPresent()) area = findById.get();
            this.serviceAreaRepository.save(new ServiceArea(dataMessage, area));
            log.info("Handled ServiceAreaMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ServiceAreaMessage: " + e.getMessage(), e);
        }
    }

    public void handleRole(KafkaMessageData message) {
        try {
            List<CustomACLEntry> customACLEntryList = new ArrayList<CustomACLEntry>();
            log.info("Received Message From Kafka : <" + message + ">");
            RoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), RoleMessage.class);
            Role savedRole = this.roleRepository.save(new Role(dataMessage));
            for (CustomACLEntry customACLEntry : dataMessage.getAclEntryList()) {
                customACLEntry.setRole(savedRole);
                customACLEntryList.add(customACLEntry);
            }
            this.customACLEntryRepository.saveAll(customACLEntryList);
            log.info("Handled RoleMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling RoleMessage: " + e.getMessage(), e);
        }
    }

    public void handleUser(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            UserMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UserMessage.class);
            StaffUser staffUser = this.staffUserRepository.save(new StaffUser(dataMessage));
            if (dataMessage.getTeamMessageList() != null &&
                    dataMessage.getTeamMessageList().size() > 0) {
                List<TeamUserMapping> teamUserMappingList = new ArrayList<TeamUserMapping>();
                for (TeamsMessage teamsMessage : dataMessage.getTeamMessageList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setStaffId(staffUser.getId());
                    teamUserMapping.setTeamId(teamsMessage.getId());
                    teamUserMappingList.add(teamUserMapping);
                }
                this.teamUserMappingRepository.saveAll(teamUserMappingList);
                log.info("Handled UserMessage successfully: " + message);
            }

        } catch (Exception e) {
            log.info("Handled UserMessage successfully: " + message);
        }
    }

    public void handleSaveStaffUserSharedDataMessage(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            System.out.println("Message : " + message);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveStaffUserSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveStaffUserSharedDataMessage.class);
            StaffUser save = staffUserRepository.save(new StaffUser(dataMessage));
            if(dataMessage.getTeamsList().size()>0){
                for (Teams item : dataMessage.getTeamsList()) {
                    TeamUserMapping teamUserMapping = new TeamUserMapping();
                    teamUserMapping.setTeamId(item.getId());
                    teamUserMapping.setStaffId((int) dataMessage.getId().longValue());
                    teamUserMappingRepository.save(teamUserMapping);
                }
            }
        } catch (Exception e) {
            log.error("Error handling SaveStaffUserSharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleBusinessUnit(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            BusinessUnitMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BusinessUnitMessage.class);
            this.businessUnitRepository.save(new BusinessUnit(dataMessage));
            log.info("Handled ServiceAreaMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ServiceAreaMessage: " + e.getMessage(), e);
        }
    }

    public void handleLeadApproval(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendApproverForLeadMsg dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendApproverForLeadMsg.class);
            this.leadMasterService.updateLeadApprover(dataMessage.getLeadFlowApproverData());
            log.info("Handled SendApproverForLeadMsg successfully: " + message);

        } catch (Exception e) {
            log.error("Error handling SendApproverForLeadMsg: " + message);

        }
    }

    public void handleUpdateStaffUser(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            System.out.println("Message : " + message);

            // JSON → UpdateStaffUserSharedDataMessage
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateStaffUserSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateStaffUserSharedDataMessage.class);

            //Fetch existing user
            StaffUser staffUser = staffUserRepository.findById(dataMessage.getId()).orElse(null);

            if (staffUser != null) {

                //Update entity fields
                staffUser.setUsername(dataMessage.getUsername());
                staffUser.setPassword(dataMessage.getPassword());
                staffUser.setFirstname(dataMessage.getFirstname());
                staffUser.setLastname(dataMessage.getLastname());
                staffUser.setStatus(dataMessage.getStatus());
                staffUser.setEmail(dataMessage.getEmail());
                staffUser.setPhone(dataMessage.getPhone());
                staffUser.setRoles(dataMessage.getRoles());
                staffUser.setIsDelete(dataMessage.getIsDelete());
                staffUser.setMvnoId(dataMessage.getMvnoId());
                staffUser.setBranchId(dataMessage.getBranchId());
                if (!"null".equalsIgnoreCase(dataMessage.getLast_login_time()) &&
                        dataMessage.getLast_login_time() != null) {
                    staffUser.setLast_login_time(LocalDateTime.parse(dataMessage.getLast_login_time()));
                } else {
                    staffUser.setLast_login_time(null);
                }

                //Save updated staff user
                staffUserRepository.save(staffUser);
                log.info("Staff User updated successfully : " + dataMessage.getUsername());
            }
            else {
                StaffUser newUser = new StaffUser(dataMessage);
                staffUserRepository.save(newUser);
                log.info("Staff User not found → Created new staff with ID : " + dataMessage.getId());
            }
        } catch (Exception e) {
            log.error("Error handling UpdateStaffUserSharedDataMessage: " + e.getMessage(), e);
        }
    }

    public void handleUpdateLeadInfo(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            SendUpdatedLeadInfo dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendUpdatedLeadInfo.class);
            this.leadMasterService.updateLeadApproverInfo(dataMessage.getLeadFlowApproverUpdatedData());
            log.info("Handled SendApproverForLeadMsg successfully: " + message);

        } catch (Exception e) {
            log.error("Error handling SendApproverForLeadMsg: " + message);

        }
    }

    public void handleBranch(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            BranchMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BranchMessage.class);
            this.branchRepository.save(new Branch(dataMessage));
            log.info("Handled BranchMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling BranchMessage: " + message);

        }
    }


    public void handleCustomers(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerMessage.class);
            this.customersRepository.save(new Customers(dataMessage));
            log.info("Handled BranchMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling BranchMessage: " + message);

        }
    }


    public void handleCustomerStatus(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            LeadStatusMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), LeadStatusMessage.class);
            this.leadMasterService.updateLeadCustStatus(new LeadStatusMessage(dataMessage));
            log.info("Handled LeadStatusMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling LeadStatusMessage: " + message);

        }
    }


    public void handleClientService(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            ClientServiceMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ClientServiceMessage.class);
            this.clientServiceSrv.update(new ClientService(dataMessage));
            log.info("Handled ClientServiceMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ClientServiceMessage: " + message);

        }
    }

    public void handleMvno(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            MvnoMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoMessage.class);
            this.mvnoRepository.save(new Mvno(dataMessage));
            log.info("Handled MvnoMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MvnoMessage: " + message);

        }
    }


    public void handlePopManagement(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            PopManagementMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PopManagementMessage.class);
            this.popManagementRepository.save(new PopManagement(dataMessage));
            log.info("Handled PopManagementMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error PopManagementMessage MvnoMessage: " + message);

        }
    }

    public void handleTeamMessage(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            TeamsMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), TeamsMessage.class);
            this.teamsRepository.save(new Teams(dataMessage));
            log.info("Handled TeamsMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling TeamsMessage: " + message);

        }
    }

    public void handleLeadAssign(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendLeadAssignMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendLeadAssignMessage.class);
            //new Gson().fromJson(new Gson().toJson(message.getData()),SendLeadAssignMessage.class);
            this.leadMasterService.updateLeadAssignApproverInfo(dataMessage.getLeadMgmtWfDTO());
            log.info("Handled SendLeadAssignMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SendLeadAssignMessage:" + message);

        }
    }


    public void handlePlanPrice(KafkaMessageData message) {
        try {
            log.info("Received Message From  RabbitMq : <" + message + ">");
            UpdatePlanPricesMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePlanPricesMessage.class);
            List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findByPlanId(dataMessage.getPlanId().intValue());
            if (custPlanMapppingList != null && !custPlanMapppingList.isEmpty()) {
                custPlanMapppingList = custPlanMapppingList.stream().map(custPlanMappping -> {
                            custPlanMappping.setOfferPrice(dataMessage.getOfferPriceUpdated());
                            custPlanMappping.setTaxAmount(dataMessage.getTaxAmountUpdated());
                            return custPlanMappping;
                        }
                ).collect(Collectors.toList());
                custPlanMapppingRepository.saveAll(custPlanMapppingList);
            }
            log.info("Handled UpdatePlanPricesMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdatePlanPricesMessage: " + message);

        }
    }

    public void handleCafConversion(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            LeadMasterPojoMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), LeadMasterPojoMessage.class);
            this.leadMasterService.updateLeadStatus(dataMessage);
            log.info("Handled LeadMasterPojoMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling LeadMasterPojoMessage : " + message);

        }
    }


    public void handleLeadQuotation(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            SendLeadQuotationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendLeadQuotationMessage.class);
            this.leadQuotationService.updateLeadQuotationApprover(dataMessage.getLeadQuotationWfDTO());
            log.info("Handled LeadMasterPojoMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling LeadMasterPojoMessage : " + message);

        }
    }

    public void handleSavePlanGroup(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            PlanGroupMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PlanGroupMsg.class);
            planGroupService.save(dataMessage);
            log.info("Handled PlanGroupMsg successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling PlanGroupMsg : " + message);

        }
    }

    public void handleUpdatePlanGroup(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            PlanGroupMsg dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PlanGroupMsg.class);
            planGroupService.update(dataMessage);
            log.info("Handled PlanGroupMsg successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling PlanGroupMsg : " + message);
        }
    }

    public void handlePartnerCreate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            SavePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePartnerSharedDataMessage.class);
            this.partnerRepository.save(new Partner(dataMessage));
            log.info("Handled SavePartnerSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SavePartnerSharedDataMessage : " + message);
        }
    }


    public void handlePartnerUpdate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            UpdatePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePartnerSharedDataMessage.class);
            this.partnerRepository.save(new Partner(dataMessage));
            log.info("Handled UpdatePartnerSharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdatePartnerSharedDataMessage : " + message);
        }
    }

    public void handleClientServiceCreate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("Handled SaveClientServMessge successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveClientServMessge : " + message);
        }
    }

    public void handleClientServiceUpdate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateClientServMessage.class);
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("Handled UpdateClientServMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateClientServMessage : " + message);
        }
    }

    public void handleRoleCreate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.saveRole(dataMessage);
            log.info("Handled CommonRoleMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CommonRoleMessage : " + message);
        }
    }

    public void handleRoleUpdate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.deleteRole(dataMessage);
            log.info("Handled CommonRoleMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CommonRoleMessage : " + message);
        }
    }

    public void handleCustomerUpdate(KafkaMessageData message) {
        try {
            log.info("Received Message From  Kafka : <" + message + ">");
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            customersService.updateCustomers(dataMessage);
            log.info("Handled UpdateCustomerShareDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCustomerShareDataMessage : " + message);
        }
    }


    public void handleMvnoUpdate(KafkaMessageData message) {
        log.info("Received Message From  Kafka : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoData.class);
            mvnoService.UpdateMvnoidISP(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Handled UpdateMvnoData successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData : " + message);
        }
    }

    private void handleSaveMvno(KafkaMessageData message) {

        log.info("Received Message From  Kafka : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            SaveMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMvno(dataMessage);
            log.info("Handled UpdateMvnoData successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData : " + message);
        }
    }

    public void handleUpdateMvno(KafkaMessageData message) {
        log.info("Received Message From  Kafka : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            UpdateMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMvno(dataMessage);
            log.info("Handled UpdateMvnoData successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData : " + message);
        }
    }

    private void handleSaveStaffUser(KafkaMessageData message) {
        log.info("Received Message From  Kafka : <" + message + ">");
        System.out.println("Message : " + message);
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveStaffUserSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveStaffUserSharedDataMessage.class);
//            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()),SaveStaffUserSharedDataMessage.class);
            staffUserRepository.save(new StaffUser(dataMessage));
            log.info("Handled Save Staff successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData : " + message);
        }
    }

    public void handleUpdateBranch(KafkaMessageData message) {
        try {
            log.info("Received Message From kafka : <" + message + ">");
            UpdateBranchSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBranchSharedData.class);
            this.branchRepository.save(new Branch(dataMessage));
            log.info("Handled Branch update Message successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling BranchMessage: " + message);

        }
    }
}
