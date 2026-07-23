package com.savbill.revenuemanagement.rabbitmq;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.Mvno.service.MvnoService;
import com.savbill.revenuemanagement.core.MvnoDiscountManagement.MvnoDiscountService;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.entity.partner.PricebookService;
import com.savbill.revenuemanagement.core.entity.staff.RolesService;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.entity.staff.TeamsService;
import com.savbill.revenuemanagement.core.repository.customer.CustPlanMappingService;
import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeHistoryRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.Inventory.ProductServiceImpl;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.partner.PartnerLedgerDetailsService;
import com.savbill.revenuemanagement.core.service.partner.PartnerService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.DbrService;
import com.savbill.revenuemanagement.core.service.prepaid.PartnerCommissionService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.mastermanagement.Area.service.AreaService;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.service.BankManagementService;
import com.savbill.revenuemanagement.mastermanagement.Branch.service.BranchService;
import com.savbill.revenuemanagement.mastermanagement.BusinessUnit.service.BusinessUnitService;
import com.savbill.revenuemanagement.mastermanagement.City.service.CityService;
import com.savbill.revenuemanagement.mastermanagement.Country.service.CountryService;
import com.savbill.revenuemanagement.mastermanagement.Pincode.service.PincodeService;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.service.ServiceAreaService;
import com.savbill.revenuemanagement.mastermanagement.State.service.StateService;
import com.savbill.revenuemanagement.productmanagement.Charge.service.ChargeService;
import com.savbill.revenuemanagement.productmanagement.Discount.service.DiscountService;
import com.savbill.revenuemanagement.productmanagement.Plan.service.PostPaidPlanService;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.service.PlanGroupService;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.productmanagement.servicePlan.service.ServicesService;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveCustomerDataShareMessage;
import com.savbill.revenuemanagement.server.CustomerProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceThread;


@Component
public class MessageReceiver  extends PostpaidInvoiceThread {
    private static Log log = LogFactory.getLog(MessageReceiver.class);
    @Autowired
    ServicesService servicesService;
    @Autowired
    TaxService taxService;
    @Autowired
    ChargeService chargeService;
    @Autowired
    PostPaidPlanService postPaidPlanService;
    @Autowired
    DiscountService discountService;
    @Autowired
    PlanGroupService planGroupService;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

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
    BankManagementService bankManagementService;

    @Autowired
    BranchService branchService;
    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;

    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    PricebookService pricebookService;

    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    private RolesService rolesService;

    @Autowired
    private PartnerService partnerService;
    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    DbrService dbrService;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    MvnoService mvnoService;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    CustomerChargeHistoryRepository customerChargeHistoryRepository;
    @Autowired
    TeamsService teamsService;

    @Autowired
    private MvnoDiscountService mvnoDiscountService;
    private static final org.apache.log4j.Logger logger = Logger.getLogger(MessageReceiver.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    //    @Autowired
//    SubscriberService subscriberService;
 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void savePlanService(SaveServicesSharedDataMessage servicesSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + servicesSharedDataMessage + ">");
        servicesService.saveService(servicesSharedDataMessage);
    }*/

  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void updatePlanService(UpdateServicesSharedDataMessage servicesSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + servicesSharedDataMessage + ">");
        servicesService.UpdateService(servicesSharedDataMessage);
    }*/

 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void saveTaxmessage(SaveTaxSharedDataMessage saveTaxSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + saveTaxSharedDataMessage + ">");
        taxService.saveTaxData(saveTaxSharedDataMessage);

    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void updateTaxmessage(UpdateTaxSharedDataMessage updateTaxSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + updateTaxSharedDataMessage + ">");
        taxService.updateTaxData(updateTaxSharedDataMessage);

    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void saveChargemessage(SaveChargeSharedDataMessage saveChargeSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + saveChargeSharedDataMessage + ">");
        chargeService.saveChargeData(saveChargeSharedDataMessage);

    }*/
/*
    @RabbitListener(queues = SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void updateChargemessage(UpdateChargeSharedDataMessage updateChargeSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + updateChargeSharedDataMessage + ">");
        chargeService.updateChargeData(updateChargeSharedDataMessage);

    }*/

 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void savePostPaidPlanmessage(SavePlanSharedDataMessage savePlanSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + savePlanSharedDataMessage + ">");
        postPaidPlanService.savePostPaidPlanData(savePlanSharedDataMessage);

    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void updatePostPaidPlanmessage(UpdatePlanSharedDataMessage updatePlanSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + updatePlanSharedDataMessage + ">");
        postPaidPlanService.updatePostPaidPlanData(updatePlanSharedDataMessage);

    }*/

  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
    public void saveDiscountmessage(SaveDiscountSharedMessage savePlanSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + savePlanSharedDataMessage + ">");
        discountService.saveDiscountData(savePlanSharedDataMessage);

    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_DISCOUNT_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE)
    public void updateDiscountmessage(UpdateDiscountSharedMessage updateDiscountSharedMessage) {
        log.info("Received Message From RabbitMq : <" + updateDiscountSharedMessage + ">");
        discountService.updateDiscountData(updateDiscountSharedMessage);

    }*/

  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void savePlanGroupmessage(SavePlanGroupSharedDataMessage planGroupSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + planGroupSharedDataMessage + ">");
        planGroupService.savePlanGroupData(planGroupSharedDataMessage);

    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void updatePlanGroupmessage(UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + updatePlanGroupSharedDataMessage + ">");
        planGroupService.updatePlanGroupData(updatePlanGroupSharedDataMessage);

    }*/

 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void saveCustomersmessage(SaveCustomerDataShareMessage saveCustomerDataShareMessage) {
        //TODO; add threading
        // parallelism for query request
//        InvoiceThread indexSearchThread = new InvoiceThread(saveCustomerDataShareMessage, subscriberService, messageReceiverWithThread);
////        String sessionId = receivedIndexQuery.getSessionId();
////        if (indexSearchThreadCleanUpRegistry.isIndexSearchThreadAvailable(sessionId)) {
////            indexSearchThreadCleanUpRegistry.remove(sessionId);
////        }
////        indexSearchThreadCleanUpRegistry.addMap(sessionId, indexSearchThread);
//        threadPool.executeTask(indexSearchThread);
        saveCustomerFromAPI(saveCustomerDataShareMessage);

    }*/

    @Transactional
    public void saveCustomerFromAPI(SaveCustomerDataShareMessage saveCustomerDataShareMessage) {
        try {
            logger.info("Initiating saveCustomerFromAPI received from CMS to save customer and create customer");
            TraceContext traceContext = tracer.currentSpan().context();
            Customers customers = subscriberService.saveCustomersData(saveCustomerDataShareMessage);
            if (customers != null && saveCustomerDataShareMessage.getRefMvno() != null) {
                mvnoService.updateMvnoRefForInvoice(Long.valueOf(saveCustomerDataShareMessage.getRefMvno()), customers.getId());
            }

            boolean postPaidInvoiceForPrepaidCharge = false;
            BillRun billRun = null;

            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customers.getId(), CommonConstants.CHARGE_TYPE_ADVANCE);

                if (count > 0) {
                    postPaidInvoiceForPrepaidCharge = true;
                    billRun = postpaidInvoiceService.addBillRunData(0, 0d, 0, 0);
                } else {
                    LocalDate earlyBilldate = customers.getNextBillDate().minusDays(customers.getEarlyBillDays());
                    if (earlyBilldate.isEqual(LocalDate.now()) || earlyBilldate.isAfter(LocalDate.now())) {
                        customers.setEarlyBilldate(earlyBilldate);
                        customersRepository.save(customers);
                        if (earlyBilldate.isEqual(LocalDate.now())) {
                            postPaidInvoiceForPrepaidCharge = true;
                        }
                    }
                }
            }

            if ((customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) || postPaidInvoiceForPrepaidCharge) {
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, customers.getId());
                data.put("Bullable_CUST_ID", customers.getPlanMappingList().get(0).getBillableCustomerId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, saveCustomerDataShareMessage.getCreatedById());
                if (postPaidInvoiceForPrepaidCharge) {
                    data.put(CustomerBillingMessage.POSTPAIDADVANCE, "Advance");
                }
                if (billRun != null) {
                    data.put(CustomerBillingMessage.BILL_RUN_ID, billRun.getId());
                }
                customerBillingMessage.setData(data);
                if (customers.getStatus().equalsIgnoreCase("NewActivation")) {
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                } else {
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.CREATE_CUSTOMER);
                }
                customerBillingMessage.setIsCaptiveportal(saveCustomerDataShareMessage.getIsCaptiveportal());
                customerBillingMessage.setReferenceNo(saveCustomerDataShareMessage.getReferenceNo());
                if (saveCustomerDataShareMessage.getRecordPaymentPojo() != null) {
                    RecordPaymentPojo recordPaymentPojo = saveCustomerDataShareMessage.getRecordPaymentPojo();
                    recordPaymentPojo.setCustomerid(customers.getId());
                    customerBillingMessage.setRecordPaymentDTO(recordPaymentPojo);
                    data.put(CustomerBillingMessage.MVNOID, customers.getMvnoId());
                }
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                }
                customerBillingMessage.setTraceContext(traceContext);
                customerBillingMessage.setIsEarlyBillDate(false);
                customerBillingMessage.setCustomerStatus(customers.getStatus());
                logger.info("Initiating processMessage method for Invoice creation save process  customer  : " + saveCustomerDataShareMessage.getUsername());

                CustomerProcessor customerProcessor = new CustomerProcessor(messageReceiverWithThread, customerBillingMessage, customers);
                getInvoicePool().execute(customerProcessor);
                //messageReceiverWithThread.processMessage(customerBillingMessage,customers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
    public void receiveMessageUpdateCustomer(UpdateCustomerShareDataMessage updateCustomerShareDataMessage) {
        log.info("Received Message From RabbitMq : <" + updateCustomerShareDataMessage + ">");
        try {
            subscriberService.updateCustomersData(updateCustomerShareDataMessage);
            log.info("Customer Updated Successfully From RabbitMq Message");
        }catch (Exception e){
            log.error("receiveMessageUpdateCustomer Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

        }*/


    // Create Country From RabbitMQ
   /* @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateCountry(CountrySharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            countryService.saveCountry(message);
            log.info("Country Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateCountry Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    // Update Country From RabbitMQ
 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateCountry(CountrySharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            countryService.updateCountry(message);
            log.info("Country Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateCountry Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    // Create State From RabbitMQ
  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateState(StateSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            stateService.saveStateEntity(message);
            log.info("State Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateState Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/
/*    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateStaffUser(UpdateStaffUserSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            staffUserService.updateStaffUser(message);
            log.info("Staff user Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateStaffUser Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/
    // Update State From RabbitMQ
 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateState(StateSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            stateService.updateStateEntity(message);
            log.info("State Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateState Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    // Create City From RabbitMQ
/*    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateCity(CitySharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            cityService.saveCityEntity(message);
            log.info("City Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateCity Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Update City From RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateCity(CitySharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            cityService.updateCityEntity(message);
            log.info("City Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateCity Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/


    //Create Pincode from RabbitMQ
/*    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreatePincode(SavePincodeSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pincodeService.savePincode(message);
            log.info("Pincode Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreatePincode Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdatePincode(SavePincodeSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pincodeService.updatePincode(message);
            log.info("Pincode Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdatePincode Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    //Create Area from RabbitMQ
/*    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateArea(SaveAreaSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            areaService.saveAreaEntity(message);
            log.info("Area Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateArea Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Area from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateArea(SaveAreaSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            areaService.updateAreaEntity(message);
            log.info("Area Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateArea Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/


    //Create Service Area from RabbitMQ
/*    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICEAREA_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateServiceArea(SaveServiceAreaSharedDataMessge message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.saveServiceArea(message);
            log.info("Service Area Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateServiceArea Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Service Area from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICEAREA_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateServiceArea(SaveServiceAreaSharedDataMessge message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.updateServiceArea(message);
            log.info("Service Area Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateServiceArea Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/


    //Create Business Unit from RabbitMQ
   /* @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateBusinessUnit(SaveBusinessUnitSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessUnitService.saveBusineeUnit(message);
            log.info("BusinessUnit Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateBusinessUnit Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    //Update Business Unit from RabbitMQ
//    @RabbitListener(queues = SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_REVENUE)
//    public void receiveMessageUpdateBusinessUnit(SaveBusinessUnitSharedDataMessage message) {
//        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
//        try {
//            businessUnitService.updateBusinessUnit(message);
//            log.info("BusinessUnit Updated Successfully From Rms");
//        } catch (Exception e) {
//            log.error("receiveMessageUpdateBusinessUnit Failed :" + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }


/*    //Create Business Unit from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateBusinessUnit(SaveBankSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            bankManagementService.saveBankdata(message);
            log.info("BusinessUnit Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateBusinessUnit Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Business Unit from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_BANK_MANAGEMENT_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateBusinessUnit(SaveBankSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            bankManagementService.updateBankdata(message);
            log.info("BusinessUnit Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBusinessUnit Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    //Create Branch from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCreateBranch(SaveBranchSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.saveBranch(message);
            log.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Branch from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateBranch(SaveBranchSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.updateBranch(message);
            log.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

    /*@RabbitListener(queues = SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_REVENUE)
    public void receiveMessageChangePLanRevenue(ChangePlanMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            TraceContext traceContext =tracer.currentSpan().context();
            MDC.put("type", "Fetch");
            MDC.put("userName", message.getGetCreatedByName());
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            Customers customers=customersRepository.findById(message.getCustomerChargeHistoryRevenues().get(0).getCustomerId()).orElse(null);
            List<Integer> oldDebitIds = prepaidInvoiceService.save(message);

            String eventType=message.getType();
            Long advanceCount=0l;
            if(message.getCustomerChargeHistoryRevenues()!=null && !message.getCustomerChargeHistoryRevenues().isEmpty())
                advanceCount=message.getCustomerChargeHistoryRevenues().stream().filter(x->x.getChargeType()!=null && x.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).count();

            Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customers.getId(),CommonConstants.CHARGE_TYPE_ADVANCE);
            boolean postPaidInvoiceForPrepaidCharge = false;
            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID) && count>0 && !message.isChangePlanNextBillDate() && ((advanceCount>0 && eventType!=null && eventType.equalsIgnoreCase("addNewService")) || (eventType==null || !eventType.equalsIgnoreCase("addNewService")))){
                postPaidInvoiceForPrepaidCharge = true;
            }

            if ((customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) || postPaidInvoiceForPrepaidCharge)
            {
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, message.getNewCustPlanMappingRevenues().get(0).getCustomerId());
                data.put(CustomerBillingMessage.RENEWAL_ID, message.getRenewalId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, message.getCreatedById());
                if(message.getPaySource() != null && message.getPaySource().length() > 0){
                    data.put(CustomerBillingMessage.PAYMENT_SOURCE,message.getPaySource());
                }
                if(message.getAdditionalInformationDTO() != null){
                    data.put(CustomerBillingMessage.ADDITIONALINFORMATIONDTO , message.getAdditionalInformationDTO());
                }
                List<CustomerChargeHistoryRevenue> chargeHistoryRevenue = message.getCustomerChargeHistoryRevenues();
                List<Double> discounts = new ArrayList<>();
                List<String> discountTypes = new ArrayList<>();
                Map<Integer, Double> discountMap = new HashMap<>();
                Map<Integer, String> discountTypeMap = new HashMap<>();
                for (CustPlanMappingRevenue chargeHistoryRevenues : message.getNewCustPlanMappingRevenues()) {
                    Integer custServiceMappingId = chargeHistoryRevenues.getCustServiceMappingId(); // Get the custServiceId
                    Double discount = chargeHistoryRevenues.getDiscount(); // Get the discount

                    // Retrieve the customer service mapping by the specific service ID
                    customerServiceMapRepository.findById(custServiceMappingId).ifPresent(mapping -> {
                        // Store the discount and discount type mapped by service ID
                        discountMap.put(custServiceMappingId, mapping.getDiscount());
                        discountTypeMap.put(custServiceMappingId, mapping.getDiscountType());
                    });
                }

// Collect discounts and discount types in the order of service IDs
                discountMap.keySet().stream().sorted().forEach(id -> {
                    discounts.add(discountMap.get(id));
                    discountTypes.add(discountTypeMap.get(id));
                });
//                Collections.reverse(discounts);
                data.put(CustomerBillingMessage.DISCOUNT,discounts);
                data.put(CustomerBillingMessage.DISCOUNTTYPE, discountTypes);
                if(message.getBuId() != null && !message.getBuId().isEmpty()){
                    data.put(CustomerBillingMessage.BUIDS , message.getBuId());
                }
                if(message.getMvnoId() != null){
                    data.put(CustomerBillingMessage.MVNOID , message.getMvnoId());
                }
                if(message.getLcoId() != null){
                    data.put(CustomerBillingMessage.PARTNERID , message.getLcoId());
                }
                if(message.getIsLco() != null){
                    data.put(CustomerBillingMessage.ISLCO , message.getIsLco());
                }
                if(message.getCreatedById() != null){
                    data.put(CustomerBillingMessage.CREATEDBYID , message.getCreatedById());
                }
                if(message.getGetCreatedByName() != null){
                    data.put(CustomerBillingMessage.CREATEDBYNAME , message.getGetCreatedByName());
                }
                if(!CollectionUtils.isEmpty(message.getOverrideChargeIds())) {
                    data.put(CustomerBillingMessage.OVERRIDECHARGES, message.getOverrideChargeIds());
                }
                if(message.getRecordPaymentDTO() != null){
                    customerBillingMessage.setRecordPaymentDTO(message.getRecordPaymentDTO());
                }
                if (count>0){
                    data.put(CustomerBillingMessage.POSTPAIDADVANCE,"Advance");
                }
                customerBillingMessage.setType(message.getType());
                data.put(CustomerBillingMessage.RENEWAL_ID, message.getRenewalId());

                if (!CollectionUtils.isEmpty(oldDebitIds)) {
                    String oldDebitDocumentIdStr = "";
                    for (Integer invoiceNo : oldDebitIds) {
                        oldDebitDocumentIdStr = oldDebitDocumentIdStr + invoiceNo + ",";
                    }
                    oldDebitDocumentIdStr = oldDebitDocumentIdStr.substring(0, oldDebitDocumentIdStr.length() - 1);
                    data.put(CustomerBillingMessage.oldDebitDocId, oldDebitDocumentIdStr);
                } else {
                    data.put(CustomerBillingMessage.oldDebitDocId, "");
                }
                message.getCreatedById();
                if (message.getParentId()!=null && message.getChildIds()!=null && message.getChildIds().size()>0){
                    data.put(CustomerBillingMessage.CUST_ID,message.getParentId());
                    customerBillingMessage.setChildIds(message.getChildIds());
                }
                customerBillingMessage.setData(data);
                if(message.getType()!=null && message.getType().equalsIgnoreCase("addNewService") || message.getType().equalsIgnoreCase("isCAFCustomer")){
                    customerBillingMessage.setNewServiceId(message.getCustomerServiceMappingRevenues().get(0).getId());
                }
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                    customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                    customerBillingMessage.setIsEarlyBillDate(false);
                    if (message.getType()!=null){
                        customerBillingMessage.setRenew(message.getType());
                    }
                }
                messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
            }

            logger.info(LogConstants.REQUEST_FROM + LogConstants.CMS+ LogConstants.REQUEST_FOR + message.getType() +", for Customer Id: " +  customers.getId() + LogConstants.REQUEST_BY + message.getGetCreatedByName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS);
        } catch (Exception e) {
            logger.error(LogConstants.REQUEST_FROM+ LogConstants.CMS+ LogConstants.REQUEST_FOR+ message.getType() +  LogConstants.REQUEST_BY + message.getGetCreatedByName()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage());
            throw new RuntimeException(e);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }*/

    /*@RabbitListener(queues = SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_SHARE_REVENUE)
    public void receiveMessageCustDirectChargeRevenue(ChangePlanMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            prepaidInvoiceService.saveCustDirectCharge(message);

            log.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_List_SHARE_REVENUE)
    public void receiveMessageCustDirectChargeListRevenue(ChangePlanMessageList message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            for (ChangePlanMessage changePlanMessage: message.getChangePlanMessageList()) {
                prepaidInvoiceService.saveCustDirectCharge(changePlanMessage);
            }
            log.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_DIRECT_CHARGE_DATA_List_SHARE_REVENUE)
    public void receiveMessageCustDirectChargeListRevenue(ChangePlanMessageList message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            for (ChangePlanMessage changePlanMessage: message.getChangePlanMessageList()) {
                prepaidInvoiceService.saveCustDirectCharge(changePlanMessage);
            }
            log.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_REVENUE)
    public void receiveStaffUserRevenue(SaveStaffUserSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            staffUserService.saveStaffuser(message);

            log.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE)
    public void receiveMessageInventoryProductRevenue(ProductMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            productService.configureProductReceiveMessage(message);
            log.info("Inventory product saved sucefully");
        } catch (Exception e) {
            log.error("receiveMessageInventoryProduct Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE)
    public void receiveMessageInventoryCustomerRevenue(CustomerInventoryRevenueMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            productService.configureCustomerInventoryReceiveMessage(message);
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/


   /* @RabbitListener(queues = SharedDataConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_REVENUE)
    public void receiveMessageCafToCustomer(CaftoCustomerMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            prepaidInvoiceService.cafToCustomer(message);
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

/*
    @RabbitListener(queues = RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE)
    public void receiveMessageApproveOrgInvoice(AppproveOrgInvoiceMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            if(message.getIsApproveRequest()!=null)
                prepaidInvoiceService.billToOrg(message);
            DebitDocument debitDocument = debitDocRepository.findById(message.getDebitdocId()).orElse(null);
            if (debitDocument != null) {
                if(message.getIsApproveRequest()!=null && message.getIsApproveRequest()) {
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                }
                if(message.getIsApproveRequest()!=null && !message.getIsApproveRequest())
                {
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                }
                debitDocRepository.save(debitDocument);
            }
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
*/

/*    @RabbitListener(queues = SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE)
    public void receiveMessagePricebook(SavePricebookSharedMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pricebookService.save(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT)
    public void receiveMessageUpdateCPr(UpdateCustomerCprDateAndStatus message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            custPlanMappingService.updateCprDateAndStatus(message);
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @Transactional
    @RabbitListener(queues = SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE)
    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            partnerService.savePartnerEntiry(message);
            Partner partner = partnerRepository.findById(message.getId()).orElse(null);
            if (partner != null && partner.getBalance() != null && partner.getBalance() > 0)
                partnerLedgerDetailsService.reverseBalance(null, 0.0, message.getBalance(), message.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
            log.info("Partner Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreatePartner Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
*/
  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE)
    public void receiveMessagePriceUpdatebook(SavePricebookSharedMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pricebookService.update(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_REVENUE)
    public void receiveMessageCretaeRole(SaveRoleSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            rolesService.createNewRole(message);
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_REVENUE)
    public void receiveMessageUpdateRole(UpdateRoleSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            rolesService.updateRoles(message);
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
*/
 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE)
    public void receiveCustomerChangeStaus(CustomerTerminationMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            subscriberService.terminate(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }

    }*/

   /* @RabbitListener(queues = SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE)
    public void receiveUpadatePartner(UpdatePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            partnerService.updatePartnerData(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }

    }*/
/*

    @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE)
    public void receiveShiftLocationParter(ShiftlocationMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            Customers customers=customersRepository.findById(message.getCustId()).orElse(null);
            if(customers!=null){
            if(message.getTranferConnission()!=null && message.getTranferConnission()>0.0) {
                partnerCommissionService.transferCommissionFromOnePartnerToAnotherPartner(message.getOldpartnerId(), message.getNewPartnerId(), message.getTranferConnission(), customers);
            }if(message.getTransferBalance()!=null && message.getTransferBalance()>0.0){
                partnerCommissionService.transferBalanceFromOnePartnerToAnotherPartner(message.getOldpartnerId(), message.getNewPartnerId(), message.getTransferBalance(),customers);
                }
            }
            ServiceArea serviceArea=serviceAreaRepository.findById(message.getServiceAreaId()).orElse(null);
            dbrService.updateServiceAreaIdForCustomer(message.getCustId(),serviceArea, LocalDate.now());


            //partnerService.updatePartnerData(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }

    }
*/

  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE)
    public void receivePaymentMessage(SavePartnerPaymentMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            partnerService.approvebalance(message.getPartnerPaymentDTO(),message.getPartnerPayment());
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }

    }*/

   /* @Transactional
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageForMvnoCreate(SaveMvnoSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            mvnoService.saveMVNOEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDAT_MVNO_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageForMvnoUpdate(UpdateMvnoSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            mvnoService.updateMVNOEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }*/


 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_SERVICE_TERMINATION_REVENUE)
    public void receiveMessageServiceTermination(ServiceTerminationMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            custPlanMappingService.changeStatusOfCustServices(message.getCustomerServiceId(), message.getCustomerStatus(), message.getRemarks(), Boolean.FALSE,message.getGeneratecn());
            log.info("Customer inventory Successfully From Inventory");
        } catch (Exception e) {
            log.error("receiveMessageUpdateBranch Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
*/
    //Create Client Service from RabbitMQ
/*    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageCreateClientService(SaveClientServMessge message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            clientServiceSrv.saveSharedClientService(message);
            log.info("Client Service Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Client Service from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            clientServiceSrv.updateSharedClientService(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

/*    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_REVENUE)
    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            rolesService.saveRole(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }*/
  /*  @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_REVENUE)
    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            rolesService.deleteRole(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }
*/
    /**message reciever for online payment adjustment started**/

  /*  @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT)
    public void receiveMessageForOnlinePayementAdjustment(SendOnlinePaymentRevenueMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            prepaidInvoiceService.adjustAllPaymentAgainstInvoice(message);
            log.info("Online payment is adjusted sucessfully");
        } catch (Exception e) {
            log.error("SendOnlinePaymentRevenueMessage Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }*/


    /**message reciever for online payment adjustment ended**/

    /**
     * message receiver for inventory record payment
     * @param message
     */
/*    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE)
    public void receiveMessageForInventoryCreditNote(RecordPaymentMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
//            prepaidInvoiceService.adjustAllPaymentAgainstInvoice(message);
            creditDocService.adjustCreditNoteForInventory(message);
            log.info("Inventory record payment is adjusted sucessfully");
        } catch (Exception e) {
            log.error("receiveMessageForInventoryCreditNote Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/


 /*   @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_DBR_SERVICE_HOLD_RESUME)
    public void receiveMessageForInventoryCreditNote(DbrHoldResumeMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try
        {
            if(message!=null && message.getCprIds()!=null && !message.getCprIds().isEmpty())
            {
                if(message.getIsServiceHold())
                    dbrService.dbrHoldOnServicePause(message.getCprIds());
                else
                    dbrService.dbrResumeOnServiceResume(message.getCprIds());
            }
            log.info("Inventory record payment is adjusted sucessfully");
        } catch (Exception e) {
            log.error("receiveMessageForInventoryCreditNote Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

 /*   @RabbitListener(queues = SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_CMS_REVENUEMANAGEMENT)
    public void updateCustomerDiscountmessage(CustomerDiscountPojo updateDiscountSharedMessage) {
        log.info("Received Message From RabbitMq : <" + updateDiscountSharedMessage + ">");
        subscriberService.updateCustomerDiscount(updateDiscountSharedMessage);
    }*/


  /*  @RabbitListener(queues = SharedDataConstants.QUEUE_SAVE_VOUCHER_BATCH_DATA_SHARE_TO_REVENUEMANAGEMENT)
    public void saveVoucherBatchMessage(SaveVoucherBatchSharedDataMessage saveVoucherBatchSharedDataMessage) {
        log.info("Received Message From RabbitMq : <" + saveVoucherBatchSharedDataMessage + ">");
        partnerService.updatePartnerBalanceForVoucherBatch(saveVoucherBatchSharedDataMessage);
    }
*/
   /* @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_TEAM_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageCreateTeam(SaveTeamsSharedSharedData message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamsService.saveTeams(message);
            log.info("Teams Created Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageCreateTeam Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_TEAM_COMMON_APIGW_TO_REVENUE)
    public void receiveMessageUpdateTeam(UpdateTeamsSharedData message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamsService.updateTeams(message);
            log.info("Team Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Transactional
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_MVNO_COMMON_APIGW_TO_REVENUE_ISP)
    public void receiveMessageUpdateTeam(UpdateMvnoData message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            mvnoService.updateMvnoIsp(message);
            log.info("Mvno Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateMvno Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
*/
  /*  @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS)
    public void receiveBudPayPaymentMessage(BudPayPaymentMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
           prepaidInvoiceService.updateProvisionalPortalCustomer(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

  /*  @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE)
    public void receiveMvnoDiscountMessage(MvnoDiscountMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            mvnoDiscountService.saveMvnoDiscountFromMessageReceiver(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }*/

   /* @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE)
    public void receiveBudpayChangePlanMessageMessage(BudpayChangePlanMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            creditDocService.processBudPaychangePlanMessage(message);
        } catch (Exception e) {
            log.error("receiveMessageUpdateTeam Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_APIGW_CUSTOMER_STATUS_UPDATE_REVENUE)
    public void receiveMessageUpdateCustomerStatus(CustomerUpdateMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            Map<String,Object> customerData = message.getCustomerData();
            if(Integer.valueOf(customerData.get("id").toString())!=null)
            {
                Customers customers=customersRepository.findById(Integer.valueOf(customerData.get("id").toString())).orElse(null);
                if(customers!=null)
                {
                    customers.setStatus(customerData.get("status").toString());
                    customersRepository.save(customers);
                }
            }
        } catch (Exception e) {
            log.error("receiveMessageUpdateMVNO Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
}*/
