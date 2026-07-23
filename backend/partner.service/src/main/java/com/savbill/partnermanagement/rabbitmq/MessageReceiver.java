/*
package com.savbill.partnermanagement.rabbitmq;

import com.savbill.partnermanagement.MicroSeviceDataShare.PartnerAmountMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SaveCustomerDataShareMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.savbill.partnermanagement.MicroSeviceDataShare.SharedMessages.UpdateClientServMessage;
import com.savbill.partnermanagement.MicroSeviceDataShare.UpdateCustomerShareDataMessage;
import com.savbill.partnermanagement.customers.CustomerService;
import com.savbill.partnermanagement.customers.Customers;
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
import com.savbill.partnermanagement.modules.Region.service.RegionService;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.modules.Plan.service.PostPaidPlanService;
import com.savbill.partnermanagement.modules.PlanGroup.service.PlanGroupService;
import com.savbill.partnermanagement.modules.PlanService.PlanServiceService;
import com.savbill.partnermanagement.modules.Role.RoleService;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserService;
import com.savbill.partnermanagement.modules.Tax.service.TaxService;
import com.savbill.partnermanagement.modules.Teams.TeamsService;
import com.savbill.partnermanagement.modules.partner.service.PricebookService;

import com.savbill.partnermanagement.rabbitmq.master.*;
import com.savbill.partnermanagement.rabbitmq.partner.SavePartnerSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.partner.UpdatePartnerSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.*;
import com.savbill.partnermanagement.rabbitmq.setting.*;
import com.savbill.partnermanagement.rabbitmq.master.*;
import com.savbill.partnermanagement.rabbitmq.partner.SavePartnerSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.partner.UpdatePartnerSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.*;
import com.savbill.partnermanagement.rabbitmq.setting.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MessageReceiver {
    //    private static Log log = LogFactory.getLog(MessageReceiver.class);
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    CityService cityService;

    @Autowired
    MvnoService mvnoService;

    @Autowired
    RoleService roleService;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    AreaService areaService;

    @Autowired
    BranchService branchService;

    @Autowired
    BusinessUnitService businessUnitService;

    @Autowired
    PincodeService pincodeService;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    PlanServiceService planServiceService;

    @Autowired
    PartnerService partnerService;

    @Autowired
    TeamsService teamsService;

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
    BusinessVerticalsService businessVerticalsService;

    @Autowired
    RegionService regionService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    CustomerService customerService;


    @RabbitListener(queues = RabbitMqConstants.TEST_RECEIVE)
    public void receiveMessageCustomerApigw(CustomMessage message) {
        logger.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            System.out.println("success..!!");
        }
        catch(Exception e) {
            logger.info("receiveMessageCustomerApigw Failed :"+e.getMessage());
        }

    }
    // Create Country From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
    public void receiveMessageCreateCountry(SaveCountrySharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            countryService.saveCountry(message);
            logger.info("Country Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateCountry Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Update Country From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
    public void receiveMessageUpdateCountry(UpdateCountrySharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            countryService.updateCountry(message);
            logger.info("Country Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateCountry Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Create State From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_STATE_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateState(SaveStateSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            stateService.saveStateEntity(message);
            logger.info("State Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateState Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Update State From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_STATE_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateState(UpdateStateSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            stateService.updateStateEntity(message);
            logger.info("State Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateState Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Create City From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CITY_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateCity(SaveCitySharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            cityService.saveCityEntity(message);
            logger.info("City Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateCity Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // Update City From RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CITY_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateCity(UpdateCitySharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            cityService.updateCityEntity(message);
            logger.info("City Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateCity Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateMVNO(UpdateMvnoSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            mvnoService.updateMVNOEntity(message);
            logger.info("MVNO Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateMVNO Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Role from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageCreateRole(SaveRoleSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.saveRoleEntity(message);
            logger.info("Role Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateRole Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Role from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdateRole(UpdateRoleSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.updateRoleEntity(message);
            logger.info("Role Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateRole Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Staff User from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateStaffUser(SaveStaffUserSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            staffUserService.saveStaffUserEntity(message);
            logger.info("Staff user Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateStaffUser Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Staff User from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateStaffUser(UpdateStaffUserSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            staffUserService.updatetaffUserEntity(message);
            logger.info("Staff user Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateStaffUser Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Area from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_AREA_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateArea(SaveAreaSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            areaService.saveAreaEntiry(message);
            logger.info("Area Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateArea Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Area from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_AREA_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateArea(UpdateAreaSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            areaService.updateAreaEntiry(message);
            logger.info("Area Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateArea Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Branch from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageCreateBranch(SaveBranchSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.saveBranchEntity(message);
            logger.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Branch from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdateBranch(UpdateBranchSharedData message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.updateBranchEntity(message);
            logger.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Business Unit from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageCreateBusinessUnit(SaveBusinessUnitSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessUnitService.saveBusinessUnitEntity(message);
            logger.info("BusinessUnit Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBusinessUnit Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Business Unit from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdateBusinessUnit(UpdateBusinessUnitSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessUnitService.updateBusinessUnitEntity(message);
            logger.info("BusinessUnit Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBusinessUnit Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Pincode from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreatePincode(SavePincodeSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pincodeService.savePincodeEntity(message);
            logger.info("Pincode Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreatePincode Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Business Unit from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdatePincode(UpdatePincodeSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pincodeService.updatePincodeEntity(message);
            logger.info("Pincode Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdatePincode Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Service Area from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateServiceArea(SaveServiceAreaSharedDataMessge message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.saveServiceAreaEntity(message);
            logger.info("Service Area Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateServiceArea Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Service Area from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateServiceArea(UpdateServiceAreaSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.updateServiceAreaEntity(message);
            logger.info("Service Area Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateServiceArea Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Services from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateServices(UpdateServiceAreaSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            serviceAreaService.updateServiceAreaEntity(message);
            logger.info("Services Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateServices Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Services from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdateServices(UpdateServicesSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            planServiceService.updatePlanServiceEntity(message);
            logger.info("Services Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateServices Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Partner from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            //partnerService.savePartnerEntiry(message);
            logger.info("Partner Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreatePartner Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Partner from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdatePartner(UpdatePartnerSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            //partnerService.updatePartnerEntiry(message);
            logger.info("Partner Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdatePartner Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Teams from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageCreateTeams(SaveTeamsSharedSharedData message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamsService.saveTeams(message);
            logger.info("Teams Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateTeams Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Teams from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE)
    public void receiveMessageUpdateTeams(UpdateTeamsSharedData message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            teamsService.updateTeams(message);
            logger.info("Teams Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateTeams Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_PARTNER)
    public void savePlanService( SaveServicesSharedDataMessage servicesSharedDataMessage) throws Exception {
        logger.info("Received Message From RabbitMq : <" + servicesSharedDataMessage + ">");
        serviceService.savePlanServiceEntity(servicesSharedDataMessage);
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER)
    public void updatePlanService(UpdateServicesSharedDataMessage servicesSharedDataMessage) throws Exception {
        logger.info("Received Message From RabbitMq : <" + servicesSharedDataMessage + ">");
        serviceService.updatePlanServiceEntity(servicesSharedDataMessage);
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER)
    public void saveTaxmessage(SaveTaxSharedDataMessage saveTaxSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + saveTaxSharedDataMessage + ">");
        taxService.saveTaxData(saveTaxSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER)
    public void updateTaxmessage(UpdateTaxSharedDataMessage updateTaxSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + updateTaxSharedDataMessage + ">");
        taxService.updateTaxData(updateTaxSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_PARTNER)
    public void saveChargemessage(SaveChargeSharedDataMessage saveChargeSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + saveChargeSharedDataMessage + ">");
        chargeService.saveChargeData(saveChargeSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER)
    public void updateChargemessage(UpdateChargeSharedDataMessage updateChargeSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + updateChargeSharedDataMessage + ">");
        chargeService.updateChargeData(updateChargeSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_CREATE_DATA_SHARE_PARTNER)
    public void savePostPaidPlanmessage(SavePlanSharedDataMessage savePlanSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + savePlanSharedDataMessage + ">");
        postPaidPlanService.savePostPaidPlanData(savePlanSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER)
    public void updatePostPaidPlanmessage(UpdatePlanSharedDataMessage updatePlanSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + updatePlanSharedDataMessage + ">");
        postPaidPlanService.updatePostPaidPlanData(updatePlanSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_PARTNER)
    public void savePlanGroupmessage(SavePlanGroupSharedDataMessage planGroupSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + planGroupSharedDataMessage + ">");
        planGroupService.savePlanGroupData(planGroupSharedDataMessage);

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER)
    public void updatePlanGroupmessage(UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage) {
        logger.info("Received Message From RabbitMq : <" + updatePlanGroupSharedDataMessage + ">");
        planGroupService.updatePlanGroupData(updatePlanGroupSharedDataMessage);

    }

//    @RabbitListener(queues = SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT)
//    public void saveCustomersmessage(SaveCustomerDataShareMessage saveCustomerDataShareMessage) {
//        logger.info("Received Message From RabbitMq : <" + saveCustomerDataShareMessage + ">");
//        Customers customers = subscriberService.saveCustomersData(saveCustomerDataShareMessage);
//
//        if(customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID) && !customers.getIstrialplan()) {
//            CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
//            Map<String, Object> data = new HashMap<>();
//            data.put(CustomerBillingMessage.CUST_ID, customers.getId());
//            customerBillingMessage.setData(data);
//            if (customers.getStatus().equalsIgnoreCase("NewActivation")){
//                customerBillingMessage.setType(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
//            }else {
//                customerBillingMessage.setType(Constants.INVOICE_TYPE.CREATE_CUSTOMER);
//            }
//            messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
//        }
//    }
//    @RabbitListener(queues = SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT)
//    public void updateCustomerspmessage(UpdateCustomerShareDataMessage updateCustomerShareDataMessage) {
//        log.info("Received Message From RabbitMq : <" + updateCustomerShareDataMessage + ">");
//        subscriberService.updateCustomersData(updateCustomerShareDataMessage);
//
//    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRICEBOOK_CREATE_DATA_PARTNER)
    public void receiveMessagePriceCreate(SavePricebookSharedMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pricebookService.save(message);
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER)
    public void receiveMessagePriceUpdatebook(UpdatePricebookSharedMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            pricebookService.update(message);
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_PARTNER_MICROSERVICE)
    public void receiveMessageCreateBranchPartner(SaveBranchSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.saveBranchEntity(message);
            logger.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Branch from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_PARTNER_MICROSERVICE)
    public void receiveMessageUpdateBranchPartner(UpdateBranchSharedData message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            branchService.updateBranchEntity(message);
            logger.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateBusinessVericalsPartner(SaveBusinessVerticalSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessVerticalsService.saveBusinessVertical(message);
            logger.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Branch from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateBusinessVerticalsPartner(UpdateBusinessVerticalSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessVerticalsService.updateBusinessVertical(message);
            logger.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Region
    @RabbitListener(queues = RabbitMqConstants.QUEUE_REGION_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateRegionPartner(SaveRegionSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            regionService.saveRegion(message);
            logger.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Branch from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_REGION_UPDATE_DATA_SHARE_PARTNER)
    public void receiveMessageUpdateRegionPartner(UpdateRegionSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            regionService.updateRegion(message);
            logger.info("Branch Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER)
    public void receiveMessagePartnerBalanceFromRevenue(PartnerAmountMessage message) {
        logger.info("Received Message From RabbitMq For creditdoc Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            partnerService.updateAmount(message);
        }
        catch(Exception e) {
            logger.info("receiveMessageCustomerApigw Failed for credit doc Creation :"+e.getMessage());
        }

    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_VERTICALS_DATA_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateDataBusinessVericalsPartner(SaveBusinessVerticalSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            businessVerticalsService.saveBusinessVertical(message);
            logger.info("Branch Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Create Client Service from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
    public void receiveMessageCreateClientService(SaveClientServMessge message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            clientServiceSrv.saveSharedClientService(message);
            logger.info("Client Service Created Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageCreateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Update Client Service from RabbitMQ
    @RabbitListener(queues = SharedDataConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_PARTNER)
    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            clientServiceSrv.updateSharedClientService(message);
            logger.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_PARTNER)
    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.saveRole(message);
            logger.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_PARTNER)
    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.deleteRole(message);
            logger.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_Partner_ISP)
    public void receiveMessageMvnoIdUpdateISP(UpdateMvnoData message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
           mvnoService.UpdateMvnoIdISP(message.getOldmvnoId(), message.getNewmvnoId());
        } catch (Exception e) {
            logger.error("receiveMessageUpdateMvnoId Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }


    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_PARTNER)
    public void receiveMessageForCustomersCreate(SaveCustomerDataShareMessage message) {
        logger.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            customerService.saveCustomers(message);
        }
        catch(Exception e) {
            logger.info("receiveMessageCustomerApigw Failed for Customer Creation :"+e.getMessage());
        }
    }


    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_PARTNER)
    public void receiveMessageForCustomersUpdate(UpdateCustomerShareDataMessage message) {
        logger.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            customerService.updateCustomers(message);
        }
        catch(Exception e) {
            logger.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }
    }


    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_PARTNER)
    public void receiveMessageCreateMVNO(SaveMvnoSharedDataMessage message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            mvnoService.saveMVNOEntity(message);
            logger.info("MVNO Created Successfully From RabbitMq Message");
        } catch (Exception e) {
            logger.error("receiveMessageCreateMVNO Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
*/
