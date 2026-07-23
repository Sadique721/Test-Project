package com.savbill.inventorymanagement.kafka;

import com.savbill.inventorymanagement.modules.CasMaster.CasMasterService;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeService;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMappingService;
import com.savbill.inventorymanagement.modules.Customers.CustomerService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InvoiceService.PrepaidInvoiceService;
import com.savbill.inventorymanagement.modules.MasterManagement.Area.AreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.Branch.BranchService;
import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnitService;
import com.savbill.inventorymanagement.modules.MasterManagement.City.CityService;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryService;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.PincodeService;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.State.StateService;
import com.savbill.inventorymanagement.modules.Mvno.MvnoService;
import com.savbill.inventorymanagement.modules.Mvno.UpdateMvnoData;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerService;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroupService;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceService;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanService;
import com.savbill.inventorymanagement.modules.Role.RoleService;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsService;
import com.savbill.inventorymanagement.rabbitmq.CAFCustomerStatusMessage;
import com.savbill.inventorymanagement.rabbitmq.CommonRoleMessage;
import com.savbill.inventorymanagement.rabbitmq.CustomerInventoryMappingMessage;
import com.savbill.inventorymanagement.rabbitmq.SavePlanAssignmentMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private BusinessUnitService businessUnitService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private ClientServiceService clientServiceSrv;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TeamsService teamsService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private PlanServiceService planService;

    @Autowired
    private TaxService taxService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PlanGroupService planGroupService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CasMasterService casMasterService;

    @Autowired
    @Lazy
    private CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    KafkaConsumerConfig consumerConfig;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final Logger log = Logger.getLogger(KafkaMessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    public KafkaMessageReceiver() {

        //Receive Common-Side Messages

        messageHandlers.put("SaveCountrySharedDataMessage", this::handleCountry);
        messageHandlers.put("UpdateCountrySharedDataMessage", this::handleUpdateCountry);
        messageHandlers.put("SaveStateSharedDataMessage", this::handleState);
        messageHandlers.put("UpdateStateSharedDataMessage", this::handleUpdateState);
        messageHandlers.put("SaveCitySharedDataMessage", this::handleCity);
        messageHandlers.put("UpdateCitySharedDataMessage", this::handleUpdateCity);
        messageHandlers.put("SavePincodeSharedDataMessage", this::handlePincode);
        messageHandlers.put("UpdatePincodeSharedDataMessage", this::handleUpdatePincode);
        messageHandlers.put("SaveAreaSharedDataMessage", this::handleArea);
        messageHandlers.put("UpdateAreaSharedDataMessage", this::handleUpdateArea);
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleBusinessUnit);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleUpdateBusinessUnit);
        messageHandlers.put("SaveServiceAreaSharedDataMessge", this::handleServiceArea);
        messageHandlers.put("UpdateServiceAreaSharedDataMessage", this::handleUpdateServiceArea);
        messageHandlers.put("SaveBranchSharedDataMessage", this::handleBranch);
        messageHandlers.put("UpdateBranchSharedData", this::handleUpdateBranch);
        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleSaveStaffUserSharedDataMessage);
        messageHandlers.put("UpdateStaffUserSharedDataMessage", this::handleUpdateStaffUser);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.CREATE_DATA_ROLE, this::handleCreateDataRole);
        messageHandlers.put("CommonRoleMessage:" + KafkaConstant.DELETE_DATA_ROLE, this::handleDeleteDataRole);
        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleSaveMvnoSharedDataMessage);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleUpdateMvnoSharedDataMessage);
        messageHandlers.put("UpdateMvnoData", this::handleUpdateMvnoData);
        messageHandlers.put("SaveClientServMessge:" + KafkaConstant.CREATE_SERVICE_CONFIG, this::handleSaveClientServMess);
        messageHandlers.put("UpdateClientServMessage:" + KafkaConstant.UPDATE_SERVICE_CONFIG, this::handleUpdateClientServMess);
        messageHandlers.put("SaveStaffAssignmentMessage", this::handleStaffUserServiceAreaMapping);
        messageHandlers.put("SavePlanAssignmentMessage", this::handlePlanServiceAreaMapping);



        //Receive Cms-Side Message
        messageHandlers.put("SaveTeamsSharedSharedData", this::handleSaveTeamSharedDataMessage);
        messageHandlers.put("UpdateTeamsSharedData", this::handleUpdateTeam);
        messageHandlers.put("SaveServicesSharedDataMessage", this::handleSaveServices);
        messageHandlers.put("UpdateServicesSharedDataMessage", this::handleUpdateServicesSharedDataMessage);
        messageHandlers.put("SaveTaxSharedDataMessage", this::handleSaveTaxSharedDataMessage);
        messageHandlers.put("UpdateTaxSharedDataMessage", this::handleUpdateTaxSharedDataMessage);
        messageHandlers.put("SavePlanSharedDataMessage", this::handleSavePlanShared);
        messageHandlers.put("UpdatePlanSharedDataMessage", this::handleUpdatePlanShared);
        messageHandlers.put("SavePlanGroupSharedDataMessage", this::handleSavePlanGroupSharedDataMessage);
        messageHandlers.put("UpdatePlanGroupSharedDataMessage", this::handleUpdatePlanGroupSharedDataMessage);
        messageHandlers.put("SaveChargeSharedDataMessage", this::handleSaveChargeSharedDataMessage);
        messageHandlers.put("UpdateChargeSharedDataMessage", this::handleUpdateChargeSharedDataMessage);
        messageHandlers.put("SaveCustomerDataShareMessage", this::handleSaveCustomerDataShareMessage);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleUpdateCustomerShareDataMessage);
        messageHandlers.put("SaveHierarchyShareDataMessage", this::handleSaveHierarchyShareDataMessage);
        messageHandlers.put("UpdateHierarchyShareDataMessage", this::handleUpdateHierarchyShareDataMessage);
        messageHandlers.put("CAFCustomerStatusMessag", this::handleCAFCustomerStatusMessage);
        messageHandlers.put("SaveCasMasterSharedDataMessage", this::handleSaveCasMasterSharedDataMessage);
        messageHandlers.put("UpdateCasMasterSharedDataMessage", this::handleUpdateCasMasterSharedDataMessage);
        messageHandlers.put("CustomerInventoryMappingMessage", this::handleCustomerInventoryMappingMessage);
        messageHandlers.put("ServiceTerminationMessage", this::handleMessageServiceTermination);
        //Receive Partner Message

        messageHandlers.put("SavePartnerSharedDataMessage:" + KafkaConstant.CREATE_PARTNER, this::handleSavePartnerSharedDataMessage);
        messageHandlers.put("UpdatePartnerSharedDataMessage:" + KafkaConstant.UPDATE_PARTNER, this::handleUpdatePartnerSharedDataMessage);
        messageHandlers.put("CaftoCustomerMessage", this::handleMessageCafToCustomere);
        messageHandlers.put("UpdateCustplanMappingMessage", this::handleUpdateCustPlanMappinForVoidPlan);
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
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_TOPIC,
                    KafkaConstant.KAFKA_NOTIFICATION_TOPIC,
                    KafkaConstant.KAFKA_RADIUS_TOPIC,
                    KafkaConstant.KAFKA_TICKET_TOPIC,
                    KafkaConstant.KAFKA_SALES_CRM_TOPIC,
                    KafkaConstant.KAFKA_NETCONFIG_TOPIC,
                    KafkaConstant.KAFKA_INTEGRATION_TOPIC,
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA)));

//            long startTime = System.currentTimeMillis();

            while (true) {
                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
//                log.debug(".......(Received Kafka records from topic. Number of records)........: " + records.count());

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
//                        log.debug("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
                    } catch (Exception e) {
                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
                    }
                }

//                long endTime = System.currentTimeMillis();
//                log.debug(":::::::::::::::::: (Total time taken for per poll in consumer (ms)):::::::::::::: " + (endTime - startTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Radius-Micro-Service: " + e.getMessage(), e);
        }
    }
//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID,containerFactory = "kafkaListenerContainerFactory")
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
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.error("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From COMMON-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleCountry(KafkaMessageData message) {
        try {
            SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save country with name: " + dataMessage.getName());
            countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCountry(KafkaMessageData message) {
        try {
            UpdateCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCountrySharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update country with name: " + dataMessage.getName());
            countryService.updateCountry(dataMessage);
            log.info("Handled UpdateCountrySharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleState(KafkaMessageData message) {
        try {
            SaveStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStateSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save state with name: " + dataMessage.getName());
            stateService.saveStateEntity(dataMessage);
            log.info("Handled SaveStateSharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateState(KafkaMessageData message) {
        try {
            UpdateStateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStateSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update state with name: " + dataMessage.getName());
            stateService.updateStateEntity(dataMessage);
            log.info("Handled UpdateStateSharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateStateSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCity(KafkaMessageData message) {
        try {
            SaveCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCitySharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save city with name: " + dataMessage.getName());
            cityService.saveCityEntity(dataMessage);
            log.info("Handled SaveCitySharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCity(KafkaMessageData message) {
        try {
            UpdateCitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCitySharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update city with name: " + dataMessage.getName());
            cityService.updateCityEntity(dataMessage);
            log.info("Handled UpdateCitySharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateCitySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePincode(KafkaMessageData message) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePincodeSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save pincode with name: " + dataMessage.getPincode());
            pincodeService.savePincodeEntity(dataMessage);
            log.info("Handled SavePincodeSharedDataMessage successfully with name: " + dataMessage.getPincode());
        } catch (Exception e) {
            log.error("Error handling SavePincodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdatePincode(KafkaMessageData message) {
        try {
            UpdatePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePincodeSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update pincode with name: " + dataMessage.getPincode());
            pincodeService.updatePincodeEntity(dataMessage);
            log.info("Handled UpdatePincodeSharedDataMessage successfully with name: " + dataMessage.getPincode());
        } catch (Exception e) {
            log.error("Error handling UpdatePincodeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleArea(KafkaMessageData message) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveAreaSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save area with name: " + dataMessage.getName());
            areaService.saveAreaEntiry(dataMessage);
            log.info("Handled SaveAreaSharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateArea(KafkaMessageData message) {
        try {
            UpdateAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateAreaSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update area with name: " + dataMessage.getName());
            areaService.updateAreaEntiry(dataMessage);
            log.info("Handled UpdateAreaSharedDataMessage successfully with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBusinessUnit(KafkaMessageData message) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save business unit with name: " + dataMessage.getBuname());
            businessUnitService.saveBusinessUnitEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getBuname());
        } catch (Exception e) {
            log.error("Error handling SaveBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBusinessUnit(KafkaMessageData message) {
        try {
            UpdateBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBusinessUnitSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update business unit with name: " + dataMessage.getBuname());
            businessUnitService.updateBusinessUnitEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getBuname());
        } catch (Exception e) {
            log.error("Error handling UpdateBusinessUnitSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleServiceArea(KafkaMessageData message) {
        try {
            SaveServiceAreaSharedDataMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServiceAreaSharedDataMessge.class);
            log.debug("Inventory Receive Attempting to save service area with name: " + dataMessage.getName());
            serviceAreaService.saveServiceAreaEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveServiceAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateServiceArea(KafkaMessageData message) {
        try {
            UpdateServiceAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServiceAreaSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update service area with name: " + dataMessage.getName());
            serviceAreaService.updateServiceAreaEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateServiceAreaSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBranch(KafkaMessageData message) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBranchSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save branch with name: " + dataMessage.getName());
            branchService.saveBranch(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveBranchSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateBranch(KafkaMessageData message) {
        try {
            UpdateBranchSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateBranchSharedData.class);
            log.debug("Inventory Receive Attempting to update branch with name: " + dataMessage.getName());
            branchService.updateBranch(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateBranchSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveStaffUserSharedDataMessage(KafkaMessageData message) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStaffUserSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save staff with name: " + dataMessage.getUsername());
            staffUserService.saveStaffUserEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling SaveStaffUserSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateStaffUser(KafkaMessageData message) {
        try {
            UpdateStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStaffUserSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update staff with name: " + dataMessage.getUsername());
            staffUserService.updatetaffUserEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling UpdateStaffUserSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCreateDataRole(KafkaMessageData message) {
        try {
            SaveRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveRoleSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save role with name: " + dataMessage.getRolename());
            roleService.saveRoleEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getRolename());
        } catch (Exception e) {
            log.error("Error handling CreateDataRole: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteDataRole(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            log.debug("Inventory Receive Attempting to delete role with name: " + dataMessage.getRolename());
            roleService.deleteRole(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getRolename());
        } catch (Exception e) {
            log.error("Error handling DeleteRole: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Async
    public void handleSaveMvnoSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveMvnoSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save mvno with name: " + dataMessage.getName());
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Async
    public void handleUpdateMvnoSharedDataMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateMvnoSharedDataMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdateMvnoSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update mvno with name: " + dataMessage.getName());
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveTeamSharedDataMessage(KafkaMessageData message) {
        try {
            //SaveTeamsSharedSharedData dataMessage=new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()),SaveTeamsSharedSharedData.class);
            SaveTeamsSharedSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTeamsSharedSharedData.class);
            log.debug("Inventory Receive Attempting to save team with name: " + dataMessage.getName());
            teamsService.saveTeams(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveTeamSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateTeam(KafkaMessageData message) {
        try {
            UpdateTeamsSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTeamsSharedData.class);
            log.debug("Inventory Receive Attempting to update team with name: " + dataMessage.getName());
            teamsService.updateTeams(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateTeamSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateMvnoData(KafkaMessageData message) {
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoData.class);
            log.debug("Inventory Receive Attempting to update mvno id isp to isp with old mvno id: " + dataMessage.getOldmvnoId());
            mvnoService.updateMvnoIdIsptoIsp(dataMessage.getOldmvnoId(), dataMessage.getNewmvnoId());
            log.info("Inventory Service Receive Kafka Message From Common-Micro-Service with old mvno id: " + dataMessage.getOldmvnoId());
        } catch (Exception e) {
            log.error("Error handling UpdateMvnoData: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveClientServMess(KafkaMessageData message) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveClientServMessge.class);
            log.debug("Inventory Receive Attempting to save client service with name: " + dataMessage.getName());
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveClientServMess: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateClientServMess(KafkaMessageData message) {
        try {
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateClientServMessage.class);
            log.debug("Inventory Receive Attempting to update client service with name: " + dataMessage.getName());
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateClientServMess: " + e.getMessage(), e);
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
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC},groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormPartnerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From PMS-Micro-Service: " + e.getMessage(), e);
//        }
//    }

    @Async
    public void handlerName(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC},groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
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
    public void handleSaveServices(KafkaMessageData message) {
        try {
            SaveServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServicesSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save plan service with name: " + dataMessage.getName());
            planService.savePlanServiceEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Service: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateServicesSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServicesSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update plan service with name: " + dataMessage.getName());
            planService.updatePlanServiceEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateServicesSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveTaxSharedDataMessage(KafkaMessageData message) {
        try {
            SaveTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTaxSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save tax with name: " + dataMessage.getName());
            taxService.saveTaxEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling saveTaxEntity: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateTaxSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTaxSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update tax with name: " + dataMessage.getName());
            taxService.updateTaxEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling updateTaxSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSavePlanShared(KafkaMessageData message) {
        try {
            SavePlanSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SavePlanSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save postpaid plan with name: " + dataMessage.getName());
            postpaidPlanService.savePostPaidPlanEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SavePlanSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdatePlanShared(KafkaMessageData message) {
        try {
            UpdatePlanSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdatePlanSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update postpaid plan with name: " + dataMessage.getName());
            postpaidPlanService.updatePostPaidPlanEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdatePlanDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSavePlanGroupSharedDataMessage(KafkaMessageData message) {
        try {
            SavePlanGroupSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePlanGroupSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save plan group with name: " + dataMessage.getPlanGroupName());
            planGroupService.savePlanGroupEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getPlanGroupName());
        } catch (Exception e) {
            log.error("Error handling SavePlanGroupDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdatePlanGroupSharedDataMessage(KafkaMessageData message) {
        try {
            UpdatePlanGroupSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePlanGroupSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update plan group with name: " + dataMessage.getPlanGroupName());
            planGroupService.updatePlanGroupEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getPlanGroupName());
        } catch (Exception e) {
            log.error("Error handling UpdatePlanGroupSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveChargeSharedDataMessage(KafkaMessageData message) {
        try {
            SaveChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveChargeSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save charge with name: " + dataMessage.getName());
            chargeService.saveChargeEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling SaveChargeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateChargeSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateChargeSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update charge with name: " + dataMessage.getName());
            chargeService.updateChargeEntity(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Error handling UpdateChargeSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustomerDataShareMessage(KafkaMessageData message) {
        try {
            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCustomerDataShareMessage.class);
            log.debug("Inventory Receive Attempting to save customer with name: " + dataMessage.getUsername());
            customerService.saveCustomers(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling SaveCustomerDataShareMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustomerShareDataMessage(KafkaMessageData message) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            log.debug("Inventory Receive Attempting to update customer with name: " + dataMessage.getUsername());
            customerService.updateCustomers(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling UpdateCustomerShareDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveHierarchyShareDataMessage(KafkaMessageData message) {
        try {
            SaveHierarchyShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveHierarchyShareDataMessage.class);
            log.debug("Inventory Receive Attempting to save hierarchy with name: " + dataMessage.getHierarchyName());
            hierarchyService.saveHierachy(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getHierarchyName());
        } catch (Exception e) {
            log.error("Error handling SaveHierarchyShareDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateHierarchyShareDataMessage(KafkaMessageData message) {
        try {
            UpdateHierarchyShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateHierarchyShareDataMessage.class);
            log.debug("Inventory Receive Attempting to update hierarchy with name: " + dataMessage.getHierarchyName());
            hierarchyService.updateHierarchy(dataMessage);
            log.info("Inventory Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getHierarchyName());
        } catch (Exception e) {
            log.error("Error handling UpdateHierarchyShareDataMessage: " + e.getMessage(), e);
        }
    }

    //No-Use
//    public void handleProductDto(KafkaMessageData message) {
//        try {
//            ProductDto dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ProductDto.class);
//            productService.saveEntity(dataMessage);
//            log.info("Inventory Service Receive Kafka Message From CMS-Service  : " + message);
//        } catch (Exception e) {
//            log.error("Error handling handleProductDto: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleCAFCustomerStatusMessage(KafkaMessageData message) {
        try {
            CAFCustomerStatusMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CAFCustomerStatusMessage.class);
            log.debug("Inventory Receive Attempting to update area with id: " + dataMessage.getCustomerId());
            customerService.saveCafToCustomer(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service with id: " + dataMessage.getCustomerId());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    public void handleSaveCasMasterSharedDataMessage(KafkaMessageData message) {
        try {
            SaveCasMasterSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCasMasterSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save cas master with name: " + dataMessage.getCasname());
            casMasterService.saveCasMasterEntity(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service with name: " + dataMessage.getCasname());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    public void handleUpdateCasMasterSharedDataMessage(KafkaMessageData message) {
        try {
            UpdateCasMasterSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCasMasterSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update cas master with name: " + dataMessage.getCasname());
            casMasterService.updateCasMasterEntity(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service with name: " + dataMessage.getCasname());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    public void handleCustomerInventoryMappingMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerInventoryMappingMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerInventoryMappingMessage.class);
            log.debug("Inventory Receive Attempting to update customer inventory status");
            customerInventoryMappingService.updateCustomerInvStatusFromCMS(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service");
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    public void handleMessageServiceTermination(KafkaMessageData message) {
        try {
            ServiceTerminationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ServiceTerminationMessage.class);
            customerService.changeStatusOfCustServices(dataMessage.getCustomerServiceId(), dataMessage.getCustomerStatus(), dataMessage.getRemarks(), Boolean.FALSE, dataMessage.getGeneratecn());
            log.info("Handled receiveMessageServiceTermination successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageServiceTermination: " + e.getMessage(), e);
        }
    }

    @Async
    private void handleSavePartnerSharedDataMessage(KafkaMessageData message) {
        try {
            SavePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePartnerSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to save partner with name: " + dataMessage.getName());
            partnerService.savePartnerEntiry(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    @Async
    private void handleUpdatePartnerSharedDataMessage(KafkaMessageData message) {
        try {
            UpdatePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePartnerSharedDataMessage.class);
            log.debug("Inventory Receive Attempting to update partner with name: " + dataMessage.getName());
            partnerService.updatePartnerEntiry(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service with name: " + dataMessage.getName());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC},groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From REVENUE-Micro-Service: " + e.getMessage(), e);
//        }
//    }

    @Async
    public void handlerNamee(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC}, groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID,containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From NOTIFICATION-Micro-Service: " + e.getMessage(), e);
//        }
//    }

    @Async
    public void handlerrNamee(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC},groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From RADIUS-Micro-Service: " + e.getMessage(), e);
//        }
//        try {
//            if(message.getDataType().equalsIgnoreCase("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//               log.info("Inventory Service Receive Kafka Message From Common-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Inventory Service Receive Kafka Error Message From Radius-Micro-Service : "+ e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC},groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
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
//            log.error("Kafka Error Message Receive From TICKET-Micro-Service: " + e.getMessage(), e);
//        }
//        try {
//            if(message.getDataType().equalsIgnoreCase("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//               log.info("Inventory Service Receive Kafka Message From Common-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Inventory Service Receive Kafka Error Message From Ticket-Micro-Service : "+ e.getMessage());
//        }
//    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_SALES_CRM_TOPIC},groupId = KafkaConstant.KAFKA_SALES_CRM_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormSalesMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From SALES_CRM-Micro-Service: " + e.getMessage(), e);
//        }
//        try {
//            if(message.getDataType().equalsIgnoreCase("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//               log.info("Inventory Service Receive Kafka Message From SalesCrm-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Inventory Service Receive Kafka Error Message From SalesCrm-Micro-Service : "+ e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NETCONFIG_TOPIC},groupId = KafkaConstant.KAFKA_NETCONFIG_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNetConfigMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From NETCONFIG-Micro-Service: " + e.getMessage(), e);
//        }
//    }
    @Async
    public void handleerNamee(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INTEGRATION_TOPIC},groupId = KafkaConstant.KAFKA_INTEGRATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormIntegrationMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
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
//            log.error("Kafka Error Message Receive From INTEGRATION-Micro-Service: " + e.getMessage(), e);
//        }
//    }

    @Async
    public void handlerNaamee(KafkaMessageData message) {
        try {
            //SaveCountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCountrySharedDataMessage.class);
            //log.info("Attempting to save country with data: " + dataMessage);
            //countryService.saveCountry(dataMessage);
            log.info("Handled SaveCountrySharedDataMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SaveCountrySharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCafToCustomere(KafkaMessageData message) {
        try {
            CaftoCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CaftoCustomerMessage.class);
            log.debug("Inventory Receive Attempting to update caf to customer with id: " + dataMessage.getCustomerId());
            prepaidInvoiceService.cafToCustomer(dataMessage);
            log.info("Handled receiveMessageCafToCustomere successfully with id: " + dataMessage.getCustomerId());
        } catch (Exception e) {
            log.error("Error handling receiveMessageCafToCustomere: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePlanServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SavePlanAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()),SavePlanAssignmentMessage.class);
            postpaidPlanService.assignPlanToServiceArea(dataMessage.getMappingList());
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }


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
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : "+ e.getMessage());
//        }
//    }

}

