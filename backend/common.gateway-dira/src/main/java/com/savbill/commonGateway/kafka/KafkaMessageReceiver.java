package com.savbill.commonGateway.kafka;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.*;
import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.*;
import com.savbill.commonGateway.Socket.WebSocketService;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.Customers.domain.Customers;
import com.savbill.commonGateway.moules.MasterManagement.LocationMaster.LocationService;
import com.savbill.commonGateway.moules.MasterManagement.PlanService.service.PlanServiceService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.moules.OTP.service.OTPManagmentService;
import com.savbill.commonGateway.moules.PartnerManagement.PartnerService;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.TeamsManagement.TeamHierarchyMapping.TeamHierarchyMappingService;
import com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping.WareHouseTeamsMappingService;
import com.savbill.commonGateway.rabbitmq.CustomMessage;
import com.savbill.commonGateway.rabbitmq.messages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.savbill.commonGateway.rabbitmq.messages.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


@Component
public class KafkaMessageReceiver {

    @Autowired
    private CustomersService customersService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private PlanServiceService planServiceService;

    @Autowired
    private TeamHierarchyMappingService teamHierarchyMappingService;

    @Autowired
    private WareHouseTeamsMappingService wareHouseTeamsMappingService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    LocationService locationService;
    @Autowired
    private OTPManagmentService otpManagmentService;

    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);
    Map<String, Consumer<KafkaMessageData>>  handlerMessage = new HashMap<>();

    public KafkaMessageReceiver(){
        handlerMessage.put("SaveCustomerDataShareMessage",this::saveCustomerData);
        handlerMessage.put("UpdateCustomerShareDataMessage",this::updateCustomerData);
        handlerMessage.put("PlanServiceAreaBindingCheckMessage",this::planServiceAreaBindingCheck);
        handlerMessage.put("CAFCustomerStatusMessage",this::CAFCustomerStatus);
        handlerMessage.put("SaveServicesSharedDataMessage",this::savePlanServices);
        handlerMessage.put("UpdateServicesSharedDataMessage",this::updateServices);
        handlerMessage.put("SaveTeamHierarchyMappingMessage",this::saveTeamHierarchyMapping);
        handlerMessage.put("UpdateTeamHierarchyMappingMessage",this::updateTeamHierarchyMapping);
        handlerMessage.put("SendSocketMessage",this::SendWebShocket);
        handlerMessage.put("MvnoStatusMessage:"+KafkaConstant.UPDATE_STAFF_STATUS,this::updateStaffStatus);
        handlerMessage.put("MvnoStatusMessage:"+KafkaConstant.UPDATE_MVNO_STATUS,this::updateMvnoStatus);
        handlerMessage.put("MvnoStatusMessage:"+KafkaConstant.CUST_DEACTIVATION_WHEN_MVNO_IS_INACTIVE,this::customerDeactivationWhenMvnoIsInActive);
        handlerMessage.put("SaveWarehouseTeamMappingSharedMessage",this::saveWearHouseTeamMapping);
        handlerMessage.put("UpdateWarehouseTeamMappingSharedMessage",this::updateWearHouseTeamMapping);
        handlerMessage.put("OTPProfileMessage:"+KafkaConstant.OPT_PROFILE_SAVE,this::saveOtpProfile);
        handlerMessage.put("OTPProfileMessage:"+KafkaConstant.OPT_PROFILE_UPDATE,this::updateOtpProfile);
        handlerMessage.put("LocationMessage",this::saveLocationMasterEntity);
        handlerMessage.put("UpdatePartnerSharedDataMessage"+KafkaConstant.UPDATE_PARTNER,this::updatePartner);
        handlerMessage.put("SavePartnerSharedDataMessage:"+KafkaConstant.CREATE_PARTNER,this::savePartner);
        handlerMessage.put("CustomMessage:"+ CommonConstants.MVNO_CUST_REF,this::saveMvnoCustRef);


    }

    private void savePartner(KafkaMessageData messageData) {
        try {
            SavePartnerSharedDataMessage message = new Gson().fromJson(new Gson().toJson(messageData.getData()), SavePartnerSharedDataMessage.class);
            partnerService.savePartnerEntiry(message);
            log.info("Common Service Receive Kafka Message From Partner-Micro-Service  : " + message);
        }catch (Exception e){
            log.error("Kafka Error Message Receive From Partner-Micro-Service : " + e.getMessage());

        }
    }
    private void updatePartner(KafkaMessageData messageData) {
        try {
            UpdatePartnerSharedDataMessage message = new Gson().fromJson(new Gson().toJson(messageData.getData()),UpdatePartnerSharedDataMessage.class);
            partnerService.updatePartnerEntiry(message);
            log.info("Common Service Receive Kafka Message From Partner-Micro-Service  : " + message);
        } catch (Exception e){
            log.error("Kafka Error Message Receive From Partner-Micro-Service : " + e.getMessage());
        }
    }
    private void saveLocationMasterEntity(KafkaMessageData messageData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(messageData.getData());
            LocationMessage locationMessage = objectMapper.readValue(jsonData, LocationMessage.class);
            locationService.saveLocationMasterEntity(locationMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + messageData);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateOtpProfile(KafkaMessageData messageData) {
        try {
            OTPProfileMessage message = new Gson().fromJson(new Gson().toJson(messageData.getData()), OTPProfileMessage.class);
            otpManagmentService.updateOTPProfile(message);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void saveOtpProfile(KafkaMessageData messageData) {
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            OTPProfileMessage dataMessage=objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), OTPProfileMessage.class);
            otpManagmentService.saveOTPProfile(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateWearHouseTeamMapping(KafkaMessageData message) {
        try {
            UpdateWarehouseTeamMappingSharedMessage updateWarehouseTeamMappingSharedMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateWarehouseTeamMappingSharedMessage.class);
            wareHouseTeamsMappingService.updateWarehouseTeamMapping(updateWarehouseTeamMappingSharedMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void saveWearHouseTeamMapping(KafkaMessageData message) {
        try {
            SaveWarehouseTeamMappingSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveWarehouseTeamMappingSharedMessage.class);
            wareHouseTeamsMappingService.saveWarehouseTeamMapping(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void savePlanServices(KafkaMessageData message) {
        try {
            SaveServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServicesSharedDataMessage.class);
            planServiceService.savePlanServiceEntity(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateServices(KafkaMessageData message) {
        try {
            UpdateServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServicesSharedDataMessage.class);
            planServiceService.updatePlanServiceEntity(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateCustomerData(KafkaMessageData message) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            customersService.updateCustomers(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void planServiceAreaBindingCheck(KafkaMessageData message) {
        try {
            PlanServiceAreaBindingCheckMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), PlanServiceAreaBindingCheckMessage.class);
            serviceAreaService.updateServiceAreaFlafForPlanBinding(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void CAFCustomerStatus(KafkaMessageData message) {
        try {
            CAFCustomerStatusMessage cafCustomerStatusMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CAFCustomerStatusMessage.class);
            customersService.saveCafToCustomer(cafCustomerStatusMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void saveTeamHierarchyMapping(KafkaMessageData message) {
        try {
            SaveTeamHierarchyMappingMessage saveTeamHierarchyMappingMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTeamHierarchyMappingMessage.class);
            teamHierarchyMappingService.saveTeamHierarchyMapping(saveTeamHierarchyMappingMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateTeamHierarchyMapping(KafkaMessageData message) {
        try {
            UpdateTeamHierarchyMappingMessage updateTeamHierarchyMappingMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTeamHierarchyMappingMessage.class);
            teamHierarchyMappingService.updateTeamHierarchyMapping(updateTeamHierarchyMappingMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateStaffStatus(KafkaMessageData message) {
        try {
            MvnoStatusMessage mvnoStatusMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoStatusMessage.class);
            staffUserService.UpdateStaffStatus(mvnoStatusMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void updateMvnoStatus(KafkaMessageData message) {
        try {
            MvnoStatusMessage mvnoStatusMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoStatusMessage.class);
            mvnoService.UpdateMvnoStatus(mvnoStatusMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void customerDeactivationWhenMvnoIsInActive(KafkaMessageData message) {
        try {
            MvnoStatusMessage mvnoStatusMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoStatusMessage.class);
            customersService.customerDeactivationWhenMvnoIsInActive(mvnoStatusMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void SendWebShocket(KafkaMessageData message) {
        try {
            SendSocketMessage sendSocketMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendSocketMessage.class);
            System.out.println("enter in common reciever : with message  "+sendSocketMessage);
            webSocketService.sendMessage(sendSocketMessage.getUrl(), sendSocketMessage.getObject());
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    private void saveCustomerData(KafkaMessageData message) {
        try {
            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCustomerDataShareMessage.class);
            Customers customers = customersService.saveCustomers(dataMessage);
            if (customers != null && dataMessage.getRefMvno() != null)
                mvnoService.updateMvnoRefForInvoice(Long.valueOf(dataMessage.getRefMvno()), customers.getId());
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }
    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC},groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormCustomerMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC},groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormPartnerMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Partner-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC},groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC},groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Revenue-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC},groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID,containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Notification-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC},groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID,  containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Radius-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC},groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Ticket-Micro-Service : "+ e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.KAFKA_SALES_CRM_TOPIC},groupId = KafkaConstant.KAFKA_SALESCRM_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormSalseCRMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);
            if (handler == null) {
                handler = handlerMessage.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Ticket-Micro-Service : "+ e.getMessage());
        }
    }

/*    SEND_CUSTOMER_CREATE_AND_UPDATE_DATA topic listener commented to stop receiving messages from cms */
    @Transactional
    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT,autoCreateTopics = "false")
    @KafkaListener(topics = {KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA},groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageForCustomerData(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;

            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);

            if (handler == null) {
//                If not found, try to find the handler with only dataType
                handler = handlerMessage.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : "+ e.getMessage());
        }
    }

    private void saveMvnoCustRef(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;

            Consumer<KafkaMessageData> handler = handlerMessage.get(keyWithEventType);

            if (handler == null) {
//                If not found, try to find the handler with only dataType
                handler = handlerMessage.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == handlerMessage.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
            CustomMessage messageData = new Gson().fromJson(new Gson().toJson(message.getData()), CustomMessage.class);
            mvnoService.saveMvnoCustRef(messageData);
            log.info("Common Service Receive Kafka Message From Partner-Micro-Service  : " + message);
        }
        finally {
            CustomMessage messageData = new Gson().fromJson(new Gson().toJson(message.getData()), CustomMessage.class);
            mvnoService.saveMvnoCustRef(messageData);
            log.info("Mvno created successfully");
        }
    }
}
