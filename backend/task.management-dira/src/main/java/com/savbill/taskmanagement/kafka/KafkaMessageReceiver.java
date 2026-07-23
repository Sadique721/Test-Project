package com.savbill.taskmanagement.kafka;

import com.savbill.taskmanagement.core.modules.Area.service.AreaService;
import com.savbill.taskmanagement.core.modules.Branch.service.BranchService;
import com.savbill.taskmanagement.core.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.taskmanagement.core.modules.BusinessUnit.service.BusinessUnitService;
import com.savbill.taskmanagement.core.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.savbill.taskmanagement.core.modules.City.service.CityService;
import com.savbill.taskmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.taskmanagement.core.modules.Country.service.CountryService;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.EmailConfig.service.EmailConfigService;
import com.savbill.taskmanagement.core.modules.Mvno.domain.UpdateMvnoData;
import com.savbill.taskmanagement.core.modules.Mvno.service.MvnoService;
import com.savbill.taskmanagement.core.modules.Partner.service.PartnerService;
import com.savbill.taskmanagement.core.modules.Pincode.service.PincodeService;
import com.savbill.taskmanagement.core.modules.Plan.service.CustPlanMappingService;
import com.savbill.taskmanagement.core.modules.Plan.service.PostPaidPlanService;
import com.savbill.taskmanagement.core.modules.PlanService.service.PlanServicesService;
import com.savbill.taskmanagement.core.modules.Region.service.RegionService;
import com.savbill.taskmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.taskmanagement.core.modules.State.service.StateService;
import com.savbill.taskmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.taskmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.taskmanagement.core.modules.role.service.RoleService;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseCustometDetailsService;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.savbill.taskmanagement.rabbitmq.messages.*;
import com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;


@Component
public class KafkaMessageReceiver implements Runnable {
    @Autowired
    private KafkaProducerConfig consumer;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    CityService cityService;

    @Autowired
    PincodeService pincodeService;

    @Autowired
    AreaService areaService;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    BusinessUnitService businessUnitService;

    @Autowired
    BranchService branchService;

    @Autowired
    TeamsService teamsService;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    CaseService caseService;

    @Autowired
    RegionService regionService;

    @Autowired
    BusinessVerticalsService businessVerticalsService;

    @Autowired
    CustomersService customersService;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    RoleService roleService;

    @Autowired
    MvnoService mvnoService;

    @Autowired
    PlanServicesService planService;

    @Autowired
    PostPaidPlanService postPaidPlanService;

    @Autowired
    PartnerService partnerService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private EmailConfigService emailConfigService;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    KafkaConsumerConfig consumerConfig;
    @Autowired
    private CaseCustometDetailsService caseCustometDetailsService;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);

    Map<String, Consumer<KafkaMessageData>> messageHandler = new HashMap<>();

    public KafkaMessageReceiver() {
        messageHandler.put("SaveCountrySharedDataMessage", this::saveCountryHandler);
        messageHandler.put("SaveStateSharedDataMessage", this::saveStateHandler);
        messageHandler.put("SaveCitySharedDataMessage", this::saveCityHandler);
        messageHandler.put("UpdateCountrySharedDataMessage", this::updateCountryHandler);
        messageHandler.put("UpdateStateSharedDataMessage", this::updateStateHandler);
        messageHandler.put("UpdateCitySharedDataMessage", this::updateCityHandler);
        messageHandler.put("SavePincodeSharedDataMessage", this::savePincodeHandler);
        messageHandler.put("UpdatePincodeSharedDataMessage", this::updatePincodeHandler);
        messageHandler.put("SaveAreaSharedDataMessage", this::saveAreaHandler);
        messageHandler.put("UpdateAreaSharedDataMessage", this::updateAreaHandler);
        messageHandler.put("SaveServiceAreaSharedDataMessge", this::saveServiceAreaHandler);
        messageHandler.put("UpdateServiceAreaSharedDataMessage", this::updateServiceAreaHandler);
        messageHandler.put("SaveBusinessUnitSharedDataMessage", this::saveBusinessUnitHandler);
        messageHandler.put("UpdateBusinessUnitSharedDataMessage", this::updateBusinessUnitHandler);
        messageHandler.put("SaveBranchSharedDataMessage", this::saveBranchHandler);
        messageHandler.put("UpdateBranchSharedData", this::updateBranchHandler);
        messageHandler.put("SaveTeamsSharedSharedData", this::saveTeamHandler);
        messageHandler.put("UpdateTeamsSharedData", this::updateTeamHandler);
        messageHandler.put("SaveHierarchyShareDataMessage", this::saveHeirarchyHandler);
        messageHandler.put("UpdateHierarchyShareDataMessage", this::updateHeirarchyHandler);
        messageHandler.put("TicketETRAuditMessage", this::saveETRTicketHandler);
        messageHandler.put("TicketETRAuditMessage:" + KafkaConstant.TASK_ETR_AUDIT_SUCCESS, this::saveETRTicketHandler);
        messageHandler.put("TicketAuditMessage:" + KafkaConstant.TASK_TAT_AUDIT_SUCCESS, this::saveTATTicketHandler);
        messageHandler.put("SaveRegionSharedDataMessage", this::saveRegionHandler);
        messageHandler.put("UpdateRegionSharedDataMessage", this::updateRegionHandler);
        messageHandler.put("SaveBusinessVerticalSharedDataMessage", this::saveBusinessVerticalHandler);
        messageHandler.put("UpdateBusinessVerticalSharedDataMessage", this::updateBusinessVerticalHandler);
        messageHandler.put("SaveCustomerDataShareMessage", this::saveCustomerDataHandler);
        messageHandler.put("UpdateCustomerShareDataMessage", this::updateCustomerDataHandler);
        messageHandler.put("SaveStaffUserSharedDataMessage", this::saveStaffUserHandler);
        messageHandler.put("UpdateStaffUserSharedDataMessage", this::updateStaffUserHandler);
        messageHandler.put("SaveRoleSharedDataMessage", this::saveRoleDataHandler);
        messageHandler.put("UpdateRoleSharedDataMessage", this::updateRoleHandler);
        messageHandler.put("SaveMvnoSharedDataMessage", this::saveMvnoHandler);
        messageHandler.put("UpdateMvnoSharedDataMessage", this::updatMvnoHandler);
        messageHandler.put("SaveServicesSharedDataMessage", this::saveServicesHandler);
        messageHandler.put("UpdateServicesSharedDataMessage", this::updateServicesHandler);
        messageHandler.put("SavePlanSharedDataMessage", this::savePostpaidPlanHandler);
        messageHandler.put("UpdatePlanSharedDataMessage", this::updatePostpaidPlanHandler);
        messageHandler.put("SavePartnerSharedDataMessage:" + KafkaConstant.CREATE_PARTNER, this::savePartnerHandler);
        messageHandler.put("UpdatePartnerSharedDataMessage" + KafkaConstant.UPDATE_PARTNER, this::updatePartnerHandler);
        messageHandler.put("EmailConfigSendToAPIGWMsg", this::getEmailConfigrationHandler);
        messageHandler.put("CAFCustomerStatusMessage", this::saveCafToCustomerHandler);
        messageHandler.put("SaveClientServMessge:" + KafkaConstant.CREATE_SERVICE_CONFIG, this::saveClientServiceHandler);
        messageHandler.put("UpdateClientServMessage:" + KafkaConstant.UPDATE_SERVICE_CONFIG, this::updateClientServiceHandler);
        messageHandler.put("CommonRoleMessage:CREATE_DATA_ROLE", this::saveRoleHandler);
        messageHandler.put("CommonRoleMessage:DELETE_DATA_ROLE", this::deleteRoleHandler);
        messageHandler.put("ChangePlanMessage", this::saveChangePlanHandler);
        messageHandler.put("UpdateMvnoData : IPS_TO_ISP", this::updatemvnoIspToIspHandler);
        messageHandler.put("CloseTicketCheckMessage:SAVE_TICKET_DATA", this::handlesaveTicketDataFromMicroService);
        messageHandler.put("CloseTicketCheckMessage:UPDATE_TICKET_DATA", this::handleupdateTicketDataFromMicroService);
        messageHandler.put("SaveStaffAssignmentMessage", this::handleStaffUserServiceAreaMapping);
        messageHandler.put("UpdateCustplanMappingMessage", this::handleUpdateCustPlanMappinForVoidPlan);
    }


    /**
     * Consumes messages from multiple Kafka topics and processes them based on their data type and event type.
     * The method subscribes to several topics and continuously polls for new messages.
     * Each message is processed asynchronously based on its type, and the corresponding handler is invoked.
     * <p>
     * If no handler is found for a specific combination of data type and event type, it will try to find a handler based only on the data type.
     * <p>
     * This method is executed within a transactional context to ensure consistency of operations.
     *
     * <p>Each message is processed asynchronously using an {@link CompletableFuture} to ensure that handlers can operate concurrently
     * without blocking the main consumer thread.</p>
     *
     * <p>Exceptions during message processing are logged with details about the failed record.</p>
     *
     * @throws Exceptions If there is an error during Kafka consumption or processing.
     */
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
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA)));

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


                        // handler with both dataType and eventType
                        Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);

                        if (handler == null) {
                            handler = messageHandler.get(dataType);
                        }

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : dataType));
                            // accept message in future task
                            Consumer<KafkaMessageData> finalHandler = handler;
                            CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
                        }
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


    //    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service : "+e.getMessage());
//        }
//    }
    @Async
    private void saveCountryHandler(KafkaMessageData messageData) {
        try {
            SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveCountrySharedDataMessage.class);
            countryService.saveCountry(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateCountryHandler(KafkaMessageData messageData) {
        try {
            UpdateCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateCountrySharedDataMessage.class);
            countryService.updateCountry(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveStateHandler(KafkaMessageData messageData) {
        try {
            SaveStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveStateSharedDataMessage.class);
            stateService.saveStateEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateStateHandler(KafkaMessageData messageData) {
        try {
            UpdateStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateStateSharedDataMessage.class);
            stateService.updateStateEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveCityHandler(KafkaMessageData messageData) {
        try {
            SaveCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveCitySharedDataMessage.class);
            cityService.saveCityEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateCityHandler(KafkaMessageData messageData) {
        try {
            UpdateCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateCitySharedDataMessage.class);
            cityService.updateCityEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void savePincodeHandler(KafkaMessageData messageData) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.savePincode(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updatePincodeHandler(KafkaMessageData messageData) {
        try {
            UpdatePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdatePincodeSharedDataMessage.class);
            pincodeService.updatePincode(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveAreaHandler(KafkaMessageData messageData) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveAreaSharedDataMessage.class);
            areaService.saveAreaEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateAreaHandler(KafkaMessageData messageData) {
        try {
            UpdateAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateAreaSharedDataMessage.class);
            areaService.updateAreaEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveServiceAreaHandler(KafkaMessageData messageData) {
        try {
            SaveServiceAreaSharedDataMessge dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveServiceAreaSharedDataMessge.class);
            serviceAreaService.saveServiceArea(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateServiceAreaHandler(KafkaMessageData messageData) {
        try {
            UpdateServiceAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateServiceAreaSharedDataMessage.class);
            serviceAreaService.updateServiceArea(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveBusinessUnitHandler(KafkaMessageData messageData) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusineeUnit(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateBusinessUnitHandler(KafkaMessageData messageData) {
        try {
            UpdateBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnit(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveBranchHandler(KafkaMessageData messageData) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveBranchSharedDataMessage.class);
            branchService.saveBranch(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateBranchHandler(KafkaMessageData messageData) {
        try {
            UpdateBranchSharedData dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateBranchSharedData.class);
            branchService.updateBranch(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveTeamHandler(KafkaMessageData messageData) {
        try {
            SaveTeamsSharedSharedData dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveTeamsSharedSharedData.class);
            teamsService.saveTeams(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateTeamHandler(KafkaMessageData messageData) {
        try {
            UpdateTeamsSharedData dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateTeamsSharedData.class);
            teamsService.updateTeams(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveHeirarchyHandler(KafkaMessageData messageData) {
        try {
            SaveHierarchyShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveHierarchyShareDataMessage.class);
            hierarchyService.saveHierachy(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateHeirarchyHandler(KafkaMessageData messageData) {
        try {

            UpdateHierarchyShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateHierarchyShareDataMessage.class);
            hierarchyService.updateHierarchy(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.info("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveETRTicketHandler(KafkaMessageData messageData) {
        try {
            TicketETRAuditMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), TicketETRAuditMessage.class);
            caseService.saveETRAudit(dataMessage.getCustomerData());
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.info("Task Service Receive Kafka Error Message From Notification-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveTATTicketHandler(KafkaMessageData messageData) {
        try {
            TicketAuditMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), TicketAuditMessage.class);
            caseService.saveTATAudit(dataMessage.getCustomerData());
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.info("Task Service Receive Kafka Error Message From Notification-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveRegionHandler(KafkaMessageData messageData) {
        try {
            SaveRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveRegionSharedDataMessage.class);
            regionService.saveRegion(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateRegionHandler(KafkaMessageData messageData) {
        try {
            UpdateRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateRegionSharedDataMessage.class);
            regionService.updateRegion(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveBusinessVerticalHandler(KafkaMessageData messageData) {
        try {
            SaveBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.saveBusinessVertical(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateBusinessVerticalHandler(KafkaMessageData messageData) {
        try {
            UpdateBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.updateBusinessVertical(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveCustomerDataHandler(KafkaMessageData messageData) {
        try {
            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveCustomerDataShareMessage.class);
            customersService.saveCustomers(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateCustomerDataHandler(KafkaMessageData messageData) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateCustomerShareDataMessage.class);
            customersService.updateCustomers(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveStaffUserHandler(KafkaMessageData messageData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveStaffUserSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.saveStaffuser(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateStaffUserHandler(KafkaMessageData messageData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateStaffUserSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), UpdateStaffUserSharedDataMessage.class);
            staffUserService.updateStaffUser(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveRoleDataHandler(KafkaMessageData messageData) {
        try {
            SaveRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveRoleSharedDataMessage.class);
            roleService.saveRoleEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    private void updateRoleHandler(KafkaMessageData messageData) {
        try {
            UpdateRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateRoleSharedDataMessage.class);
            roleService.updateRoleEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveMvnoHandler(KafkaMessageData messageData) {
        try {
            SaveMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updatMvnoHandler(KafkaMessageData messageData) {
        try {
            UpdateMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveServicesHandler(KafkaMessageData messageData) {
        try {
            SaveServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveServicesSharedDataMessage.class);
            planService.savePlanServiceEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateServicesHandler(KafkaMessageData messageData) {
        try {
            UpdateServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateServicesSharedDataMessage.class);
            planService.updatePlanServiceEntity(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void savePostpaidPlanHandler(KafkaMessageData messageData) {
        try {
            SavePlanSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SavePlanSharedDataMessage.class);
            postPaidPlanService.savePostpaidPlan(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updatePostpaidPlanHandler(KafkaMessageData messageData) {
        try {
            UpdatePlanSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdatePlanSharedDataMessage.class);
            postPaidPlanService.updatePostPaidPlan(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void savePartnerHandler(KafkaMessageData messageData) {
        try {
            SavePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SavePartnerSharedDataMessage.class);
            partnerService.savePartnerService(dataMessage);
            log.info("Task Service Receive Kafka Message From Partner-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Partner-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updatePartnerHandler(KafkaMessageData messageData) {
        try {
            UpdatePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdatePartnerSharedDataMessage.class);
            partnerService.updatePartnerService(dataMessage);
            log.info("Task Service Receive Kafka Message From Partner-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Partner-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void getEmailConfigrationHandler(KafkaMessageData messageData) {
        try {
            EmailConfigSendToAPIGWMsg dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), EmailConfigSendToAPIGWMsg.class);
            log.info("Task Service Receive Kafka Message From Notification-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Notification-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveCafToCustomerHandler(KafkaMessageData messageData) {
        try {
            CAFCustomerStatusMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), CAFCustomerStatusMessage.class);
            customersService.saveCafToCustomer(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveClientServiceHandler(KafkaMessageData messageData) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updateClientServiceHandler(KafkaMessageData messageData) {
        try {
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateClientServMessage.class);
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    @Transactional
    public void saveRoleHandler(KafkaMessageData messageData) {
        try {
            CommonRoleMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), CommonRoleMessage.class);
            roleService.saveRole(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (RuntimeException e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void deleteRoleHandler(KafkaMessageData messageData) {
        try {
            CommonRoleMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), CommonRoleMessage.class);
            roleService.deleteRole(dataMessage);
            log.info("Task Service Receive Kafka Message From Common-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void saveChangePlanHandler(KafkaMessageData messageData) {
        try {
            ChangePlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), ChangePlanMessage.class);
            customersService.saveCustomersPlanAndServiceData(dataMessage);
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);
        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service" + e.getMessage());
        }
    }

    @Async
    private void updatemvnoIspToIspHandler(KafkaMessageData messageData) {
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), UpdateMvnoData.class);
            mvnoService.updateMvnoIsp(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Task Service Receive Kafka Message From CMS-Micro-Service" + dataMessage);

        } catch (Exception e) {
            log.error("Task Service Receive Kafka Error Message From Common-Micro-Service" + e.getMessage());
        }
    }

    @Async
    public void handlesaveTicketDataFromMicroService(KafkaMessageData message) {
        try {
            CloseTicketCheckMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CloseTicketCheckMessage.class);
            caseCustometDetailsService.saveCaseCustomerDetails(dataMessage);
            log.info("TASK-MGMT Service Receive Kafka Message From Ticket-Mgmt-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handleupdateTicketDataFromMicroService(KafkaMessageData message) {
        try {
            CloseTicketCheckMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CloseTicketCheckMessage.class);
            caseCustometDetailsService.updateCaseCustomerDetails(dataMessage);
            log.info("TASK-MGMT Receive Kafka Message From Ticket-Mgmt-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleCustomerQuotaInfo" + e.getMessage());
        }
    }

    @Async
    public void handleStaffUserServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SaveStaffAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()), SaveStaffAssignmentMessage.class);
            serviceAreaService.assignStaffToServiceArea(dataMessage.getMappingList());
        } catch (Exception e) {
            log.error("Error parsing message", e);
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

//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCustomerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From CMS-Micro-Service : "+e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC}, groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormPartnerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Partner-Micro-Service : "+e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC}, groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Inventory-Micro-Service : "+e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC}, groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Revenue-Micro-Service : "+e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC}, groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Notification-Micro-Service : "+e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC}, groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//            if (handler == null) {
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            log.error("Task Service Receive Kafka Error Message From Radius-Micro-Service : " + e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA},groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
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
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : "+ e.getMessage());
//        }
//    }
}
