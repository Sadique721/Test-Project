/*
package com.savbill.ticketmanagement.rabbitmq;

import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.CommanRabbitCall;
import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Constants.RabbitCallConstants;
import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.Messages.ActivePlanListRespMessage;
import com.savbill.ticketmanagement.core.modules.Area.service.AreaService;
import com.savbill.ticketmanagement.core.modules.Branch.service.BranchService;
import com.savbill.ticketmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.ticketmanagement.core.modules.BusinessUnit.service.BusinessUnitService;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.Service.BusinessVerticalsService;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.ticketmanagement.core.modules.City.service.CityService;
import com.savbill.ticketmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.ticketmanagement.core.modules.Country.service.CountryService;
import com.savbill.ticketmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.ticketmanagement.core.modules.EmailConfig.service.EmailConfigService;
import com.savbill.ticketmanagement.core.modules.Mvno.domain.UpdateMvnoData;
import com.savbill.ticketmanagement.core.modules.Mvno.service.MvnoService;
import com.savbill.ticketmanagement.core.modules.Partner.service.PartnerService;
import com.savbill.ticketmanagement.core.modules.Pincode.service.PincodeService;
import com.savbill.ticketmanagement.core.modules.Plan.service.PostPaidPlanService;
import com.savbill.ticketmanagement.core.modules.PlanService.domain.PlanService;
import com.savbill.ticketmanagement.core.modules.PlanService.service.PlanServicesService;
import com.savbill.ticketmanagement.core.modules.Region.service.RegionService;
import com.savbill.ticketmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.ticketmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.ticketmanagement.core.modules.State.service.StateService;
import com.savbill.ticketmanagement.core.modules.Teams.service.HierarchyService;
import com.savbill.ticketmanagement.core.modules.Teams.service.TeamsService;
import com.savbill.ticketmanagement.core.modules.role.service.RoleService;
import com.savbill.ticketmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.ticketmanagement.core.modules.tickets.service.CaseService;
import com.savbill.ticketmanagement.rabbitmq.messages.CloseTicketCheckMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.CommonRoleMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.*;
import com.savbill.ticketmanagement.rabbitmq.messages.EmailConfigSendToAPIGWMsg;
import com.savbill.ticketmanagement.rabbitmq.messages.TicketETRAuditMessage;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MessageReceiver {
 */
/*   private static Log log = LogFactory.getLog(MessageReceiver.class);

    @Autowired
    CommanRabbitCall commanRabbitCall;

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



    @Transactional
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
//    @Transactional
//    @RabbitListener(queues = RabbitCallConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET)
//    public void receiveMessageForCountryCreation(CountrySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//           countryService.saveCountry(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
//        }
//
//    }


//    @RabbitListener(queues = RabbitCallConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET)
//    public void receiveMessageForStateCreation(StateSharedDataMessage message) {
//        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            stateService.saveStateEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for State Creation :"+e.getMessage());
//        }
//
//    }


//    @RabbitListener(queues = RabbitCallConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET)
//    public void receiveMessageForCityCreation(CitySharedDataMessage message) {
//        log.info("Received Message From RabbitMq For City Creation, receiveMessage : <" + message + ">");
//        System.out.println("Message : " + message);
//        try {
//            cityService.saveCityEntity(message);
//        }
//        catch(Exception e) {
//            log.info("receiveMessageCustomerApigw Failed for City Creation :"+e.getMessage());
//        }
//
//    }




    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForCountryUpdation(UpdateCountrySharedDataMessage message) {
        log.info("Received Message From RabbitMq For Country Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            countryService.updateCountry(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Country Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForStateUpdation(UpdateStateSharedDataMessage message) {
        log.info("Received Message From RabbitMq For State Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            stateService.updateStateEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for State Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForCityUpdation(UpdateCitySharedDataMessage message) {
        log.info("Received Message From RabbitMq For City Update receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            cityService.updateCityEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for City Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForPincodeCreate(SavePincodeSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Pincode Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            pincodeService.savePincode(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Pincode Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForPincodeUpdate(UpdatePincodeSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Pincode Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            pincodeService.updatePincode(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Pincode Update :"+e.getMessage());
        }

    }


    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForAreaCreate(SaveAreaSharedDataMessage message) {
        log.info("Received Message From RabbitMq For area Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            areaService.saveAreaEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for area Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForAreaUpdate(UpdateAreaSharedDataMessage message) {
        log.info("Received Message From RabbitMq For area Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            areaService.updateAreaEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for area Update :"+e.getMessage());
        }

    }


    //service area share
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForServiceAreaCreate(SaveServiceAreaSharedDataMessge message) {
        log.info("Received Message From RabbitMq For service area Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
             serviceAreaService.saveServiceArea(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for service area Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForServiceAreaUpdate(UpdateServiceAreaSharedDataMessage message) {
        log.info("Received Message From RabbitMq For service area  Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            serviceAreaService.updateServiceArea(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for service area Update :"+e.getMessage());
        }

    }

    //business unit share
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForBusinessUnitCreate(SaveBusinessUnitSharedDataMessage message) {
        log.info("Received Message From RabbitMq For business unit Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            businessUnitService.saveBusineeUnit(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for business unit Creation :"+e.getMessage());
        }

    }

    @Transactional
@RabbitListener(queues = RabbitCallConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForBusinessUnitUpdate(UpdateBusinessUnitSharedDataMessage message) {
        log.info("Received Message From RabbitMq For business unit Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            businessUnitService.updateBusinessUnit(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for business unit Update :"+e.getMessage());
        }

    }


    //Branch
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForBranchCreate(SaveBranchSharedDataMessage message) {
        log.info("Received Message From RabbitMq For branch Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            branchService.saveBranch(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for branch Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForBranchUpdate(UpdateBranchSharedData message) {
        log.info("Received Message From RabbitMq For branch Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            branchService.updateBranch(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for branch Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForTeamCreate(SaveTeamsSharedSharedData message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            teamsService.saveTeams(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForTeamsUpdate(UpdateTeamsSharedData message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            teamsService.updateTeams(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForHierachyCreate(SaveHierarchyShareDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            hierarchyService.saveHierachy(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @RabbitListener(queues = RabbitCallConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForHierarchyUpdate(UpdateHierarchyShareDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            hierarchyService.updateHierarchy(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    //Ticket Recievers
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_ETR_AUDIT)
    public void receiveFinalTicketETRAudits(TicketETRAuditMessage message) {

        log.info("Received Message From RabbitMq : <" + message + ">");
        try {
            caseService.saveETRAudit(message.getCustomerData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_TICKET_TAT_AUDIT)
    public void receiveFinalTicketTATAudits(TicketETRAuditMessage message) {

        log.info("Received Message From RabbitMq : <" + message + ">");
        try {
            caseService.saveTATAudit(message.getCustomerData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_REGION_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForRegionCreate(SaveRegionSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            regionService.saveRegion(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForRegionUpdate(UpdateRegionSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            regionService.updateRegion(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForBusinessVerticalsCreate(SaveBusinessVerticalSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            businessVerticalsService.saveBusinessVertical(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForBusinessVerticalsUpdate(UpdateBusinessVerticalSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            businessVerticalsService.updateBusinessVertical(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForCustomersCreate(SaveCustomerDataShareMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            customersService.saveCustomers(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET)
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
    @RabbitListener(queues = RabbitCallConstants.QUEUE_STAFF_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForStaffCreate(SaveStaffUserSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            staffUserService.saveStaffuser(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForStaffUpdate(UpdateStaffUserSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            staffUserService.updateStaffUser(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }


    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_ROLE_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForRoleCreate(SaveRoleSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            roleService.saveRoleEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForRoleUpdate(UpdateRoleSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            roleService.updateRoleEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_MVNO_CREATE_DATA_SHARE_TICKET)
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
    @RabbitListener(queues = RabbitCallConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForMvnoUpdate(UpdateMvnoSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            mvnoService.updateMVNOEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForPlanServiceCreate(SaveServicesSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            planService.savePlanServiceEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForPlanServiceUpdate(UpdateServicesSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            planService.updatePlanServiceEntity(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }


    // Partner and Plan
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PLAN_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForPostPaidPlanCreate(SavePlanSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            postPaidPlanService.savePostpaidPlan(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForPostPaidPlanUpdate(UpdatePlanSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            postPaidPlanService.updatePostPaidPlan(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET)
    public void receiveMessageForPartnerServiceCreate(SavePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            partnerService.savePartnerService(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Creation :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET)
    public void receiveMessageForPartnerServiceUpdate(UpdatePartnerSharedDataMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            partnerService.updatePartnerService(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }

    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE)
    public void receiveMessageForClinetServSave(SaveClientServMessge message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            clientServiceSrv.saveClientServData(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }
    }
    @Transactiona
    @RabbitListener(queues = RabbitCallConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE)
    public void receiveMessageForClinetServUpdate(UpdateClientServMessage message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            clientServiceSrv.updateClientServData(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_EMAIL_CONFIG_TO_APIGW)
    public void receiveemailconfigmessage(EmailConfigSendToAPIGWMsg message) {
        log.info("Received email config  Message From RabbitMq is : <" + message + ">");
        try {
            emailConfigService.getEmailconfigFromMessage(message.getEmailConfigData());
        } catch (Exception e) {
            log.info("processing email config message  Failed :" + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitCallConstants.QUEUE_CAF_TO_CUSTOMER_DATA_SHARE_TICKET)
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


    //Create Client Service from RabbitMQ
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
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
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_TICKET)
    public void receiveMessageUpdateClientService(UpdateClientServMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            clientServiceSrv.updateSharedClientService(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_CREATE_DATA_ROLE_TICKET)
    public void receiveMessageRoleCreateFromCMS(CommonRoleMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.saveRole(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }
    @RabbitListener(queues = RabbitMqConstants.QUEUE_SEND_DELETE_DATA_ROLE_TICKET)
    public void receiveMessageRoleDeleteFromCMS(CommonRoleMessage message) {
        log.info("Received Message From RabbitMq receiverMessage : <" + message + ">");
        try {
            roleService.deleteRole(message);
            log.info("Client Service Updated Successfully From Rms");
        } catch (Exception e) {
            log.error("receiveMessageUpdateClientService Failed :" +e.getMessage());
            throw new RuntimeException(e);
        }

    }


    @Transactional
    @RabbitListener(queues = RabbitMqConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_TICKET)
    public void receiveMessageChangePLanTicket(ChangePlanMessage message) {
        log.info("Received Message From RabbitMq For Teams Creation, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            customersService.saveCustomersPlanAndServiceData(message);
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Customers Change Plan And Service :"+e.getMessage());
        }

    }
    @Transactional
    @RabbitListener(queues = RabbitCallConstants.QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_TICKET_ISP)
    public void receiveMessageForMvnoUpdateIps(UpdateMvnoData message) {
        log.info("Received Message From RabbitMq For Teams Update, receiveMessage : <" + message + ">");
        System.out.println("Message : " + message);
        try {
            mvnoService.updateMvnoIsp( message.getOldmvnoId(),  message.getNewmvnoId());
        }
        catch(Exception e) {
            log.info("receiveMessageCustomerApigw Failed for Teams Update :"+e.getMessage());
        }

    }*//*

}
*/
