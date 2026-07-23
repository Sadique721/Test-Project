package com.savbill.partnermanagement.kafka;

import com.savbill.partnermanagement.MicroSeviceDataShare.PartnerAmountMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SaveCustomerDataShareMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.UpdateClientServMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.UpdateCustomerShareDataMessage;
import com.savbill.partnermanagement.customers.CustomerService;
import com.savbill.partnermanagement.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.savbill.partnermanagement.modules.Charge.service.ChargeService;
import com.savbill.partnermanagement.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.partnermanagement.modules.MasterManagement.Area.AreaService;
import com.savbill.partnermanagement.modules.MasterManagement.Branch.BranchService;
import com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit.BusinessUnitService;
import com.savbill.partnermanagement.modules.MasterManagement.City.CityService;
import com.savbill.partnermanagement.modules.MasterManagement.Country.CountryService;
import com.savbill.partnermanagement.modules.MasterManagement.Pincode.PincodeService;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.partnermanagement.modules.MasterManagement.State.StateService;
import com.savbill.partnermanagement.modules.Mvno.MvnoService;
import com.savbill.partnermanagement.modules.Mvno.UpdateMvnoData;
import com.savbill.partnermanagement.modules.Plan.service.PostPaidPlanService;
import com.savbill.partnermanagement.modules.PlanGroup.service.PlanGroupService;
import com.savbill.partnermanagement.modules.PlanService.PlanServiceService;
import com.savbill.partnermanagement.modules.Region.service.RegionService;
import com.savbill.partnermanagement.modules.Role.RoleService;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserService;
import com.savbill.partnermanagement.modules.Tax.service.TaxService;
import com.savbill.partnermanagement.modules.Teams.TeamsService;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.partner.service.PricebookService;
import com.savbill.partnermanagement.rabbitmq.SavePlanAssignmentMessage;
import com.savbill.partnermanagement.rabbitmq.master.*;
import com.savbill.partnermanagement.rabbitmq.product.*;
import com.savbill.partnermanagement.rabbitmq.setting.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.savbill.partnermanagement.rabbitmq.master.*;
import com.savbill.partnermanagement.rabbitmq.product.*;
import com.savbill.partnermanagement.rabbitmq.setting.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    PincodeService pincodeService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private BusinessUnitService businessUnitService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private BusinessVerticalsService businessVerticalsService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private RoleService roleService;
    @Autowired
    PlanServiceService serviceService;
    @Autowired
    TaxService taxService;
    @Autowired
    ChargeService chargeService;
    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    PostPaidPlanService postPaidPlanService;
    @Autowired
    PricebookService pricebookService;
    @Autowired
    TeamsService teamsService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    KafkaConsumerConfig consumerConfig;
    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();


    public KafkaMessageReceiver() throws Exception {
        messageHandlers.put("SaveCountrySharedDataMessage", this::handleSaveCountry);
        messageHandlers.put("SaveStateSharedDataMessage", this::handleSaveState);
        messageHandlers.put("SaveCitySharedDataMessage", this::handleSaveCity);
        messageHandlers.put("SavePincodeSharedDataMessage", this::handleSavePincode);
        messageHandlers.put("SaveAreaSharedDataMessage", this::handleSaveArea);
        messageHandlers.put("SaveServiceAreaSharedDataMessge", this::handleSaveServiceArea);
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleSaveBusinessUnit);
        messageHandlers.put("SaveBranchSharedDataMessage", this::handleSaveBranch);
        messageHandlers.put("SaveRegionSharedDataMessage", this::handleSaveRegion);
        messageHandlers.put("SaveBusinessVerticalSharedDataMessage", this::handleSaveBusinessVertical);
        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleSaveStaffUser);
        messageHandlers.put("SaveClientServMessge", this::handleSaveClientService);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleSaveMvno);
        messageHandlers.put("SaveStaffAssignmentMessage", this::handleStaffUserServiceAreaMapping);


        messageHandlers.put("UpdateCountrySharedDataMessage", this::handleUpdateCountry);
        messageHandlers.put("UpdateStateSharedDataMessage", this::handleUpdateState);
        messageHandlers.put("UpdateCitySharedDataMessage", this::handleUpdateCity);
        messageHandlers.put("UpdatePincodeSharedDataMessage", this::handleUpdatePincode);
        messageHandlers.put("UpdateAreaSharedDataMessage", this::handleUpdateArea);
        messageHandlers.put("UpdateServiceAreaSharedDataMessage", this::handleUpdateServiceArea);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleUpdateBusinessUnit);
        messageHandlers.put("UpdateBranchSharedData", this::handleUpdateBranch);
        messageHandlers.put("UpdateRegionSharedDataMessage", this::handleUpdateRegion);
        messageHandlers.put("UpdateBusinessVerticalSharedDataMessage", this::handleUpdateBusinessVertical);
        messageHandlers.put("UpdateStaffUserSharedDataMessage", this::handleUpdateStaffUser);
        messageHandlers.put("UpdateMvnoData", this::handleUpdateMvnoISP);


        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.CREATE_DATA_ROLE, this::handleCreateRole);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.DELETE_DATA_ROLE, this::handleDeleteRole);
        messageHandlers.put("SaveServicesSharedDataMessage", this::handleCreateService);
        messageHandlers.put("UpdateServicesSharedDataMessage", this::handleUpdateService);
        messageHandlers.put("SaveTaxSharedDataMessage", this::handleCreateTax);
        messageHandlers.put("UpdateTaxSharedDataMessage", this::handleUpdateTax);
        messageHandlers.put("SaveChargeSharedDataMessage", this::handleCreateCharge);
        messageHandlers.put("UpdateChargeSharedDataMessage", this::handleUpdateCharge);
        messageHandlers.put("UpdatePlanSharedDataMessage", this::handleUpdatePlan);
        messageHandlers.put("SavePlanSharedDataMessage", this::handleSavePlan);
        messageHandlers.put("SavePlanGroupSharedDataMessage", this::handleSavePlanGroup);
        messageHandlers.put("UpdatePlanGroupSharedDataMessage", this::handleUpdatePlanGroup);
        messageHandlers.put("SavePricebookSharedMessage", this::handleSavePriceBook);
        messageHandlers.put("UpdatePricebookSharedMessage", this::handleUpdatePriceBook);
        messageHandlers.put("SaveTeamsSharedSharedData", this::handleCreateTeam);
        messageHandlers.put("UpdateTeamsSharedData", this::handleUpdateTeam);
        messageHandlers.put("SaveCustomerDataShareMessage", this::handleSaveCustomers);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleUpdateCustomers);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleUpdateMvnoSharedDataMessage);
        messageHandlers.put("SaveClientServMessge:" + KafkaConstant.CREATE_SERVICE_CONFIG, this::handleCreateClientService);
        messageHandlers.put("UpdateClientServMessage:" + KafkaConstant.UPDATE_SERVICE_CONFIG, this::handleUpdateClientService);

        //REVENUE
        messageHandlers.put("PartnerAmountMessage:" + KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER, this::handlePartnerBalance);
        messageHandlers.put("SavePlanAssignmentMessage", this::handlePlanServiceAreaMapping);

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
    @javax.transaction.Transactional
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
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC,
                    KafkaConstant.KAFKA_SALES_CRM_TOPIC,
                    KafkaConstant.KAFKA_INTEGRATION_TOPIC,
                    KafkaConstant.KAFKA_NETCONFIG_TOPIC,
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
                        Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);

                        if (handler == null) {
                            handler = messageHandlers.get(dataType);
                        }

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
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

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    private void handleSaveCountry(KafkaMessageData kafkaMessageData) {
        try {
            SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveCountrySharedDataMessage.class);
            countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveState(KafkaMessageData kafkaMessageData) {
        try {
            SaveStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveStateSharedDataMessage.class);
            stateService.saveStateEntity(dataMessage);
            log.info("Handled SaveStateSharedDataMessage successfully: " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Error handling SaveStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveCity(KafkaMessageData kafkaMessageData) {
        try {
            SaveCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveCitySharedDataMessage.class);
            cityService.saveCityEntity(dataMessage);
            log.info("Handled SaveCitySharedDataMessage successfully: " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Error handling SaveCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSavePincode(KafkaMessageData kafkaMessageData) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.savePincodeEntity(dataMessage);
            log.info("Handled SavePincodeSharedDataMessage successfully: " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Error handling SavePincodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveArea(KafkaMessageData kafkaMessageData) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveAreaSharedDataMessage.class);
            areaService.saveAreaEntiry(dataMessage);
            log.info("Handled SaveAreaSharedDataMessage successfully: " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Error handling SaveAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveServiceArea(KafkaMessageData kafkaMessageData) {
        try {
            SaveServiceAreaSharedDataMessge dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveServiceAreaSharedDataMessge.class);
            serviceAreaService.saveServiceAreaEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling SaveServiceAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveBusinessUnit(KafkaMessageData kafkaMessageData) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusinessUnitEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveBranch(KafkaMessageData kafkaMessageData) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveBranchSharedDataMessage.class);
            branchService.saveBranch(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling SaveBranchSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveRegion(KafkaMessageData kafkaMessageData) {
        try {
            SaveRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveRegionSharedDataMessage.class);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
            regionService.saveRegion(dataMessage);
        } catch (JsonSyntaxException e) {
            log.error("Partner Error handling SaveRegionSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveBusinessVertical(KafkaMessageData kafkaMessageData) {
        try {
            SaveBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.saveBusinessVertical(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (JsonSyntaxException e) {
            log.error("Partner Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveStaffUser(KafkaMessageData kafkaMessageData) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.saveStaffUserEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveClientService(KafkaMessageData kafkaMessageData) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling handleSaveClientService: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSaveMvno(KafkaMessageData kafkaMessageData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvnoSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateCountry(KafkaMessageData kafkaMessageData) {
        try {
            UpdateCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), UpdateCountrySharedDataMessage.class);
            countryService.updateCountry(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + kafkaMessageData);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateCountry: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateState(KafkaMessageData message) {
        try {
            UpdateStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStateSharedDataMessage.class);
            stateService.updateStateEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateState : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateCity(KafkaMessageData message) {
        try {
            UpdateCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCitySharedDataMessage.class);
            cityService.updateCityEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateCity : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdatePincode(KafkaMessageData message) {
        try {
            UpdatePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePincodeSharedDataMessage.class);
            pincodeService.updatePincodeEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdatePincode : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateArea(KafkaMessageData message) {
        try {
            UpdateAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateAreaSharedDataMessage.class);
            areaService.updateAreaEntiry(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateArea : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateServiceArea(KafkaMessageData message) {
        try {
            UpdateServiceAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServiceAreaSharedDataMessage.class);
            serviceAreaService.updateServiceAreaEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateServiceArea : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateBusinessUnit(KafkaMessageData message) {
        try {
            UpdateBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnitEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateBusinessUnit : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateBranch(KafkaMessageData message) {
        try {
            UpdateBranchSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBranchSharedData.class);
            branchService.updateBranch(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateBranch : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateRegion(KafkaMessageData message) {
        try {
            UpdateRegionSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateRegionSharedDataMessage.class);
            regionService.updateRegion(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (JsonSyntaxException e) {
            log.error("Partner Error handling handleUpdateRegion : " + e.getMessage(), e);
        }

    }

    @Async
    private void handleUpdateBusinessVertical(KafkaMessageData message) {
        try {
            UpdateBusinessVerticalSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessVerticalSharedDataMessage.class);
            businessVerticalsService.updateBusinessVertical(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (JsonSyntaxException e) {
            log.error("Partner Error handling handleUpdateBusinessVertical : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateStaffUser(KafkaMessageData message) {
        try {
            UpdateStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStaffUserSharedDataMessage.class);
            staffUserService.updatetaffUserEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateStaffUser : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateClientService(KafkaMessageData message) {
        try {
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateClientServMessage.class);
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateClientService : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateMvnoISP(KafkaMessageData message) {
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoData.class);
            mvnoService.UpdateMvnoIdISP(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateMvno : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleCreateRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            roleService.saveRole(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateRole : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleDeleteRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            roleService.deleteRole(dataMessage);
            log.info("Partner Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleDeleteRole : " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCMSMicroService(KafkaMessageData message) {
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

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    private void handleCreateService(KafkaMessageData message) {
        try {
            SaveServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServicesSharedDataMessage.class);
            serviceService.savePlanServiceEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateService : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateService(KafkaMessageData message) {
        try {
            UpdateServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServicesSharedDataMessage.class);
            serviceService.updatePlanServiceEntity(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateService : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateTax(KafkaMessageData message) {
        try {
            UpdateTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTaxSharedDataMessage.class);
            taxService.updateTaxData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateTax : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleCreateTax(KafkaMessageData message) {
        try {
            SaveTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTaxSharedDataMessage.class);
            taxService.saveTaxData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateTax : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateCharge(KafkaMessageData message) {
        try {
            UpdateChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateChargeSharedDataMessage.class);
            chargeService.updateChargeData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateCharge : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleCreateCharge(KafkaMessageData message) {
        try {
            SaveChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveChargeSharedDataMessage.class);
            chargeService.saveChargeData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateCharge : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdatePlan(KafkaMessageData message) {
        try {
            UpdatePlanSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePlanSharedDataMessage.class);
            postPaidPlanService.updatePostPaidPlanData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdatePlan : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSavePlan(KafkaMessageData message) {
        try {
            SavePlanSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SavePlanSharedDataMessage.class);
            postPaidPlanService.savePostPaidPlanData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleSavePlan : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSavePlanGroup(KafkaMessageData message) {
        try {
            SavePlanGroupSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SavePlanGroupSharedDataMessage.class);
            planGroupService.savePlanGroupData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleSavePlanGroup : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdatePlanGroup(KafkaMessageData message) {
        try {
            UpdatePlanGroupSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdatePlanGroupSharedDataMessage.class);
            planGroupService.updatePlanGroupData(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdatePlanGroup : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSavePriceBook(KafkaMessageData message) {
        try {
            SavePricebookSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePricebookSharedMessage.class);
            pricebookService.save(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleSavePriceBook : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdatePriceBook(KafkaMessageData message) {
        try {
            UpdatePricebookSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePricebookSharedMessage.class);
            pricebookService.update(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdatePriceBook : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleCreateTeam(KafkaMessageData message) {
        try {
            SaveTeamsSharedSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTeamsSharedSharedData.class);
            teamsService.saveTeams(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateTeam : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateTeam(KafkaMessageData message) {
        try {
            UpdateTeamsSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTeamsSharedData.class);
            teamsService.updateTeams(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateTeam : " + e.getMessage(), e);
        }
    }

    private void handleSaveCustomers(KafkaMessageData message) {
        try {
            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCustomerDataShareMessage.class);
            customerService.saveCustomers(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleSaveCustomers : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleUpdateCustomers(KafkaMessageData message) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            customerService.updateCustomers(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleUpdateCustomers : " + e.getMessage(), e);
        }
    }

    @Async
    private void handleCreateClientService(KafkaMessageData message) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handleCreateClientService : " + e.getMessage(), e);
        }
    }

    @Async
    private void handlePartnerBalance(KafkaMessageData message) {
        try {
            PartnerAmountMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PartnerAmountMessage.class);
            partnerService.updateAmount(dataMessage);
            log.info("Partner Service Receive Kafka Message From CMS-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Partner Error handling handlePartnerBalance : " + e.getMessage(), e);
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
    public void handlePlanServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SavePlanAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()),SavePlanAssignmentMessage.class);
            postPaidPlanService.assignPlanToServiceArea(dataMessage.getMappingList());
        } catch (Exception e) {
            log.error("Error parsing message", e);
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }


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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }


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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC}, groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message){
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_SALES_CRM_TOPIC}, groupId = KafkaConstant.KAFKA_SALES_CRM_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormSalesCrmMicroService(KafkaMessageData message) {
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }

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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NETCONFIG_TOPIC}, groupId = KafkaConstant.KAFKA_NETCONFIG_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNetConfigMicroService(KafkaMessageData message) {
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
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }
}
