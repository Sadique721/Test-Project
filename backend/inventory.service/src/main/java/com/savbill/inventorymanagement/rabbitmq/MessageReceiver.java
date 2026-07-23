//package com.savbill.inventorymanagement.rabbitmq;
//
//import com.savbill.inventorymanagement.modules.CasMaster.CasMasterService;
//import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeService;
//import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
//import com.savbill.inventorymanagement.modules.Customers.CustomerService;
//import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
//import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductDto;
//import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
//import com.savbill.inventorymanagement.modules.MasterManagement.Area.AreaService;
//import com.savbill.inventorymanagement.modules.MasterManagement.Branch.BranchService;
//import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnitService;
//import com.savbill.inventorymanagement.modules.MasterManagement.City.CityService;
//import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryService;
//import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.PincodeService;
//import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
//import com.savbill.inventorymanagement.modules.MasterManagement.State.StateService;
//import com.savbill.inventorymanagement.modules.Mvno.MvnoService;
//import com.savbill.inventorymanagement.modules.Mvno.UpdateMvnoData;
//import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerService;
//import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroupService;
//import com.savbill.inventorymanagement.modules.PlanService.PlanServiceService;
//import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanService;
//import com.savbill.inventorymanagement.modules.Role.RoleService;
//import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
//import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxService;
//import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
//import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.TeamsService;
//import com.savbill.inventorymanagement.rabbitmq.SharedMessages.*;
//import com.savbill.inventorymanagement.security.spring.SpringContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
////import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.transaction.Transactional;
//
//@Component
//public class MessageReceiver {
////    private static Log log = LogFactory.getLog(MessageReceiver.class);
////    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
////
////    @Autowired
////    CountryService countryService;
////
////    @Autowired
////    StateService stateService;
////
////    @Autowired
////    CityService cityService;
////
////    @Autowired
////    MvnoService mvnoService;
////
////    @Autowired
////    RoleService roleService;
////
////    @Autowired
////    StaffUserService staffUserService;
////
////    @Autowired
////    AreaService areaService;
////
////    @Autowired
////    BranchService branchService;
////
////    @Autowired
////    BusinessUnitService businessUnitService;
////
////    @Autowired
////    PincodeService pincodeService;
////
////    @Autowired
////    ServiceAreaService serviceAreaService;
////
////    @Autowired
////    PlanServiceService planServiceService;
////
////    @Autowired
////    PartnerService partnerService;
////
////    @Autowired
////    PostpaidPlanService postpaidPlanService;
////
////    @Autowired
////    TaxService taxService;
////
////    @Autowired
////    PlanGroupService planGroupService;
////
////    @Autowired
////    ChargeService chargeService;
////
////    @Autowired
////    CustomerService customerService;
////
////    @Autowired
////    TeamsService teamsService;
////
////    @Autowired
////    HierarchyService hierarchyService;
////
////    @Autowired
////    ProductServiceImpl productService;
////
////    @Autowired
////    CasMasterService casMasterService;
////
////    @Autowired
////    ClientServiceService clientServiceService;
////
////
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.TEST_RECEIVE)
////    public void receiveMessageCustomerApigw(CustomMessage message) {
////        logger.info("Received Message From RabbitMq receiveMessage : <" + message + ">");
////        System.out.println("Message : " + message);
////        try {
////            System.out.println("success..!!");
////        }
////        catch(Exception e) {
////            logger.info("receiveMessageCustomerApigw Failed :"+e.getMessage());
////        }
////
////    }
////    // Create Country From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateCountry(SaveCountrySharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            countryService.saveCountry(message);
////            logger.info("Country Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateCountry Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    // Update Country From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateCountry(UpdateCountrySharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            countryService.updateCountry(message);
////            logger.info("Country Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateCountry Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    // Create State From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateState(SaveStateSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            stateService.saveStateEntity(message);
////            logger.info("State Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateState Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    // Update State From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateState(UpdateStateSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            stateService.updateStateEntity(message);
////            logger.info("State Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateState Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    // Create City From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateCity(SaveCitySharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            cityService.saveCityEntity(message);
////            logger.info("City Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateCity Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    // Update City From RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateCity(UpdateCitySharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            cityService.updateCityEntity(message);
////            logger.info("City Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateCity Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create MVNO from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateMVNO(SaveMvnoSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            mvnoService.saveMVNOEntity(message);
////            logger.info("MVNO Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateMVNO Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update MVNO from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateMVNO(UpdateMvnoSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            mvnoService.updateMVNOEntity(message);
////            logger.info("MVNO Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateMVNO Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Role from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateRole(SaveRoleSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
//////            roleService.saveRoleEntity(message);
////            roleService.saveRoleWithNewACL(message);
////            logger.info("Role Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateRole Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Role from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateRole(UpdateRoleSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
//////            roleService.updateRoleEntity(message);
////            roleService.updateRoleWithNewACl(message);
////            logger.info("Role Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateRole Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Staff User from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateStaffUser(SaveStaffUserSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            staffUserService.saveStaffUserEntity(message);
////            logger.info("Staff user Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateStaffUser Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Staff User from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateStaffUser(UpdateStaffUserSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            staffUserService.updatetaffUserEntity(message);
////            logger.info("Staff user Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateStaffUser Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Area from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateArea(SaveAreaSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            areaService.saveAreaEntiry(message);
////            logger.info("Area Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateArea Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Area from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateArea(UpdateAreaSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            areaService.updateAreaEntiry(message);
////            logger.info("Area Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateArea Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Branch from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateBranch(SaveBranchSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            branchService.saveBranchEntity(message);
////            logger.info("Branch Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateBranch Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Branch from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateBranch(UpdateBranchSharedData message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            branchService.updateBranchEntity(message);
////            logger.info("Branch Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateBranch Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Business Unit from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateBusinessUnit(SaveBusinessUnitSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            businessUnitService.saveBusinessUnitEntity(message);
////            logger.info("BusinessUnit Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateBusinessUnit Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Business Unit from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateBusinessUnit(UpdateBusinessUnitSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            businessUnitService.updateBusinessUnitEntity(message);
////            logger.info("BusinessUnit Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateBusinessUnit Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Pincode from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreatePincode(SavePincodeSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            pincodeService.savePincodeEntity(message);
////            logger.info("Pincode Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreatePincode Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Business Unit from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdatePincode(UpdatePincodeSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            pincodeService.updatePincodeEntity(message);
////            logger.info("Pincode Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdatePincode Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Service Area from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateServiceArea(SaveServiceAreaSharedDataMessge message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            serviceAreaService.saveServiceAreaEntity(message);
////            logger.info("Service Area Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateServiceArea Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Service Area from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateServiceArea(UpdateServiceAreaSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            serviceAreaService.updateServiceAreaEntity(message);
////            logger.info("Service Area Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateServiceArea Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Services from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateServices(SaveServicesSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            planServiceService.savePlanServiceEntity(message);
////            logger.info("Services Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateServices Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Services from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateServices(UpdateServicesSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            planServiceService.updatePlanServiceEntity(message);
////            logger.info("Services Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateServices Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Partner from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreatePartner(SavePartnerSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            partnerService.savePartnerEntiry(message);
////            logger.info("Partner Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreatePartner Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Partner from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdatePartner(UpdatePartnerSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            partnerService.updatePartnerEntiry(message);
////            logger.info("Partner Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdatePartner Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Tax from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TAX_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateTax(SaveTaxSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            taxService.saveTaxEntity(message);
////            logger.info("Tax Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateTax Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Tax from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TAX_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateTax(UpdateTaxSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            taxService.updateTaxEntity(message);
////            logger.info("Tax Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateTax Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Postpaid Plan from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreatePostPaidPlan(SavePlanSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            postpaidPlanService.savePostPaidPlanEntity(message);
////            logger.info("Postpaid plan Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreatePostPaidPlan Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Postpaid Plan from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdatePostPaidPlan(UpdatePlanSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            postpaidPlanService.updatePostPaidPlanEntity(message);
////            logger.info("Postpaid plan Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdatePostPaidPlan Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Plangroup from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreatePlanGroup(SavePlanGroupSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            planGroupService.savePlanGroupEntity(message);
////            logger.info("Plangroup Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreatePlanGroup Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update PlanGroup from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdatePlanGroup(UpdatePlanGroupSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            planGroupService.updatePlanGroupEntity(message);
////            logger.info("PlanGroup Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdatePlanGroup Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Charge from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateCharge(SaveChargeSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            chargeService.saveChargeEntity(message);
////            logger.info("Charge Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateCharge Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Charge from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateCharge(UpdateChargeSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            chargeService.updateChargeEntity(message);
////            logger.info("Charge Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateCharge Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Customer from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateCustomer(SaveCustomerDataShareMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            customerService.saveCustomers(message);
////            logger.info("Customer Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateCustomer Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Customer from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateCustomer(UpdateCustomerShareDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            customerService.updateCustomers(message);
////            logger.info("Customer Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateCustomer Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Teams from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateTeams(SaveTeamsSharedSharedData message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            teamsService.saveTeams(message);
////            logger.info("Teams Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateTeams Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Teams from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateTeams(UpdateTeamsSharedData message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            teamsService.updateTeams(message);
////            logger.info("Teams Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateTeams Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Hierarchy from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateHierarchy(SaveHierarchyShareDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            hierarchyService.saveHierachy(message);
////            logger.info("Hierarchy Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateHierarchy Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Hierarchy from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateHierarchy(UpdateHierarchyShareDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            hierarchyService.updateHierarchy(message);
////            logger.info("Hierarchy Updated Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateHierarchy Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    // Product From RMS
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_PRODUCT_FROM_RMS)
////    public void productFromRms(ProductDto message){
////        logger.info("Received Message From RabbitMq : <" + message + ">");
////        try {
////            productService.saveEntityFromRms(message);
////            logger.info("Product Created Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_INVENTORY)
////    public void receiveMessageCafToCustomer(CAFCustomerStatusMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            customerService.saveCafToCustomer(message);
////            logger.info("Convert Caf To Customer Successfully From RabbitMq Message");
////        } catch (Exception e) {
////            logger.error("receiveMessageCafToCustomer Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Cas Master
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CASMASTER_CREATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageCreateCasMaster(SaveCasMasterSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            casMasterService.saveCasMasterEntity(message);
////            logger.info("Cas Master Updated Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateCasMaster Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY)
////    public void receiveMessageUpdateCasMaster(UpdateCasMasterSharedDataMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            casMasterService.updateCasMasterEntity(message);
////            logger.info("Cas Master Updated Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateCasMaster Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Create Client Service from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
////    public void receiveMessageCreateClientService(SaveClientServMessge message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            clientServiceService.saveSharedClientService(message);
////            logger.info("Client Service Created Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageCreateClientService Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////    //Update Client Service from RabbitMQ
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_INVENTORY)
////    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            clientServiceService.updateSharedClientService(message);
////            logger.info("Client Service Updated Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////    }
////
////
////
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_INVENTORY)
////    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            roleService.saveRoles(message);
////            logger.info("Role Created Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////
////    }
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_INVENTORY)
////    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            roleService.deleteRole(message);
////            logger.info("Role Updated Successfully From Rms");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////
////    }
////
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY)
////    public void receiveMessageStatusUpdateFromCMS(CustomerInventoryMappingMessage message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            CustomerInventoryMappingService inventoryMappingService = SpringContext.getBean(CustomerInventoryMappingService.class);
////            inventoryMappingService.updateCustomerInvStatusFromCMS(message);
////            logger.info("Customer Inventory Update Successfully From CMS");
////        } catch (Exception e) {
////            logger.error("receiveMessageStatusUpdateFromCMS Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////
////    }
////    @Transactional
////    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_INVENTORY_ISP)
////    public void receiveMessageMvnoIdUpdateFormCommon(UpdateMvnoData message) {
////        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
////        try {
////            mvnoService.updateMvnoIdIsptoIsp(message.getOldmvnoId(),message.getNewmvnoId());
////            logger.info("Mvno Updated Successfully From Common");
////        } catch (Exception e) {
////            logger.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
////            throw new RuntimeException(e);
////        }
////
////    }
//}
