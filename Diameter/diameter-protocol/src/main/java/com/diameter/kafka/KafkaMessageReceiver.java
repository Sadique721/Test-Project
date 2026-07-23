package com.diameter.kafka;

import com.diameter.dto.*;
import com.diameter.model.QOSPolicy;
import com.diameter.model.TestKafkaDto;
import com.diameter.service.*;
import com.diameter.serviceImpl.QOSPolicyServiceImpl;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Component
public class KafkaMessageReceiver implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();

    @Autowired
    private TestKafkaService testKafkaService;

    @Autowired
    private PulseManagementService ocsPulseManagementService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private QOSPolicyServiceImpl qosPolicyService;

    @Autowired
    private ZoneService ocsZoneService;
    @Autowired
    private RatePackageService ocsRatePackageService;

    @Autowired
    private RatePackageGroupService ocsRatePackageGroupService;


    @Autowired
    private RatePackageGroupMappingService ocsRatePackageGroupMappingService;


    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Autowired
    KafkaConsumerConfig consumerConfig;

    @Autowired
    private Gson gson;

    public KafkaMessageReceiver() {
        //Receive From Common
        messageHandlers.put("TestKafkaDto", this::handleTestKafka);
        messageHandlers.put("SaveCustomerDataShareMessage", this::handleSaveCustomerDataShareMessage);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleUpdateCustomerShareDataMessage);
        messageHandlers.put("SavePlanSharedDataMessage", this::handleSavePlanSharedDataMessage);
        messageHandlers.put("UpdatePlanSharedDataMessage", this::handleUpdatePlanSharedDataMessage);
        messageHandlers.put("QosPolicyMessage", this::handleSaveCustQosPolicy);
        // ocs
        messageHandlers.put("ZoneDTOMessage:"+KafkaConstant.ZONE_SAVE, this::handleSaveZoneMessage);
        messageHandlers.put("ZoneDTOMessage:"+KafkaConstant.ZONE_UPDATE, this::handleUpdateZoneMessage);
        messageHandlers.put("ZoneDTOMessage:"+KafkaConstant.ZONE_DELETE, this::handleDeleteZoneMessage);

        messageHandlers.put("PulseManagementDTOMessage:"+KafkaConstant.PULSE_SAVE, this::handleSavePulseMessage);
        messageHandlers.put("PulseManagementDTOMessage:"+KafkaConstant.PULSE_UPDATE, this::handleUpdatePulseMessage);
        messageHandlers.put("PulseManagementDTOMessage:"+KafkaConstant.PULSE_DELETE, this::handleDeletePulseMessage);

        messageHandlers.put("RatePackageDTOMessage:"+KafkaConstant.RATE_PACKAGE_SAVE, this::handleSaveRatePackageMessage);
        messageHandlers.put("RatePackageDTOMessage:"+KafkaConstant.RATE_PACKAGE_UPDATE, this::handleUpdateRatePackageMessage);
        messageHandlers.put("RatePackageDTOMessage:"+KafkaConstant.RATE_PACKAGE_DELETE, this::handleDeleteRatePackageMessage);

        messageHandlers.put("RatePackageGroupDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_SAVE, this::handleSaveRatePackageGroupMessage);
        messageHandlers.put("RatePackageGroupDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_UPDATE, this::handleUpdateRatePackageGroupMessage);
        messageHandlers.put("RatePackageGroupDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_DELETE, this::handleDeleteRatePackageGroupMessage);

        messageHandlers.put("RatePackageGroupMappingDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_MAPPING_SAVE,   this::handleSaveRatePackageGroupMappingMessage);
        messageHandlers.put("RatePackageGroupMappingDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_MAPPING_UPDATE, this::handleUpdateRatePackageGroupMappingMessage);
        messageHandlers.put("RatePackageGroupMappingDTOMessage:"+KafkaConstant.RATE_PACKAGE_GROUP_MAPPING_DELETE, this::handleDeleteRatePackageGroupMappingMessage);
        //cpm
        messageHandlers.put("CustomerServiceActivationMessage", this::handleUpdateServiceStatus);
        messageHandlers.put("CustomerUpdateMessage", this::handleCustomerStatusChangeMessage);
        messageHandlers.put("CustPackageRelMessage", this::handleCustomerPackagerelChangeMessage);
        messageHandlers.put("CustomerPackageRelMessage", this::handleCustomerPackagerelChangeMessage);
        messageHandlers.put("ChangePlanMessage", this::saveChangePlanHandler);
        messageHandlers.put("DiameterChangePlanSyncMessage", this::saveDiameterChangePlanSyncHandler);
        //Replace Inventory Update Imsi
        messageHandlers.put("UpdateCustomerServiceMappingImsiMessage", this::handleUpdateCustomerServiceMappingImsi);


    }


//    @Transactional
//    @Override
//    public void run() {
//        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
//        deserializer.setRemoveTypeHeaders(false);
//        deserializer.addTrustedPackages("*");
//        deserializer.setUseTypeMapperForKey(false);
//
//        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
//                new DefaultKafkaConsumerFactory<>(consumerConfig.packetDataPropsPrimary(),
//                        new StringDeserializer(), deserializer);
//
//        try (KafkaConsumer<String, KafkaMessageData> primaryConsumer = (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {
//            primaryConsumer.subscribe(Arrays.asList(
//                    KafkaConstant.KAFKA_CMS_TOPIC
////                    KafkaConstant.KAFKA_PMS_TOPIC
//            ));
//
//            while (true) {
//                long startTime = System.currentTimeMillis();
//
//                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
//                log.info("Kafka records received::::::::::::::: " + records.count());
//
//                for (ConsumerRecord<String, KafkaMessageData> record : records) {
//                    try {
//                        KafkaMessageData message = record.value();
//                        String dataType = message.getDataType();
//                        String eventType = message.getEventType();
//
//                        String keyWithEventType = dataType + ":" + eventType;
//
//                        Consumer<KafkaMessageData> handler = messageHandlers.getOrDefault(keyWithEventType, messageHandlers.get(dataType));
//                        if (handler != null) {
//                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
//                            CompletableFuture.runAsync(() -> handler.accept(message), executor);
//                        }
//
//                        log.debug("Received message: dataType = " + dataType + ", eventType = " + eventType);
//                    } catch (Exception e) {
//                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
//                    }
//                }
//
//                try {
//                    primaryConsumer.commitSync(); // safer manual commit
//                } catch (Exception e) {
//                    log.error("Commit failed", e);
//                }
//
//                long endTime = System.currentTimeMillis();
//                log.debug("Poll processing time (ms): " + (endTime - startTime));
//            }
//        } catch (Exception e) {
//            log.error("Kafka Error: " + e.getMessage(), e);
//        }
//    }

    @Transactional
    @Override
    public void run() {

        JsonDeserializer<KafkaMessageData> deserializer =
                new JsonDeserializer<>(KafkaMessageData.class, objectMapper, false);

        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(false);

        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
                new DefaultKafkaConsumerFactory<>(
                        consumerConfig.packetDataPropsPrimary(),
                        new StringDeserializer(),
                        deserializer
                );

        try (KafkaConsumer<String, KafkaMessageData> consumer =
                     (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {

            consumer.subscribe(Arrays.asList(
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA,
                    KafkaConstant.KAFKA_RADIUSA_TOPIC,
                    KafkaConstant.UPDATE_SERVICE_STATUS_DIAMETER,
                    KafkaConstant.KAFKA_CMS_CHANGE_PLAN_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC
            ));

            while (true) {

                ConsumerRecords<String, KafkaMessageData> records =
                        consumer.poll(Duration.ofMillis(5000));

                log.info("Kafka records received: {}", records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {

                    try {
                        KafkaMessageData message = record.value();

                        if (message == null) {
                            log.warn("Received null Kafka message at offset {}", record.offset());
                            continue;
                        }

                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        log.debug("Received message -> dataType: {}, eventType: {}",
                                dataType, eventType);

                        if (dataType == null) {
                            log.warn("Skipping message because dataType is null. Offset: {}",
                                    record.offset());
                            continue;
                        }

                        // Build composite key
                        String keyWithEventType = dataType + ":" + eventType;

                        // Resolve handler safely
                        Consumer<KafkaMessageData> resolvedHandler =
                                messageHandlers.get(keyWithEventType);

                        if (resolvedHandler == null) {
                            resolvedHandler = messageHandlers.get(dataType);
                        }

                        if (resolvedHandler != null) {
                            final Consumer<KafkaMessageData> handlerToUse = resolvedHandler;
                            final KafkaMessageData messageToProcess = message;

                            CompletableFuture.runAsync(
                                    () -> handlerToUse.accept(messageToProcess),
                                    executor
                            );
                        } else {
                            log.warn("No handler found for key: {} or dataType: {}",
                                    keyWithEventType, dataType);
                        }

                    } catch (Exception ex) {
                        log.error("Error processing record at offset {} partition {}",
                                record.offset(),
                                record.partition(),
                                ex);
                    }
                }

                try {
                    consumer.commitSync();
                } catch (Exception commitEx) {
                    log.error("Kafka commit failed", commitEx);
                }
            }

        } catch (Exception e) {
            log.error("Kafka consumer fatal error", e);
        }
    }

    @Async
    public void handleTestKafka(KafkaMessageData message) {

        try {
            log.debug("Inside Test kafka message");
            TestKafkaDto dataMessage =
                    new Gson().fromJson(
                            new Gson().toJson(message.getData()),
                            TestKafkaDto.class
                    );

            testKafkaService.saveTestKafka(dataMessage);

            log.info("Handled SaveTestKafkaMessage successfully: {}", message);

        } catch (Exception e) {
            log.error("Error handling SaveTestKafkaMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveCustomerDataShareMessage(KafkaMessageData message) {
        try {
//            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCustomerDataShareMessage.class);
            SaveCustomerDataShareMessage dataMessage =
                    objectMapper.convertValue(
                            message.getData(),
                            SaveCustomerDataShareMessage.class
                    );
            log.debug("Diameter Receive Attempting to save customer with name: {}", dataMessage.getUsername());
            customerService.saveCustomers(dataMessage);
            log.info("Diameter Service Receive Kafka Message From CMS-Service with name: {}", dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling SaveCustomerDataShareMessage: {}", e.getMessage(), e);
        }
    }
//    @Async
    public void handleUpdateCustomerShareDataMessage(KafkaMessageData message) {
        try {
//            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            UpdateCustomerShareDataMessage dataMessage = objectMapper.convertValue(
                    message.getData(),
                    UpdateCustomerShareDataMessage.class
            );
            log.debug("Diameter Receive Attempting to update customer with name: " + dataMessage.getUsername());
            customerService.updateCustomers(dataMessage);
            log.info("Diameter Service Receive Kafka Message From CMS-Service with name: " + dataMessage.getUsername());
        } catch (Exception e) {
            log.error("Error handling UpdateCustomerShareDataMessage: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleSavePlanSharedDataMessage(KafkaMessageData message) {

        try {

            if (message == null || message.getData() == null) {
                log.warn("Received null SavePlanSharedDataMessage from Kafka");
                return;
            }

            // Deserialize safely using injected Gson bean
            SavePlanSharedDataMessage dataMessage =
                    gson.fromJson(
                            gson.toJson(message.getData()),
                            SavePlanSharedDataMessage.class
                    );

            if (dataMessage == null) {
                log.warn("Deserialized SavePlanSharedDataMessage is null");
                return;
            }

            log.debug("Diameter Receive Attempting to save plan with name: {}",
                    dataMessage.getName());

            postpaidPlanService.savePostpaidPlan(dataMessage);

            log.info("Diameter Service Successfully processed plan from CMS-Service with name: {}",
                    dataMessage.getName());

        } catch (Exception e) {

            log.error("Error handling SavePlanSharedDataMessage for plan name {} : {}",
                    message != null ? message.getData() : "UNKNOWN",
                    e.getMessage(),
                    e);
        }
    }

    @Async
    public void handleUpdatePlanSharedDataMessage(KafkaMessageData message) {

        try {

            if (message == null || message.getData() == null) {
                log.warn("Received null UpdatePlanSharedDataMessage from Kafka");
                return;
            }

            // Deserialize safely using injected Gson bean
            UpdatePlanSharedDataMessage dataMessage =
                    gson.fromJson(
                            gson.toJson(message.getData()),
                            UpdatePlanSharedDataMessage.class
                    );

            if (dataMessage == null) {
                log.warn("Deserialized UpdatePlanSharedDataMessage is null");
                return;
            }

            log.debug("Diameter Receive Attempting to save plan with name: {}",
                    dataMessage.getName());

            postpaidPlanService.updatePostpaidPlan(dataMessage);

            log.info("Diameter Service Successfully processed plan from CMS-Service with name: {}",
                    dataMessage.getName());

        } catch (Exception e) {

            log.error("Error handling UpdatePlanSharedDataMessage for plan name {} : {}",
                    message != null ? message.getData() : "UNKNOWN",
                    e.getMessage(),
                    e);
        }
    }

    @Async
    public void handleSaveCustQosPolicy(KafkaMessageData message) {

        try {

            CustomMessage dataMessage =
                    objectMapper.convertValue(message.getData(), CustomMessage.class);

            QOSPolicy policy = new QOSPolicy(dataMessage);

            qosPolicyService.createOrUpdateKafka(policy);

            log.info("Successfully processed QOS Policy Kafka message. Id={}",
                    policy.getId());

        } catch (Exception e) {
            log.error("Failed to process QOS Policy Kafka message. Raw={}",
                    message, e);
        }
    }

    @Async
    public void handleSaveZoneMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            ZoneDTOMessage dataMessage = mapper.convertValue(message.getData(), ZoneDTOMessage.class);
            ocsZoneService.saveZone(dataMessage);
            log.info("[OCS] Zone message processed | Operation: {} | Zone: {}",
                    "ZONE_SAVE", dataMessage.getZoneName());
        } catch (Exception e) {
            log.error("[OCS] Error handling ZoneDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateZoneMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            ZoneDTOMessage dataMessage = mapper.convertValue(message.getData(), ZoneDTOMessage.class);
            ocsZoneService.updateZone(dataMessage);
            log.info("[OCS] Zone message processed | Operation: {} | Zone: {}","ZONE_UPDATE", dataMessage.getZoneName());

        }catch(Exception e){
            log.error("[OCS] Error handling ZoneDTOMessage: {}", e.getMessage(), e);

        }
    }

    @Async
    public void handleDeleteZoneMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            ZoneDTOMessage dataMessage = mapper.convertValue(message.getData(), ZoneDTOMessage.class);
            ocsZoneService.deleteZone(dataMessage);

            log.info("[OCS] Zone message processed | Operation: {} | Zone: {}","ZONE_Delete", dataMessage.getZoneName());

        }catch(Exception e){
            log.error("[OCS] Error handling ZoneDTOMessage: {}", e.getMessage(), e);

        }
    }


    @Async
    public void handleSavePulseMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            PulseManagementDTOMessage dataMessage = mapper.convertValue(message.getData(), PulseManagementDTOMessage.class);
            ocsPulseManagementService.savePulse(dataMessage);
            log.info("[OCS] Pulse message processed | Operation: {} | Pulse: {}", "PULSE_SAVE", dataMessage.getPulseName());
        } catch (Exception e) {
            log.error("[OCS] Error handling PulseManagementDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdatePulseMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            PulseManagementDTOMessage dataMessage = mapper.convertValue(message.getData(), PulseManagementDTOMessage.class);
            ocsPulseManagementService.updatePulse(dataMessage);
            log.info("[OCS] Pulse message processed | Operation: {} | Pulse: {}", "PULSE_UPDATE", dataMessage.getPulseName());
        } catch (Exception e) {
            log.error("[OCS] Error handling PulseManagementDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleDeletePulseMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            PulseManagementDTOMessage dataMessage = mapper.convertValue(message.getData(), PulseManagementDTOMessage.class);
            ocsPulseManagementService.deletePulse(dataMessage);
            log.info("[OCS] Pulse message processed | Operation: {} | Pulse: {}", "PULSE_DELETE", dataMessage.getPulseName());
        } catch (Exception e) {
            log.error("[OCS] Error handling PulseManagementDTOMessage: {}", e.getMessage(), e);
        }
    }


    @Async
    public void handleSaveRatePackageMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageDTOMessage.class);
            ocsRatePackageService.savePackage(dataMessage);
            log.info("[OCS] RatePackage message processed | Operation: {} | Package: {}", "RATE_PACKAGE_SAVE", dataMessage.getPackageName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateRatePackageMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageDTOMessage.class);
            ocsRatePackageService.updatePackage(dataMessage);
            log.info("[OCS] RatePackage message processed | Operation: {} | Package: {}", "RATE_PACKAGE_UPDATE", dataMessage.getPackageName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteRatePackageMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageDTOMessage.class);
            ocsRatePackageService.deletePackage(dataMessage);
            log.info("[OCS] RatePackage message processed | Operation: {} | Package: {}", "RATE_PACKAGE_DELETE", dataMessage.getPackageName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveRatePackageGroupMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupDTOMessage.class);
            ocsRatePackageGroupService.saveGroup(dataMessage);
            log.info("[OCS] RatePackageGroup message processed | Operation: {} | Group: {}", "RATE_PACKAGE_GROUP_SAVE", dataMessage.getGroupName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateRatePackageGroupMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupDTOMessage.class);
            ocsRatePackageGroupService.updateGroup(dataMessage);
            log.info("[OCS] RatePackageGroup message processed | Operation: {} | Group: {}", "RATE_PACKAGE_GROUP_UPDATE", dataMessage.getGroupName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteRatePackageGroupMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupDTOMessage.class);
            ocsRatePackageGroupService.deleteGroup(dataMessage);
            log.info("[OCS] RatePackageGroup message processed | Operation: {} | Group: {}", "RATE_PACKAGE_GROUP_DELETE", dataMessage.getGroupName());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupDTOMessage: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveRatePackageGroupMappingMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupMappingDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupMappingDTOMessage.class);
            ocsRatePackageGroupMappingService.saveMappings(dataMessage);
            log.info("[OCS] RatePackageGroupMapping message processed | Operation: RATE_PACKAGE_GROUP_MAPPING_SAVE | GroupId: {}", dataMessage.getGroupId());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupMappingDTOMessage SAVE: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateRatePackageGroupMappingMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupMappingDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupMappingDTOMessage.class);
            ocsRatePackageGroupMappingService.updateMappings(dataMessage);
            log.info("[OCS] RatePackageGroupMapping message processed | Operation: RATE_PACKAGE_GROUP_MAPPING_UPDATE | GroupId: {}", dataMessage.getGroupId());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupMappingDTOMessage UPDATE: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteRatePackageGroupMappingMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            RatePackageGroupMappingDTOMessage dataMessage = mapper.convertValue(message.getData(), RatePackageGroupMappingDTOMessage.class);
            ocsRatePackageGroupMappingService.deleteMappings(dataMessage);
            log.info("[OCS] RatePackageGroupMapping message processed | Operation: RATE_PACKAGE_GROUP_MAPPING_DELETE | GroupId: {}", dataMessage.getGroupId());
        } catch (Exception e) {
            log.error("[OCS] Error handling RatePackageGroupMappingDTOMessage DELETE: {}", e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateServiceStatus(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            CustomerServiceActivationMessage dataMessage = mapper.convertValue(message.getData(), CustomerServiceActivationMessage.class);
            customerService.updateServiceStatus(dataMessage);
            log.info("Diameter Service Receive Kafka Message From CMS-Service with id: " + dataMessage.getCustId() + " and status: " + dataMessage.getStatus());
        } catch (Exception e) {
            log.error("Error handling CustomerServiceActivationMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerStatusChangeMessage(KafkaMessageData message) {
        log.info("Received Message From Kafka receiverMessage : <" + message + ">");
        try {
            CustomerUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerUpdateMessage.class);
            customerService.updateCustomerStatus(dataMessage);

        } catch (Exception e) {
            log.error("receiveMessageUpdateCustomerStatus Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Async
    public void handleCustomerPackagerelChangeMessage(KafkaMessageData message) {
        log.info("Received Message From Kafka receiverMessage : <" + message + ">");
        try {
            CustomerPackageRelMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerPackageRelMessage.class);
            customerService.updateCustomerPackageRel(dataMessage);
        } catch (Exception e) {
            log.error("receiveMessageUpdateCustomerStatus Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Async
    private void saveChangePlanHandler(KafkaMessageData messageData) {
        try {
            ChangePlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(messageData.getData()), ChangePlanMessage.class);
            customerService.saveCustomersPlanAndServiceData(dataMessage);
            log.info("Diameter Service Receive Kafka Message From CMS-Micro-Service change/renew plan" + dataMessage);
        } catch (Exception e) {
            log.error("Diameter Service Receive Kafka Error Message From CMS-Micro-Service", e);
        }
    }

    @Async
    private void saveDiameterChangePlanSyncHandler(KafkaMessageData messageData) {
        try {
            Gson gson = new Gson();
            Map<String, Object> syncData = gson.fromJson(gson.toJson(messageData.getData()), Map.class);
            ChangePlanMessage dataMessage = gson.fromJson(gson.toJson(messageData.getData()), ChangePlanMessage.class);
            customerService.saveDiameterChangePlanSyncData(dataMessage, syncData);
            log.info("Diameter Service Receive Kafka Message From CMS-Micro-Service diameter sync" + dataMessage);
        } catch (Exception e) {
            log.error("Diameter Service Receive Kafka Error Message From CMS-Micro-Service diameter sync", e);
        }
    }
    @Async
    public void handleUpdateCustomerServiceMappingImsi(
            KafkaMessageData message) {

        try {

            UpdateCustomerServiceMappingImsiMessage dataMessage =
                    new Gson().fromJson(
                            new Gson().toJson(message.getData()),
                            UpdateCustomerServiceMappingImsiMessage.class
                    );

            customerService.updateCustomerServiceMappingImsi(
                    dataMessage.getCustomerId(),
                    dataMessage.getMsisdn(),
                    dataMessage.getImsi()
            );

            log.info(
                    "Handled UpdateCustomerServiceMappingImsiMessage successfully. customerId={}, msisdn={}",
                    dataMessage.getCustomerId(),
                    dataMessage.getMsisdn()
            );

        } catch (Exception e) {

            log.error(
                    "Error handling UpdateCustomerServiceMappingImsiMessage",
                    e
            );
        }
    }

}
