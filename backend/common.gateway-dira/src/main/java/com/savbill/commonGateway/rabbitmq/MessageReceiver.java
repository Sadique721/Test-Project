package com.savbill.commonGateway.rabbitmq;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages.*;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
    /* private static Log log = LogFactory.getLog(MessageReceiver.class);

    @Autowired
    CustomersService customersService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    PlanServiceService planServiceService;
    @Autowired
    TeamHierarchyMappingService teamHierarchyMappingService;
    @Autowired
    WareHouseTeamsMappingService wareHouseTeamsMappingService;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    MvnoService mvnoService;

    @Autowired
    LocationService locationService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OTPManagmentService otpManagmentService;



    @RabbitListener(queues = RabbitMqConstants.TEST_RECEIVE)
    public void receiveMessageCustomerApigw(CustomMessage message) {
        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            System.out.println("success..!!");
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed :"+e.getMessage());
        }
  
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON)
    public void receiveMessageForCustomersCreate(SaveCustomerDataShareMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            Customers customers = customersService.saveCustomers(message);
            if(customers != null && message.getRefMvno() != null) {
                mvnoService.updateMvnoRefForInvoice(Long.valueOf(message.getRefMvno()), customers.getId());
            }
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Customer Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_COMMON)
    public void receiveMessageForCustomersUpdate(UpdateCustomerShareDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            customersService.updateCustomers(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }
    }
    @Transactional
    //Create Partner from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_API_COMMON)
    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            partnerService.savePartnerEntiry(message);
            log.info("Partner Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreatePartner Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Partner from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON)
    public void receiveMessageUpdatePartner(UpdatePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            partnerService.updatePartnerEntiry(message);
            log.info("Partner Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdatePartner Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK)
    public void receiveMessagePlanServiceAreaBindCheck(PlanServiceAreaBindingCheckMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.updateServiceAreaFlafForPlanBinding(message);
            log.info("Service area flag fro is binding updated successfully");
        } catch (Exception e) {
            log.error("Service area flag fro is binding updated Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(queues = RabbitMqConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_COMMONGATEWAY)
    public void receiveMessageCafToCustomer(CAFCustomerStatusMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            customersService.saveCafToCustomer(message);
            log.info("Convert Caf To Customer Successfully From RabbitMq Message");
        } catch (Exception e) {
            log.error("receiveMessageCafToCustomer Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //Create Services from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_COMMON)
    public void receiveMessageCreateServices(SaveServicesSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            planServiceService.savePlanServiceEntity(message);
            log.info("Services Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateServices Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Services from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON)
    public void receiveMessageUpdateServices(UpdateServicesSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            planServiceService.updatePlanServiceEntity(message);
            log.info("Services Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateServices Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Team Hierarchy from RabbitMQ
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAM_HIERARCHY_CREATE_DATA_SHARE_COMMONGATEWAY)
    public void receiveMessageCreateTeamHierarchy(SaveTeamHierarchyMappingMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamHierarchyMappingService.saveTeamHierarchyMapping(message);
            log.info("Hierarchy Created Successfully From RabbitMq Message");
        } catch (Exception e) {
            log.error("receiveMessageCreateHierarchy Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Team Hierarchy from RabbitMQ
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAM_HIERARCHY_UPDATE_DATA_SHARE_COMMONGATEWAY)
    public void receiveMessageUpdateTeamHierarchy(UpdateTeamHierarchyMappingMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamHierarchyMappingService.updateTeamHierarchyMapping(message);
            log.info("Hierarchy Updated Successfully From RabbitMq Message");
        } catch (Exception e) {
            log.error("receiveMessageUpdateHierarchy Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create WarehouseTeamMapping from RabbitMQ
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONGATEWAY)
    public void receiveMessageCreateWarehouseTeamMapping(SaveWarehouseTeamMappingSharedMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            wareHouseTeamsMappingService.saveWarehouseTeamMapping(message);
            log.info("Hierarchy Created Successfully From RabbitMq Message");
        } catch (Exception e) {
            log.error("receiveMessageCreateHierarchy Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update WarehouseTeamMapping from RabbitMQ
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONGATEWAY)
    public void receiveMessageUpdateWarehouseTeamMapping(UpdateWarehouseTeamMappingSharedMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            wareHouseTeamsMappingService.updateWarehouseTeamMapping(message);
            log.info("Hierarchy Updated Successfully From RabbitMq Message");
        } catch (Exception e) {
            log.error("receiveMessageUpdateHierarchy Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON)
    public void receiveSocketMessage(SendSocketMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            webSocketService.sendMessage(message.getUrl(),  message.getObject());
            log.info("Socket send message to gui successfully");
        } catch (Exception e) {
            log.error("SendSocketMessage Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_STAFF_STATUS_DUNNING_MESSAGE)
    public void receiveMvnoStaffStatusMessage(MvnoStatusMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            staffUserService.UpdateStaffStatus(message);
            log.info("Socket send message to gui successfully");
        } catch (Exception e) {
            log.error("SendSocketMessage Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE)
    public void receiveMvnoStatusMessage(MvnoStatusMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            mvnoService.UpdateMvnoStatus(message);
            log.info("Socket send message to gui successfully");
        } catch (Exception e) {
            log.error("SendSocketMessage Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_STATUS_DUNNING_MESSAGE)
    public void receiveMessageCustomerDeactiovationWhenMvnoIsInActive(MvnoStatusMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            customersService.customerDeactivationWhenMvnoIsInActive(message);
            log.info("Defoult Deprovision Successfull");
        } catch (Exception e) {
            log.error("receiveMessage defoult update Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    } */
}
