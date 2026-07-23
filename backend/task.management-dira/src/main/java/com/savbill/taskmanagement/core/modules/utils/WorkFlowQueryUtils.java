package com.savbill.taskmanagement.core.modules.utils;


import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.Area.domain.Area;
import com.savbill.taskmanagement.core.modules.Branch.domain.Branch;
import com.savbill.taskmanagement.core.modules.Branch.repository.BranchRepository;
import com.savbill.taskmanagement.core.modules.BranchServiceArea.domain.BranchServiceAreaMapping;
import com.savbill.taskmanagement.core.modules.BranchServiceArea.repository.BranchServiceAreaMappingRepository;
import com.savbill.taskmanagement.core.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.taskmanagement.core.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.taskmanagement.core.modules.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.savbill.taskmanagement.core.modules.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.taskmanagement.core.modules.City.domain.City;
import com.savbill.taskmanagement.core.modules.CustomerAddress.Service.CustomerAddressService;
import com.savbill.taskmanagement.core.modules.CustomerAddress.domain.CustomerAddress;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Customers.repository.CustomerRepository;
import com.savbill.taskmanagement.core.modules.Partner.domain.Partner;
import com.savbill.taskmanagement.core.modules.Partner.repository.PartnerRepository;
import com.savbill.taskmanagement.core.modules.Pincode.domain.Pincode;
import com.savbill.taskmanagement.core.modules.Plan.domain.CustPlanMappping;
import com.savbill.taskmanagement.core.modules.Plan.domain.PostpaidPlan;
import com.savbill.taskmanagement.core.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.taskmanagement.core.modules.PlanService.repository.PlanServiceRepository;
import com.savbill.taskmanagement.core.modules.Region.domain.Region;
import com.savbill.taskmanagement.core.modules.Region.repository.RegionBranchRepository;
import com.savbill.taskmanagement.core.modules.Region.repository.RegionRepository;
import com.savbill.taskmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.savbill.taskmanagement.core.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.taskmanagement.core.modules.State.domian.State;
import com.savbill.taskmanagement.core.modules.Teams.domain.QueryFieldMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import com.savbill.taskmanagement.core.modules.Teams.domain.Teams;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.savbill.taskmanagement.core.modules.Teams.repository.TeamsRepository;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.dto.StaffUserPojo;
import com.savbill.taskmanagement.core.modules.staffuser.mapper.StaffUserMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.repository.TicketServiceMappingRepo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class
WorkFlowQueryUtils {


//    @Autowired
//    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
//    @Autowired
//    CreditDocRepository creditDocRepository;
//
//    @Autowired
//    PostpaidPlanRepo postpaidPlanRepo;
//
//
//    @Autowired
//    ChargeRepository chargeRepository;
//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @Autowired
    StaffUserMapper staffUserMapper;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    RegionBranchRepository regionBranchRepository;
//
//    @Autowired
//    SubscriberMapper subscriberMapper;
//    @Autowired
//    BusinessVerticalMappingRepository businessVerticalMappingRepository;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    @Autowired
    TeamsRepository teamsRepository;
//    @Autowired
//    PlanServiceRepository planServiceRepository;
//
//    @Autowired
//    PlanGroupService planGroupService;
//
//    @Autowired
//    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    TicketServiceMappingRepo ticketServiceMappingRepo;

    @Autowired
    CustomerRepository customersRepository;


    @Autowired
    CaseService caseService;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    PostpaidPlanRepo  postpaidPlanRepo;

    @Autowired
    CustomerAddressService customerAddressService;


    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;


    @Autowired
    BranchRepository branchRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    BusinessVerticalsRepository businessVerticalsRepository;

    @Autowired
    PlanServiceRepository planServiceRepository;

//    @Autowired
//    DebitDocRepository debitDocRepository;
//
//    @Autowired
//    CustomerDocDetailsService customerDocDetailsService;

//    @Autowired
//    AreaService areaService;
//    @Autowired
//    PincodeService pincodeService;
//    @Autowired
//    CityService cityService;
//    @Autowired
//    StateService stateService;

    @Autowired
    private CustomersService customersService;

//    @Autowired
//    PostpaidPlanService postpaidPlanService;
//    @Autowired
//    private DbrService dbrService;

//    @Autowired
//    ServiceAreaService serviceAreaService;
//
//    @Autowired
//    CustomerAddressService customerAddressService;
//
//    @Autowired
//    CaseService caseService;
//
//
//    @Autowired
//    ChargeService chargeService;
//
//    @Autowired
//    CustomerDBRRepository customerDBRRepository;
//
//    @Autowired
//    CustMacMapppingService custMacMapppingService;
//
//    @Autowired
//    private CustMacMapppingRepository custMacMapppingRepository;
//
//    @Autowired
//    private CreditDocService creditDocService;
//
//    @Autowired
//    private CustomerDBRService customerDBRService;
//
//    @Autowired
//    private ServiceRepository serviceRepository;
//
//    @Autowired
//    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;
//
//    @Autowired
//    CustomerAddressRepository customerAddressRepository;
//
//    @Autowired
//    PartnerPaymentRepository partnerPaymentRepository;
//
//    @Autowired
//    CustomerDocDetailsRepository customerDocDetailsRepository;
//
//    @Autowired
//    LeadMasterRepository leadMasterRepository;
//    @Autowired
//    LeadCustomerAddressRepository leadCustomerAddressRepository;
//
//    @Autowired
//    private CreditDebtMappingRepository creditDebtMappingRepository;
//
//    @Autowired
//    WorkflowAuditService workflowAuditService;
//    @Autowired
//    private StaffUserRepository staffUserRepository;
//
//    @Autowired
//    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
//
//    @Autowired
//    private BranchRepository branchRepository;
//
//    @Autowired
//    private StaffUserServiceRepository staffUserServiceRepository;
//
//    @Autowired
//    CustomerApproveRepo customerApproveRepo;
//    @Autowired
//    private RegionRepository regionRepository;
//    @Autowired
//    private BusinessVerticalsRepository businessVerticalsRepository;
//    @Autowired
//    private PlanGroupRepository planGroupRepository;
//    @Autowired
//    private BusinessUnitRepository businessUnitRepository;
//    @Autowired
//    private TicketReasonCategoryRepo ticketReasonCategoryRepo;
//
//    @Autowired
//    private CommonListRepository commonListRepository;
//
//    @Autowired
//    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;
//
//    @Autowired
//    SubscriberService subscriberService;
//
//    @Autowired
//    CommonListService commonListService;
//    @Autowired
//    CustomerMapper customerMapper;
//
//    @Autowired
//    TrialDebitDocRepository trialDebitDocRepository;
//    @Autowired
//    private CustPlanMappingRepository custPlanMapppingRepository;
//
//    @Autowired
//    private CustomerServiceMappingRepository customerServiceMappingRepository;
//    @Autowired
//    InOutWardMacRepo inOutWardMacRepo;
//    @Autowired
//    CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;
//    @Autowired
//    LeadQuotationDetailsRepository leadQuotationDetailsRepository;
    //    @Autowired
//    CustomerMapper customerMapper;
//    @Autowired
//    TrialDebitDocRepository trialDebitDocRepository;
//    @Autowired
//    PartnerRepository partnerrepo;
//
//    @Autowired
//    PartnerLedgerService partnerLedgerService;
//
//    @Autowired
//    PartnerLedgerDetailsService partnerLedgerDetailsService;
//
//    @Autowired
//    private PartnerPaymentMapper partnerPaymentMapper;
//    @Autowired
//    CustPlanMappingService custPlanMappingService;
//    @Autowired
//    EzBillServiceUtility ezBillServiceUtility;
//
//    @Autowired
//    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
//    @Autowired
//    CaseRepository caseRepository;
//
//    @Autowired
//    LeadCustPlanMapppingRepository leadCustPlanMapppingRepository;
//    @Autowired
//    ShiftLocationRepository shiftLocationRepository;



//    private Boolean checkTicketCondition(Object entity, List<QueryFieldMapping> queryFieldMappingList, boolean condition, StringBuilder queryInit, ScriptEngine engine) {
//        Boolean result=false;
//        if (entity instanceof CaseDTO) {
//            CaseDTO caseDTO = (CaseDTO) entity;
//            Customers ticketCustomer = customersService.get(caseDTO.getCustomersId());
//            Partner ticketCustomerPartner = partnerRepository.findById(ticketCustomer.getParnterId()).orElse(null);
//            CustomerAddress ticketCustomerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", ticketCustomer.getId());
//            for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
//                switch (queryFieldMapping.getQueryField()) {
//                    case CommonConstants.CASE_CONDITION.PLAN_PURCHASE_TYPE: {
//                        if (ticketCustomer.getPlanPurchaseType() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//                        break;
//
//                    }
//                    case CommonConstants.CASE_CONDITION.PLAN_MODE: {
//                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
//                            if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.SERVICE_AREA: {
//                        ServiceArea serviceArea = serviceAreaService.getByID(ticketCustomer.getServicearea().getId());
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CALENDAR_TYPE: {
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CATEGORY: {
//                        if (ticketCustomer.getDunningCategory() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                            break;
//                        }
//                    }
//                    case CommonConstants.CASE_CONDITION.PARTNER_NAME: {
//                        if (ticketCustomerPartner != null) switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//
//
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PARTNER_EMAIL: {
//                        if (ticketCustomerPartner != null && ticketCustomerPartner.getEmail() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//                        break;
//
//                    }
//                    case CommonConstants.CASE_CONDITION.AREA: {
//                        Area area = ticketCustomerAddress.getArea();
//                        switch (queryFieldMapping.getQueryOperator()) {
//
//                            case "==":
//                                condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PINCODE: {
//                        Pincode pincode = ticketCustomerAddress.getPincode();
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CITY: {
//                        City city = ticketCustomerAddress.getCity();
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.STATE: {
//                        State state = ticketCustomerAddress.getState();
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.BILL_TO: {
//                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.INVOICE_TO_ORG: {
//                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PARENT_CUSTOMER_USERNAME: {
//                        if (ticketCustomer.getParentCustomers() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.USERNAME: {
//                        switch (queryFieldMapping.getQueryOperator()) {
//                            case "==":
//                                condition = ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//                            case "!=":
//                                condition = !ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                break;
//
//
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PLAN_SERVICES: {
//                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                        if (caseDTO.getTeamHierarchyMappingId() != null) {
//                            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Math.toIntExact(caseDTO.getTeamHierarchyMappingId())).orElse(null);
//                            if (teamHierarchyMapping != null) {
//                                Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//
//                            }
//                        }
//                        break;
//                    }
//
//                    case CommonConstants.PAYMENT_CONDITION.CUSTOMER_CATEGORY: {
//                        if (ticketCustomer.getDunningCategory() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                        }
//                        break;
//                    }
////                        case CommonConstants.CASE_CONDITION.CURRENT_PARTNER:
////
////                            break;
//                    case CommonConstants.CASE_CONDITION.TICKET_CATEGORY: {
//                        if (caseDTO.getCaseReasonCategory() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==": {
//                                    condition = caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                    break;
//                                }
//                                case "!=": {
//                                    condition = !caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                    break;
//                                }
//
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.TICKET_SUB_CATEGORY: {
//                        if (caseDTO.getCaseReasonSubCategory() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                    break;
//                                case "!=":
//                                    condition = !caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                    break;
//                            }
//
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PRIORITY: {
//                        if (caseDTO.getPriority() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//                        break;
//
//                    }
//                    case CommonConstants.CASE_CONDITION.DEPARTMENT: {
//                        if (caseDTO.getDepartment() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==": {
//                                    condition = caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                }
//                                case "!=": {
//                                    condition = !caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.BRANCH: {
//                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                        if (queryFieldMapping.getQueryOperator() != null) {
//                            Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                            if (branchServiceAreaMappings.size() > 0 && branch != null) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                        break;
//                                    }
//                                    case "!=": {
//                                        condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.REGION: {
//                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                        if (queryFieldMapping.getQueryOperator() != null) {
//                            Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                            if (branchServiceAreaMappings.size() > 0 && region != null) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                        break;
//                                    }
//                                    case "!=": {
//                                        condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.BUSINESS_VERTICAL: {
//                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                        if (queryFieldMapping.getQueryOperator() != null) {
//                            BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                            if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        for (Region region : businessVerticals.getBuregionidList()) {
//                                            condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                            break;
//                                        }
//                                    }
//                                    case "!=": {
//                                        for (Region region : businessVerticals.getBuregionidList()) {
//                                            condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PLAN_GROUP: {
////                                if (ticketCustomer.getPlangroup() != null) {
////                                    PlanGroup planGroup = ticketCustomer.getPlangroup();
////                                    if (planGroup != null) {
////                                        switch (queryFieldMapping.getQueryOperator()) {
////                                            case "==": {
////                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            }
////                                            case "!=": {
////                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            }
////                                        }
////                                    }
////                                }
////                                break;
//                        for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
//                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                            if (postpaidPlan != null) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                    case "!=": {
//                                        condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.PLAN_CATEGORY: {
//                        for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
//                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                            if (postpaidPlan != null) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                    case "!=": {
//                                        condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.FEASIBILITY_REQUIRED: {
//                        if (ticketCustomer.getFeasibilityRequired() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==": {
//
//                                    condition = ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                }
//
//                                case "!=": {
//                                    condition = !ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                }
//
//                            }
//
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.BU: {
//                        if (ticketCustomer.getBuId() != null) {
//                            BusinessUnit businessUnit = businessUnitRepository.findById(ticketCustomer.getBuId()).orElse(null);
//                            if (businessUnit != null) {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==": {
//                                        condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                    case "!=": {
//                                        condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    }
//                                }
//                            }
//
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.VALLEY_TYPE: {
//                        if (ticketCustomer.getValleyType() != null) {
//                            String valleyType = ticketCustomer.getValleyType().replace(" ", "");
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                    break;
//                                case "!=":
//                                    condition = !valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                    break;
////                                        case "!=":
////                                            condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                            break;
//                            }
//                            break;
//                        }
//                    }
//                    case CommonConstants.CASE_CONDITION.CUSTOMER_AREA: {
//                        if (ticketCustomer.getCustomerArea() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CUSTOMER_TYPE: {
//                        if (ticketCustomer != null) {
//                            switch (queryFieldMapping.getQueryOperator()){
//                                case "==":
//                                    condition = ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                        }
//
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.TICKET_STATUS: {
//                        if (caseDTO.getCaseStatus() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.TICKET_RAISED_BY_TEAM: {
//                        List<Long> teams=teamsRepository.findAllByStaff(caseDTO.getCreatedById());
//                        if(teams.size()>0){
//                            List<Teams>teamsList=teamsRepository.findAllByIdIn(teams);
//                            for(Teams teams1:teamsList){
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                            }
//                        }
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.CASE_TYPE: {
//                        if(caseDTO.getCaseType()!=null){
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                        }
//
//                        break;
//                    }
//                    case CommonConstants.CASE_CONDITION.TICCKET_CREATED_DURATION: {
//                        if(caseDTO!=null){
//                            DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                            String localDate =caseDTO.getCreatedate().toLocalDate().format(formatter);
//                            LocalDate localDate1 = LocalDate.parse(localDate, formatter);
//                            LocalDate localDate2 = LocalDate.parse(queryFieldMapping.getQueryValue(), formatter);
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition =localDate1.equals(localDate2);
//                                    break;
//                                case "!=":
//                                    condition = !localDate1.equals(localDate2);
//                                    break;
//                            }
//                        }
//                        break;
//                    }
//                }
//                queryInit.append(condition);
//                if (queryFieldMapping.getQueryCondition().equals("and")) {
//                    queryInit.append("&&");
//                } else if (queryFieldMapping.getQueryCondition().equals("or")) {
//                    queryInit.append("||");
//                }
//            }
//            try {
//                result= (Boolean) engine.eval(queryInit.toString().toLowerCase());
//                return (Boolean) engine.eval(queryInit.toString().toLowerCase());
//            } catch (ScriptException e) {
//                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
//            }
//        }
//        return result;
//    }

    public List<StaffUserPojo> assignCAFToStaffFromTeam(List<ServiceArea> serviceAreaList, Long buId, Teams team) {
        List<StaffUser> tempStaffList = new ArrayList<>();
        Set<StaffUserPojo> returnList;
        Set<StaffUser> staffList = team.getStaffUser();
        staffList = staffList.stream().filter(staffUser -> staffUser.getStatus().equals(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toSet());
        if (staffList != null && staffList.size() > 0) {
            for (StaffUser staff : staffList) {
                if (!staff.getIsDelete() && staff.getStatus().equalsIgnoreCase("Active")) {
                    if (staff.getServiceAreaNameList() != null && staff.getServiceAreaNameList().size() > 0) {
                        for (ServiceArea serviceArea : staff.getServiceAreaNameList()) {
                            if (serviceAreaList.stream().anyMatch(serviceArea1 -> serviceArea1.getId().equals(serviceArea.getId()))) {
                                if (staff.getBusinessUnitNameList().size() > 0) {
                                    if (buId != null && buId != 0) {
                                        for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                            if (buId.equals(businessUnit.getId())) {
                                                if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                                    tempStaffList.add(staff);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    tempStaffList.add(staff);
                                }
                            }
                        }
                    } else if (staff.getServiceAreaNameList().size() == 0) {
                        if (staff.getBusinessUnitNameList().size() > 0) {
                            if (buId != null && buId != 0) {
                                for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                    if (buId == businessUnit.getId().longValue()) {
                                        if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                            tempStaffList.add(staff);
                                        }
                                    }
                                }
                            }
                        } else {
                            tempStaffList.add(staff);
                        }

                    }
                }
            }
        }
        returnList = tempStaffList.stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toSet());
        List<StaffUserPojo> staffUserList = returnList.stream().collect(Collectors.toList());
        return staffUserList;
    }



    public int assignStaffFromList(List<StaffUserPojo> staffList, String eventName, Object entity) {
        int staffId = 0;
        Long count;
        if (staffList.size() > 0) {
            HashMap<Integer, Long> countListMap = new HashMap<>();
            for (StaffUserPojo staffUserTemp : staffList) {
                   if (entity instanceof CaseDTO && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
                    count = caseService.findMinimumAssignReuqestByStaff(staffUserTemp.getId());
                    if (count != null) {
                        countListMap.put(staffUserTemp.getId(), count);
                    }
                }
            }
            if (countListMap.values().size() == 0) {
                return staffId;
            } else {
                Long minValueInMap = Collections.min(countListMap.values());
                // This will return min value in the HashMap
                for (Map.Entry<Integer, Long> entry : countListMap.entrySet()) {  // Iterate through HashMap
                    if (Objects.equals(entry.getValue(), minValueInMap)) {
                        staffId = entry.getKey();     // staff id with minimum reuqest
                    }
                }
                if (countListMap.size() > 0 && staffId != 0) {
                    return staffId;
                }
            }
        }

        return staffId;
    }


//    @Transactional
//    public void closeAllPendingTAsks(CustomersPojo custTerminate) {
//        Customers customers=customerMapper.dtoToDomain(custTerminate,new CycleAvoidingMappingContext());
//        QCustomers qCustomers=QCustomers.customers;
//        QCustomerAddress qCustomerAddress=QCustomerAddress.customerAddress;
//      //  BooleanExpression booleanExpression=qCustomers.isNotNull().and(qCustomers.id.eq(qCustomerAddress.customer.id).and(qCustomerAddress.status.equalsIgnoreCase("NewActivation")));
//        BooleanExpression booleanExpression=qCustomerAddress.isNotNull().and(qCustomerAddress.customer.id.eq(custTerminate.getId())).and(qCustomerAddress.status.equalsIgnoreCase("NewActivation"));
//        List<CustomerAddress> customerAddress=IterableUtils.toList(customerAddressRepository.findAll(booleanExpression));
//        if(customerAddress.size()>0) {
//            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Approve Customer address before termination.", new Throwable());
//        }
//        List<Case>caseList=caseRepository.findAllByCustomers_IdAndIsDeleteIsFalseOrderByCaseIdDesc(custTerminate.getId());
//        if(caseList.size()>0){
//            for(Case case1:caseList){
//                if(case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN) || case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_ON_HOLD)||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)
//                        ||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
//                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Close All Tickets before Termination.", new Throwable());
//                }
//
//            }
//
//        }
//        List<CustomerInventoryMapping> customerInventoryMappingList=customerInventoryMappingRepo.findAllByCustomerAndStatus(customers,"PENDING");
//            if(customerInventoryMappingList.size()>0){
//                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Inventory Before Termination.", new Throwable());
//
//            }
//            List<CustomerServiceMapping> customerServiceMapping=customerServiceMappingRepository.findByCustId(custTerminate.getId());
//            if(customerServiceMapping.size()>0){
//                for (CustomerServiceMapping customerServiceMappinglist: customerServiceMapping ){
//                    if(customerServiceMappinglist.getStatus().equalsIgnoreCase("PENDING")){
//                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Customer Discount Before Termination.", new Throwable());
//                    }
//                }
//            }
//            List<CreditDocument>creditDocumentList=creditDocRepository.findAllByCustId(custTerminate.getId());
//            if(creditDocumentList.size()>0) {
//                for (CreditDocument creditDocument : creditDocumentList) {
//                    if (creditDocument.getStatus().equalsIgnoreCase("PENDING")) {
//                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Credit Doc Before Termination.", new Throwable());
//                    }
//                }
//            }
//            List<DebitDocument>debitDocumentList=debitDocRepository.findAllByCustomer(customers);
//            if(debitDocumentList.size()>0) {
//                for (DebitDocument deditDocument : debitDocumentList) {
//                    if (deditDocument.getStatus() != null) {
//                        if (deditDocument.getStatus().equalsIgnoreCase("PENDING")) {
//                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Debit Doc Before Termination.", new Throwable());
//                        }
//                    }
//                }
//            }
//    }

//    @Transactional
//    public void closeALLTickets(CustomersPojo custTerminate) {
//        List<Case>caseList=caseRepository.findAllByCustomers_IdAndIsDeleteIsFalseOrderByCaseIdDesc(custTerminate.getId());
//        if(caseList.size()>0){
//            for(Case case1:caseList){
//                if(case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN) || case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP)||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_ON_HOLD)||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS)
//                        ||case1.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
//                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Close All Tickets before Termination.", new Throwable());
//                }
//
//            }
//
//        }
//
//    }

    public Boolean checkCondition(List<QueryFieldMapping> queryFieldMappingList, String eventName, Object entity){
        StringBuilder queryInit = new StringBuilder();
        boolean condition = false;
        DecimalFormat df = new DecimalFormat("0.00");
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        switch (eventName) {
            case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
                return checkTicketCondition(entity,queryFieldMappingList,condition,queryInit,engine);
            }
        }
        return false;
    }


    private Boolean checkTicketCondition(Object entity, List<QueryFieldMapping> queryFieldMappingList, boolean condition, StringBuilder queryInit, ScriptEngine engine) {
        Boolean result=false;
        if (entity instanceof CaseDTO) {
            ScriptEngineManager mgr = new ScriptEngineManager();
            engine = mgr.getEngineByName("JavaScript");
            CaseDTO caseDTO = (CaseDTO) entity;
            Customers ticketCustomer = null;
            if (Objects.nonNull(ticketCustomer)) {
                //customersService.get(caseDTO.getCustomersId())
                Partner ticketCustomerPartner = partnerRepository.findById(ticketCustomer.getParnterId()).orElse(null);
                CustomerAddress ticketCustomerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", ticketCustomer.getId());
                //List<TicketServicemapping> ticketServiceList = caseDTO.getTicketServicemappingList();
                for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                    switch (queryFieldMapping.getQueryField()) {
//                    case CommonConstants.CASE_CONDITION.PLAN_PURCHASE_TYPE: {
//                        if (ticketCustomer.getPlanPurchaseType() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//                        break;
//
//                    }
                        case CommonConstants.CASE_CONDITION.PLAN_MODE: {
                            for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.SERVICE_AREA: {
                            ServiceArea serviceArea = serviceAreaService.getByID(Long.valueOf(ticketCustomer.getServiceAreaId()));
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.CALENDAR_TYPE: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.CATEGORY: {
                            if (ticketCustomer.getDunningCategory() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;

                                }
                                break;
                            }
                        }
                        case CommonConstants.CASE_CONDITION.PARTNER_NAME: {
                            if (ticketCustomerPartner != null) switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;


                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PARTNER_EMAIL: {
                            if (ticketCustomerPartner != null && ticketCustomerPartner.getEmail() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            break;

                        }
                        case CommonConstants.CASE_CONDITION.AREA: {
                            Area area = ticketCustomerAddress.getArea();
                            switch (queryFieldMapping.getQueryOperator()) {

                                case "==":
                                    condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PINCODE: {
                            Pincode pincode = ticketCustomerAddress.getPincode();
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.CITY: {
                            City city = ticketCustomerAddress.getCity();
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.STATE: {
                            State state = ticketCustomerAddress.getState();
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.BILL_TO: {
                            for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.INVOICE_TO_ORG: {
                            for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PARENT_CUSTOMER_USERNAME: {
                            if (ticketCustomer.getParentCustUsername() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomer.getParentCustUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomer.getParentCustUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.USERNAME: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;


                            }
                            break;
                        }
//                    case CommonConstants.CASE_CONDITION.PLAN_SERVICES: {
//
//                        for (TicketServicemapping ticketServicemapping : ticketServiceList) {
//                            PlanService planService = planServiceRepository.findById(ticketServicemapping.getServiceid().intValue()).orElse(null);
//                            if(planService!=null){
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = planService.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !planService.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                if(condition==true) break;
//                            }
//                        }
//                        break;
//                    }
                        case CommonConstants.CASE_CONDITION.CURRENT_TEAM_ASSIGNED: {
                            if (caseDTO.getTeamHierarchyMappingId() != null) {
                                TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Math.toIntExact(caseDTO.getTeamHierarchyMappingId())).orElse(null);
                                if (teamHierarchyMapping != null) {
                                    Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                    if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }

                                }
                            }
                            break;
                        }
                        case CommonConstants.PAYMENT_CONDITION.CUSTOMER_CATEGORY: {
                            if (ticketCustomer.getDunningCategory() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;

                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.TICKET_CATEGORY: {
                            if (caseDTO.getCaseCategoryName() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = caseDTO.getCaseCategoryName().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !caseDTO.getCaseCategoryName().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                        break;
                                    }

                                }
                            }
//                        if(condition==false){
//                            return false;
//                        }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.TICKET_SUB_CATEGORY: {
                            if (caseDTO.getCaseSubCategoryName() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = caseDTO.getCaseSubCategoryName().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                        break;
                                    case "!=":
                                        condition = !caseDTO.getCaseSubCategoryName().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                        break;
                                }

                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PRIORITY: {
                            if (caseDTO.getPriority() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }
                            break;

                        }
                        case CommonConstants.CASE_CONDITION.DEPARTMENT: {
                            if (caseDTO.getDepartment() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.BRANCH: {
                            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServiceAreaId().longValue());
                            if (queryFieldMapping.getQueryOperator() != null) {
                                Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                            break;
                                        }
                                        case "!=": {
                                            condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.REGION: {
                            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServiceAreaId().longValue());
                            if (queryFieldMapping.getQueryOperator() != null) {
                                Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                if (branchServiceAreaMappings.size() > 0 && region != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                            break;
                                        }
                                        case "!=": {
                                            condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.BUSINESS_VERTICAL: {
                            List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServiceAreaId().longValue());
                            if (queryFieldMapping.getQueryOperator() != null) {
                                BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            for (Region region : businessVerticals.getBuregionidList()) {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                        case "!=": {
                                            for (Region region : businessVerticals.getBuregionidList()) {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PLAN_GROUP: {
//                                if (ticketCustomer.getPlangroup() != null) {
//                                    PlanGroup planGroup = ticketCustomer.getPlangroup();
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
                            for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
                                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                if (postpaidPlan != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.PLAN_CATEGORY: {
                            for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
                                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                if (postpaidPlan != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.FEASIBILITY_REQUIRED: {
                            if (ticketCustomer.getFeasibilityRequired() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {

                                        condition = ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }

                                    case "!=": {
                                        condition = !ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }

                                }

                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.BU: {
                            if (ticketCustomer.getBuId() != null) {
                                BusinessUnit businessUnit = businessUnitRepository.findById(ticketCustomer.getBuId()).orElse(null);
                                if (businessUnit != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }

                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.VALLEY_TYPE: {
                            if (ticketCustomer.getValleyType() != null) {
                                String valleyType = ticketCustomer.getValleyType().replace(" ", "");
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                        break;
                                    case "!=":
                                        condition = !valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                        break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
                                }
                                break;
                            }
                        }
                        case CommonConstants.CASE_CONDITION.CUSTOMER_AREA: {
                            if (ticketCustomer.getCustomerArea() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }
                            break;
                        }
                        case CommonConstants.CASE_CONDITION.CUSTOMER_TYPE: {
                            if (ticketCustomer != null && Objects.nonNull(ticketCustomer.getCustcategory())) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;

                                }
                            }

                            break;
                        }
                        case CommonConstants.CASE_CONDITION.TICKET_STATUS: {
                            if (caseDTO.getCaseStatus() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }
                            break;
                        }
//                    case CommonConstants.CASE_CONDITION.TICKET_RAISED_BY_TEAM: {
//                        List<Long> teams=teamsRepository.findAllByStaff(caseDTO.getCreatedById());
//                        if(teams.size()>0){
//                            List<Teams>teamsList=teamsRepository.findAllByIdIn(teams);
//                            for(Teams teams1:teamsList){
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                            }
//                        }
//                        break;
//                    }
                        case CommonConstants.CASE_CONDITION.CASE_TYPE: {
                            if (caseDTO.getCaseType() != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                            }

                            break;
                        }
//                    case CommonConstants.CASE_CONDITION.TICCKET_CREATED_DURATION: {
//                        if(caseDTO!=null){
//                            DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                            String localDate =caseDTO.getCreatedate().toLocalDate().format(formatter);
//                            LocalDate localDate1 = LocalDate.parse(localDate, formatter);
//                            LocalDate localDate2 = LocalDate.parse(queryFieldMapping.getQueryValue(), formatter);
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition =localDate1.equals(localDate2);
//                                    break;
//                                case "!=":
//                                    condition = !localDate1.equals(localDate2);
//                                    break;
//                            }
//                        }
//                        break;
//                    }
                    }
                    queryInit.append(condition);
                    if (queryFieldMapping.getQueryCondition().equals("and")) {
                        queryInit.append("&&");
                    } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                        queryInit.append("||");
                    }
                }
                try {
                    result = (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                } catch (ScriptException e) {
                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                }
            }
            return result;
        }return  null;
    }


































}
