//package com.savbill.integrationsystem.rabbitmq;
//
//import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
//import com.savbill.integrationsystem.Case.CaseRepo;
//import com.savbill.integrationsystem.Case.CaseService;
//import com.savbill.integrationsystem.CustomerInventoryMapping.CustomerInventoryService;
//import com.savbill.integrationsystem.CustomerPackage.entity.CustomerPackageService;
//import com.savbill.integrationsystem.CustomerServiceMapping.CustomerServiceMappingService;
//import com.savbill.integrationsystem.InventoryItem.*;
//import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
//import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
//import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
//import com.savbill.integrationsystem.PostpaidPlan.PostpaidPlanServiceImpl;
//import com.savbill.integrationsystem.Services.ServicesService;
//import com.savbill.integrationsystem.billgen.entity.SaveBranchSharedDataMessage;
//import com.savbill.integrationsystem.billgen.service.*;
//import com.savbill.integrationsystem.nms.NmsService;
//import com.savbill.integrationsystem.nms.entity.UuidDataDTO;
//import com.savbill.integrationsystem.pojo.NMSServiceActivationDTO;
//import com.savbill.integrationsystem.rms.model.InwardDto;
//import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
//import com.savbill.integrationsystem.rms.model.WareHouseDto;
//import com.savbill.integrationsystem.rms.service.InwardService;
//import com.savbill.integrationsystem.rms.service.ProductCategoryService;
//import com.savbill.integrationsystem.rms.service.WareHouseService;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.log4j.MDC;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageReceiver {
//    private static Log log = LogFactory.getLog(MessageReceiver.class);
//
//    @Autowired
//    BillGenService billGenService;
//    @Autowired
//    ChargeServices chargeServices;
//    @Autowired
//    PlanGroupService planGroupService;
//    @Autowired
//    CustomerService customerService;
//    @Autowired
//    CreditdocService creditdocService;
//    @Autowired
//    TaxService taxService;
//
//    @Autowired
//    ServiceAreaInService serviceAreaInService;
//    @Autowired
//    BusinessUnitService businessUnitService;
//    @Autowired
//    DebitDocumentService debitDocumentService;
//    @Autowired
//    StaffUserService staffUserService;
//    @Autowired
//    BranchService branchService;
//
//    @Autowired
//    WareHouseService wareHouseService;
//
//    @Autowired
//    ApproveInventoryItemService approveInventoryItemService;
//
//    @Autowired
//    ApproveRemoveInventoryItemService inventoryRemoveItemService;
//    @Autowired
//    InventoryItemService inventoryItemService;
//
//    @Autowired
//    private CustomerPackageService customerPackageService;
//
//    @Autowired
//    private CustomerInventoryService customerInventoryService;
//
//    @Autowired
//    private CustomerServiceMappingService customerServiceMappingService;
//
//    @Autowired
//    private ServicesService servicesService;
//
//    @Autowired
//    private PostpaidPlanServiceImpl postpaidPlanService;
//    @Autowired
//    private IntentoryItemRepo intentoryItemRepo;
//    @Autowired
//    private CaseService caseService;
//
//    @Autowired
//    private CaseRepo caseRepo;
//
//    @Autowired
//    private ProductCategoryService productCategoryService;
//
//    @Autowired
//    private InwardService inwardService;
//
//    @Autowired
//    private NmsService nmsService;
//
//    @Autowired
//    private PaymentConfigService paymentConfigService;
//
//
//    @Autowired
//    CustomerPaymentService customerPaymentService;
//
//    @Autowired
//    CustomerPaymentRepository customerPaymentRepository;
//
//
//    //    @RabbitListener(queues = RabbitMqConstants.TEST_RECEIVE)
////    public void receiveMessageCustomerApigw(CustomMessage message) {
////        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////            System.out.println("success..!!");
////        }
////        catch(Exception e) {
////            log.info("receiveMessageCustomerApigw Failed :"+e.getMessage());
////        }
////
////    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN)
//    public void receiveMessageBillGenAPIGW(CreditNoteMessageIntegrationSystem message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            creditdocService.saveCreditNoteGenData(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHARGE_MGMTN_SUCCESS)
//    public void receiveMessageChargeFromAPIGW(ChargeMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            chargeServices.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_SERVICE_SUCCESS)
//    public void receiveMessagePlanServiceFromAPIGW(PlanServiceMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            planGroupService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_TAX_MGMTN_SUCCESS)
//    public void receiveMessageTaxFromAPIGW(TaxMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            taxService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_SUCCESS)
////    public void receiveMessageCustomersServiceFromAPIGW(CustomerMessage message) {
////        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////
////            cusomersService.save(message);
////            System.out.println("success..!!");
////        } catch (Exception e) {
////            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
////        }
////
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_SUCCESS)
//    public void receiveMessageServiceAreaFromAPIGW(ServiceAreaIn message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            serviceAreaInService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_SUCCESS)
//    public void receiveMessageBusinessAreaFromAPIGW(BusinessUnitMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            businessUnitService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_DEBIT_DOCUMENT_SUCCESS)
//    public void receiveMessageDebitDocumentFromAPIGW(DebitDocumentMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            debitDocumentService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS)
//    public void receiveMessageCreditDocFromAPIGW(CreditDocMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            creditdocService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.error("receiveMessageCustomerApigw Failed :", e);
//        }
//
//    }
//
//
//    //    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_SUCCESS)
////    public void receiveMessageCreditDocServiceFromAPIGW(CreditDocMessage message) {
////        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////
////            creditdocService.save(message);
////            System.out.println("success..!!");
////        } catch (Exception e) {
////            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
////        }
////
////    }
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_MANAGEMENT_SUCCESS)
////    public void receiveMessageStaffManagementFromAPIGW(StaffUserMessage message) {
////        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////
////            staffUserService.save(message);
////            System.out.println("success..!!");
////        } catch (Exception e) {
////            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
////        }
////
////    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_SUCCESS)
//    public void receiveMessageBranchFromAPIGW(SaveBranchSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//
//            branchService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_SUCCESS)
//    public void receiveMessageCustomerFromAPIGW(CustomerMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//
//        try {
//            customerService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CANCEL_REGENERATE_SUCCESS)
//    public void receiveMessageCancelRegenerate(CancelRegenerateInvoice message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//
//        try {
//            debitDocumentService.deleteflag(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//
//    }
//
//    //    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_SERIAL_NUMBER)
////    public void receiveMessageSerialNumberFromAPIGW(SerialNumberMessage message) {
////        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////              System.out.println("success..!!");
////        } catch (Exception e) {
////            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
////        }
////
////    }
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION)
//    public void receiveMessageCustomerPackageRelApigw(CustomerPackageRelMessage message) {
//        MDC.put("userName", "RabbitMq");
////		setUserProperties(message);
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        System.out.println("Message : " + message);
////		try{
////			Thread.sleep(15000);
//        customerPackageService.save(message);
////		} catch (Exception e){
////			e.printStackTrace();
////		}
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY)
//    public void receiveMessageCustomrInventoryMappingRelApigw(CustomerInventoryMappingMessage message) {
//        MDC.put("userName", "RabbitMq");
////		setUserProperties(message);
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        System.out.println("Message : " + message);
////		try{
////			Thread.sleep(15000);
//        customerInventoryService.save(message);
////		} catch (Exception e){
////			e.printStackTrace();
////		}
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION)
//    public void receiveMessageCustomerPackageRelApigw(CustomerServiceMappingMessage message) {
//        MDC.put("userName", "RabbitMq");
////		setUserProperties(message);
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        System.out.println("Message : " + message);
////		try{
////			Thread.sleep(15000);
//        customerServiceMappingService.save(message);
////		} catch (Exception e){
////			e.printStackTrace();
////		}
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_FOR_INTEGRATION)
//    public void receiveMessageCustomerPackageRelApigw(PlanServiceForIntegrationMessage message) {
//        MDC.put("userName", "RabbitMq");
////		setUserProperties(message);
//        log.info("Received Message From RabbitMq : <" + message + ">");
//        System.out.println("Message : " + message);
////		try{
////			Thread.sleep(15000);
//        servicesService.save(message);
////		} catch (Exception e){
////			e.printStackTrace();
////		}
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION)
//    public void receiveMessagePostpaidPlanApigw(PostpaidPlanMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessagePostpaidPlanApigw : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            postpaidPlanService.save(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM)
//    public void receiveMessageInventoryItemApigw(ItemMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessagePostpaidPlanApigw : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            inventoryItemService.save(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MDC.remove("userName");
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUST_PLAN_MAPPING_UPDATE)
//    public void receiveMessageCustomerPlanMappingUpdateFromAPIGW(CustPlanMappingUpdateMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            customerPackageService.update(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION)
//    public void receiveMessageApproveItemFromAPIGW(ApproveInventoryItemMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            approveInventoryItemService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION)
//    public void receiveMessageApproveItemFromAPIGW(ApproveRemoveInventoryItemRequestMessage message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            inventoryRemoveItemService.save(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRODUCTCATEGORY_INTEGRATOIN)
//    public void receiveMessageProductCategoryFromInventory(ProductCategoryDto message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            productCategoryService.saveProductCategoryFromInventory(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN)
//    public void receiveMessageWareHouseFromInventory(WareHouseDto message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            wareHouseService.saveWareHouseFromIntegration(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN)
//    public void inwardFromInventory(InwardDto message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            inwardService.saveInwardFromInventory(message);
//            System.out.println("success..!!");
//        } catch (Exception e) {
//            log.info("receiveMessageCustomerApigw Failed :" + e.getMessage());
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM)
//    public void receiveMessageTicketMessageFromApigw(TicketMessageIntegration message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessagePostpaidPlanApigw : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            caseService.save(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MDC.remove("userName");
//    }
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_CMS_CONFIGURATION_INTIGRATION)
//    public void receiveMessageInventoryMessageFromApigw(NMSServiceActivationDTO message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveMessagePostpaidPlanApigw : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            nmsService.activateNMSServices(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MDC.remove("userName");
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_NMS_SERVICE_DELETE_REQUEST)
//    public void receiveNmsServiceDeleteRequest(UuidDataDTO message) {
//        MDC.put("userName", "RabbitMq");
//        log.info("Received Message From RabbitMq receiveNmsServiceDeleteRequest : <" + message + ">");
//        try {
//            nmsService.deleteNMSService(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MDC.remove("userName");
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_CONFIGURTION_TO_INTEGRATION)
//    public void receivePaymentConfigMessage(PaymentConfigMessage message) {
//        log.info("Received Payment Config  Message From RabbitMq is : <" + message + ">");
//        try {
//            paymentConfigService.handleRecievePaymentConfig(message);
//        } catch (Exception e) {
//            log.info("processing Payment Config message  Failed :" + e.getMessage());
//        }
//    }
//
//
//    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_INTEGRATION)
//    public void receiveApymentAuditMessageFromIntegration(CustPayDTOMessage message) {
//        log.info("Received Message From RabbitMq receiveMessageCustomerMAcApigw: <" + message + ">");
//        try {
//            Boolean isCustomerPaymentExit = customerPaymentRepository.existsById(message.getOrderId());
//            if (!isCustomerPaymentExit) {
//                customerPaymentService.saveCustomerPayment(message);
//            } else {
//                customerPaymentService.updateCustomerPayment(message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
