package com.savbill.commonGateway.moules.CommonList.controller;


import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.CommonList.model.CommonListDTO;
import com.savbill.commonGateway.moules.CommonList.service.CommonListService;
import com.savbill.commonGateway.moules.CommonList.utils.TypeConstants;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.COMMON_LIST)
public class CommonListController extends ExBaseAbstractController<CommonListDTO> {
private static Logger logger = LoggerFactory.getLogger(CommonListController.class);
    @Autowired
    private CommonListService commonListService;
    private static String MODULE = " [CommonListController] ";
    @Autowired
    private CacheManager cacheManager;

    public CommonListController(CommonListService service) {
        super(service);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TITLE)
    public GenericDataDTO getCommonListByTitle() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByTitle]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
    //    logger.info(SUB_MODULE, "Get Common List by title " );
        MDC.remove("type");
        return getCommonListByTypeSorted(TypeConstants.TITLE);
    }

    @GetMapping(UrlConstants.AUDIT_FOR)
    public GenericDataDTO getCommonListByAuditFor() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByAuditFor]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("GET Common list by Author is Successfull  :  request: {};response:{}" ,SUB_MODULE, APIConstants.SUCCESS);

        try {
       //     ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListForAudit(TypeConstants.AUDIT_FOR);
            List<CommonListDTO> sortedList = list.stream()
                    .sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed())
                    .collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            logger.info("GET Common list by Author is Successfull  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
            return genericDataDTO;
        } catch (Exception ex) {
        //    ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to search :  request: {Sub-Module:{};Exception:{}}",SUB_MODULE,ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @ApiIgnore
    @GetMapping(UrlConstants.NETWORK)
    public GenericDataDTO getCommonListByNetwork() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByNetwork]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Network  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.NETWORK);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INTERCOM_GROUP)
    public GenericDataDTO getCommonListByIntercomGroup() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByIntercomGroup]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by IntercomGroup  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INTERCOM_GROUP);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.VOICE_SERVICE)
    public GenericDataDTO getCommonListByVoiceService() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByVoiceService]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by VoiceService  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.VOICE_SERVICE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PAYMENT_STATUS)
    public GenericDataDTO getCommonListByPaymentStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPaymentStatus]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PaymentStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAYMENT_STATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PAYMENT_MODE)
    public GenericDataDTO getCommonListByPaymentMode() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPaymentMode]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PaymentMode  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAYMENT_MODE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_TYPE)
    public GenericDataDTO getCommonListByPlanType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPlanType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlanType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLAN_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_CATEGORY)
    public GenericDataDTO getCommonListByPlanCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPlanCategory]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlanCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLAN_CATEGORY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.QUOTA_TYPE)
    public GenericDataDTO getCommonListByQuotaType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByQuotaType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by QuotaType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.QUOTA_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.QUOTA_TYPE_DATA)
    public GenericDataDTO getCommonListByQuotaTypeData() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByQuotaTypeData]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by QuotaTypeData  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.QUOTA_TYPE_DATA);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.QUOTA_TYPE_TIME)
    public GenericDataDTO getCommonListByQuotaTypeTime() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByQuotaTypeTime]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by QuotaTypeTime  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.QUOTA_TYPE_TIME);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.ADDRESS_TYPE)
    public GenericDataDTO getCommonListByAddressType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByAddressType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by AddressType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.ADDRESS_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.BILL_RUN_STATUS)
    public GenericDataDTO getCommonListByBillRunStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByBillRunStatus]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by BillRunStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.BILL_RUN_STATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DUNNING_CREDIT_CLASS)
    public GenericDataDTO getCommonListByDunningCreditClass() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningCreditClass]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by DunningCreditClass  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.DUNNING_CREDIT_CLASS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DUNNING_ACTION)
    public GenericDataDTO getCommonListByDunningAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningAction]";
  //      ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by DunningAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.DUNNING_ACTION);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PARTNER_COMM_TYPE)
    public GenericDataDTO getPartnerCommType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPartnerCommType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PartnerCommType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PARTNER_COMM_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_STATUS)
    public GenericDataDTO getCommonListByPlanStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPLANSTATUS]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlanStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLAN_STATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_GROUP)
    public GenericDataDTO getCommonListByPlanGroup() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPLANGROUP]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlanGroup  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLAN_GROUP);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CHARGE_TYPE)
    public GenericDataDTO getCommonListByChargeType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByChargeType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by ChargeType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CHARGE_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TAX_TYPE)
    public GenericDataDTO getCommonListByTaxType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByTaxType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TaxType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TAX_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TAX_GROUP)
    public GenericDataDTO getCommonListByTaxGroup() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByTaxGroup]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TaxGroup  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TAX_GROUP);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.AUTH_DRIVER_TYPE)
    public GenericDataDTO getCommonListByAuthDriverType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByAuthDriverType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by AuthDriverType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.AUTH_DRIVER_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.LDAP_AUTH_TYPE)
    public GenericDataDTO getCommonListByLDAPAuthType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByLDAPAuthType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by LDAPAuthType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.LDAP_AUTH_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.BILLING_CYCLE)
    public GenericDataDTO getCommonListByBillingCycle() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByBillingCycle]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by BillingCycle  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.BILLING_CYCLE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DISC_TYPE)
    public GenericDataDTO getCommonListByDiscType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDiscType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by DiscType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.DISC_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.NETWORK_DEVICE_TYPE)
    public GenericDataDTO getCommonListByNetworkDeviceType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByNetworkDeviceType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.NETWORK_DEVICE_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TAT_CONSIDERATION)
    public GenericDataDTO getCommonListByTATConsideration() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByTATConsideration]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TATConsideration  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TAT_CONSIDERATION);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.COMMON_STATUS)
    public GenericDataDTO getCommonListByCommonStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCommonStatus]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CommonStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.COMMON_STATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.OPERATION)
    public GenericDataDTO getCommonListByOperation() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByOperation]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Operation  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.OPERATION);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CASETYPE)
    public GenericDataDTO getCommonListByCaseType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseFor]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CaseType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CASETYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CASEFOR)
    public GenericDataDTO getCommonListByCaseFor() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseFor]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CaseFor  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CASEFOR);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.ORIGIN)
    public GenericDataDTO getCommonListByOrigin() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByOrigin]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Origin  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.ORIGIN);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CASESTATUS)
    public GenericDataDTO getCommonListByCaseStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseStatus]";
        ApplicationLogger.logger.info(SUB_MODULE);
  //      logger.info("GET Common list by CaseStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CASESTATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PRIORITY)
    public GenericDataDTO getCommonListByPriority() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPriority]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Priority  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PRIORITY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.STATUS)
    public GenericDataDTO getCommonListByStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByOlt]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Status  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.STATUS);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CALENDER)
    public  GenericDataDTO getCommonListByCalender() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCalender]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Calender  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CALENDER);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.HIERARCHY_EVENT)
    public  GenericDataDTO getCommonListByHierarchyEvent() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByHierarchyEvent]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by HierarchyEvent  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.HIERARCHY_EVENT);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.DUNNING_EVENT)
    public  GenericDataDTO getCommonListByDunningType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
     //   ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by DunningType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.DUNNING_EVENT);
    }

//    @ApiIgnore
//    @GetMapping(UrlConstants.DUNNING_EVENT)
//    public  GenericDataDTO getCommonListByDunningType() throws Exception {
//        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
//        ApplicationLogger.logger.info(SUB_MODULE);
//        return getCommonListByType(TypeConstants.DUNNING_EVENT);
//    }
    @ApiIgnore
    @GetMapping(UrlConstants.PAYMENT_ACTION_EVENT)
    public  GenericDataDTO getCommonListByPaymentAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
    //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PaymentAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAYMENT_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PAYMENT_CATEGORY_EVENT)
    public  GenericDataDTO getCommonListByPaymentCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PaymentCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAYMENT_CATEGORY_EVENT);
    }

    @GetMapping(UrlConstants.PARTNER_TYPE)
    public  GenericDataDTO getCommonListByPartnerType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPartnerType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PartnerType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PARTNER_TYPE);
    }


    @ApiIgnore
    @GetMapping(UrlConstants.PLANS_ACTION_EVENT)
    public  GenericDataDTO getCommonListByPlansAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
      //  ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlansAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLANS_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLANS_CATEGORY_EVENT)
    public  GenericDataDTO getCommonListByPlansCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
      //  ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by PlansCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PLANS_CATEGORY_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CAF_ACTION_EVENT)
    public  GenericDataDTO getCommonListByCAFAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CAFAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CAF_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CAF_CATEGORY_EVENT)
    public  GenericDataDTO getCommonListByCAFCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CAFCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CAF_CATEGORY_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TERMINATE_ACTION_EVENT)
    public  GenericDataDTO getCommonListByTerminateAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
   //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TerminateAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TERMINATE_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TERMINATE_CATEGORY_EVENT)
    public  GenericDataDTO getCommonListByTerminateCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
  //      ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TerminateCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TERMINATE_CATEGORY_EVENT);
    }

//    @ApiIgnore
//    @GetMapping(UrlConstants.INVENTORY_ASSIGN_EVENT)
//    public  GenericDataDTO getCommonListByInventoryAssignAction() throws Exception {
//        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
//        ApplicationLogger.logger.info(SUB_MODULE);
//        return getCommonListByType(TypeConstants.INVENTORY_ASSIGN_EVENT);
//    }







    @GetMapping("byType/{type}")
    public GenericDataDTO getCommonList(@PathVariable("type") String type) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonList]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("getCommonList with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}; message:{}}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        try {
        //    ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList = list.stream()
                    .sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed())
                    .collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            return genericDataDTO;
        } catch (Exception ex) {
       //     ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");

            logger.error("Unable to fetch get commonlist with  : " + type +  " request: {  module  : {}}; Response : {{}; message: {}}",SUB_MODULE, APIConstants.FAIL,genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


    @GetMapping(UrlConstants.GENERIC + "/{type}")
    public GenericDataDTO getCommonListWithCacheByType(@PathVariable("type") String type) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPaymentStatus]";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("getCommonList with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}; message: {}}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        try {
            ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList;
            if (type.equalsIgnoreCase(TypeConstants.TICKET_SEARCH_OPTION)) {
                sortedList = list.stream()
                        .sorted(Comparator.comparing(CommonListDTO::getDisplayName))
                        .collect(Collectors.toList());
            } else {
                sortedList = list.stream()
                        .sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed())
                        .collect(Collectors.toList());
            }
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to fetch get commonlist with  : " + type +  " request: {  module  : {}}; Response : {{};mesagee: {}}",SUB_MODULE, APIConstants.FAIL,genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(UrlConstants.GENERIC + "/{type}"+"/{name}")
    public GenericDataDTO getCommonListForMvno(@PathVariable("type") String type,@PathVariable("type") String name) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPaymentStatus]";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("getCommonList with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}; message: {}}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        try {
            ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList = list.stream()
                    .sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed())
                    .collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to fetch get commonlist with  : " + type +  " request: {  module  : {}}; Response : {{};mesagee: {}}",SUB_MODULE, APIConstants.FAIL,genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CHARGE_CATEGORY)
    public GenericDataDTO getCommonListByChargeCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByChargeCategory]";
     //   ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("Fetching get Common list by Category:  request: {module: {}}; Response : {{}}",SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CHARGE_CATEGORY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.BILL_TO)
    public GenericDataDTO getCommonListByBillTo() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByChargeCategory]";
       // ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("Fetching Get Common list by Bill to:  request: {module: {}}; Response : {{};}",SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.BILL_TO);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PRODUCT_CATEGORY)
    public GenericDataDTO getCommonListByProductType() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByNetworkDeviceType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.PRODUCT_CATEGORY_TYPE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.AT_MIDNIGHT)
    public GenericDataDTO getCommonListByAtMidnight() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByAtMidnight]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.AT_MIDNIGHT);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.ACTUAL_TIME)
    public GenericDataDTO getCommonListByActualTime() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByActualTime]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.ACTUAL_TIME);
    }
    @GetMapping(UrlConstants.CLEAR_CACHE)
    public GenericDataDTO clearCache() {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[clearCache]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("Clear cache :  request: {module: {}}; Response : {{}}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        try {
            ApplicationLogger.logger.info(SUB_MODULE);
            for (String name : cacheManager.getCacheNames()) {
                cacheManager.getCache(name).clear();
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to clear cache");

            logger.error(" Failed to clear cache; request: { From : {}, Response : {{},message: {},Exception:{}}", SUB_MODULE,APIConstants.FAIL, genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    private GenericDataDTO getCommonListByTypeSorted(String type) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByType]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("getCommonList by type with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}};message: {}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage());
        try {
            // ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList = list.stream()
                    .sorted(Comparator.comparing(CommonListDTO::getIdentityKey))
                    .collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            logger.info("Fetching CommonList by type with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}};message: {}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage());
            return genericDataDTO;
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to fetch by type "+type+" :Request : : {{}};message: {};exception:{}",SUB_MODULE, APIConstants.FAIL,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage(),ex.getStackTrace());

        }
        MDC.remove("type");
        return genericDataDTO;
    }

    private GenericDataDTO getCommonListByType(String type) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByType]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("getCommonList by type with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}};message: {}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage());
        try {
           // ApplicationLogger.logger.info(SUB_MODULE);
            List<CommonListDTO> list = commonListService.getCommonListByType(type);
            List<CommonListDTO> sortedList = list.stream()
                    .sorted(Comparator.comparing(CommonListDTO::getIdentityKey).reversed())
                    .collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            logger.info("Fetching CommonList by type with type "+type+"  is fetched  successfully:  request: {module: {}}; Response : {{}};message: {}",SUB_MODULE, APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage());
            return genericDataDTO;
        } catch (Exception ex) {
    //        ApplicationLogger.logger.error(SUB_MODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to fetch by type "+type+" :Request : : {{}};message: {};exception:{}",SUB_MODULE, APIConstants.FAIL,genericDataDTO.getResponseMessage(),genericDataDTO.getResponseMessage(),ex.getStackTrace());

        }
MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/delete")
    public GenericDataDTO delete(@RequestBody CommonListDTO entityDTO, Authentication authentication) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Delete");
        try {
            CommonListDTO dtoData = commonListService.getEntityById(entityDTO.getIdentityKey());
         //   ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
            entityDTO.setStatus("InActive");
            commonListService.updateEntity(entityDTO);
            genericDataDTO.setData(entityDTO);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Delete "+dtoData.getValue()+" is deleted successfully:  request: {module: {}}; Response : {{},message:{}",getModuleNameForLog(), APIConstants.SUCCESS,genericDataDTO.getResponseMessage());

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
            //    ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getStackTrace(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Delete common List Service "+ entityDTO.getText()+" Response : {module: {}}; Response : {{},message:{};Exception: {}",getModuleNameForLog(), APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),ex.getStackTrace());
            } else {
            //    ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getStackTrace(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
                logger.error("Unable to Delete common List Service "+ entityDTO.getText()+"; Response : {{},message:{};Exception: {}",getModuleNameForLog(), APIConstants.SUCCESS,genericDataDTO.getResponseMessage(),ex.getStackTrace());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[CommonListController]";
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_CHANGE_DISCOUNT_ACTION_EVENT)
    public  GenericDataDTO getCustomerChangeDiscountAction() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCustomerChangeDiscountAction]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CUSTOMER_CHANGE_DISCOUNT_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_CHANGE_DISCOUNT_EVENT)
    public  GenericDataDTO getCustomerChangeDiscountCategory() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCustomerChangeDiscountAction]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CUSTOMER_CHANGE_DISCOUNT_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.WAREHOUSE_TYPE_CATEGORY_EVENT)
    public  GenericDataDTO getWareHouseTypeCategory() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getWareHouseTypeCategory]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.WAREHOUSE_TYPE_CATEGORY_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.WAREHOUSE_TYPE_ACTION_EVENT)
    public  GenericDataDTO getWareHouseTypeAction() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getWareHouseTypeAction]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.WAREHOUSE_TYPE_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TICKET_PRIORITY)
    public  GenericDataDTO getticketpriority() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getticketpriority]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.TICKET_PRIORITY);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.DUNNING_DAYS)
    public  GenericDataDTO getDunningDays(HttpServletRequest req,HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            String SUB_MODULE = getModuleNameForLog() + "[getWareHouseTypeAction]";
            ApplicationLogger.logger.info(SUB_MODULE);
            return getCommonListByType(TypeConstants.DUNNING_DAYS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @ApiIgnore
    @GetMapping(UrlConstants.VALLEY_TYPES)
    public  GenericDataDTO getCommonListByValleyType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByValleyType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Valley Type  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.VALLEY_TYPES);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_TYPE)
    public  GenericDataDTO getCommonListByCustomerType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustomerType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Customer Type  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.FUNDED)
    public  GenericDataDTO getCommonListByFunded() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByFunded]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Funded  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.FUNDED);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PAID)
    public  GenericDataDTO getCommonListByPaid() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPaid]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Paid  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAID);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.FREE)
    public  GenericDataDTO getCommonListByFree() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByFree]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Free  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.FREE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_SECTOR)
    public  GenericDataDTO getCommonListByCustomerSector() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustomerSector]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Customer Sector  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_SECTOR);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INSIDE_VALLEY_TYPES)
    public  GenericDataDTO getCommonListByInsideValleyTypes() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByInsideValleyTypes]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list of Inside Valley Types  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INSIDE_VALLEY_TYPES);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.OUTSIDE_VALLEY_TYPES)
    public  GenericDataDTO getCommonListByOutsideValleyTypes() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByOutsideValleyTypes]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list of Outside Valley Types  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.OUTSIDE_VALLEY_TYPES);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DEPARTMENT_TYPE)
    public  GenericDataDTO getDepartmentTypes() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getDepartmentTypes]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.DEPARTMENT_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TICKET_SOURCE_TYPE)
    public  GenericDataDTO getTicketSourceTypes() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getTicketSourceTypes]";
        ApplicationLogger.logger.info(SUB_MODULE);
        GenericDataDTO result = getCommonListByType(TypeConstants.TICKET_SOURCE_TYPE);
        if (result.getDataList() != null) {
            // Sort by id in ascending order
            result.getDataList().sort(Comparator.comparing(CommonListDTO::getId));
        }
        return result;
    }

    @ApiIgnore
    @GetMapping(UrlConstants.ACCESSIBILITY)
    public GenericDataDTO getCommonListByAccessibility() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByAccessibility]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by accessibility  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.ACCESSIBILITY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCVERIFICATIONMODE)
    public GenericDataDTO getCommonListByCustdocverificationmode() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocverificationmode]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocverificationmode  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCVERIFICATIONMODE);
    }

    @GetMapping(UrlConstants.CUSTDOCVERIFICATIONMODES)
    public GenericDataDTO getCommonListByCustdocverificationmodesType(@RequestParam("mode") String mode) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocverificationmode_"+  mode + "]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocverificationmode_ {}: request: {};response:{}",mode, SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");

        String verificationMode = commonListService.concatMethod(mode);
        return getCommonListByType(verificationMode);
    }



    @GetMapping(UrlConstants.CUSTDOCSUBTYPE)
    public GenericDataDTO getCommonListBycustdocsubtype(@RequestParam("mode") String mode,@RequestParam("custdocsubtype") String custdocsubtype) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBycustdocsubtype_proofofidentity_online]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofidentity_online  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");

        String docsubtype = commonListService.concatMethod(mode,custdocsubtype);
        if (docsubtype==null){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Error request", null);
        }
        return getCommonListByType(docsubtype);
    }

    @GetMapping(UrlConstants.MVNODOCSUBTYPE)
    public GenericDataDTO getCommonListByMvnodocsubtype(@RequestParam("mode") String mode,@RequestParam("mvnodocsubtype") String mvnodocsubtype) throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBycustdocsubtype_proofofidentity_online]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofidentity_online  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");

        String docsubtype = commonListService.concatMethodForMvno(mode,mvnodocsubtype);
        if (docsubtype==null){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Error request", null);
        }
        return getCommonListByType(docsubtype);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_ONLINE)
    public GenericDataDTO getCommonListBycustdocsubtype_proofofaddress_online() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBycustdocsubtype_proofofaddress_online]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofaddress_online  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_ONLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_OFFLINE)
    public GenericDataDTO getCommonListBycustdocsubtype_proofofidentity_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBycustdocsubtype_proofofidentity_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofidentity_offline  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_OFFLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_ONLINE)
    public GenericDataDTO getCommonListBycustdocsubtype_proofofidentity_online() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBycustdocsubtype_proofofidentity_online]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofidentity_online  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_PROOFOFIDENTITY_ONLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_OFFLINE)
    public GenericDataDTO getCommonListByCustdocsubtype_proofofaddress_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocsubtype_proofofaddress_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_proofofaddress_offline  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_PROOFOFADDRESS_OFFLINE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_CONTRACT_OFFLINE)
    public GenericDataDTO getCommonListByCustdocsubtype_contract_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocsubtype_contract_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_contract_offline  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_CONTRACT_OFFLINE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.CURRENT_INWARD_TYPE)
    public GenericDataDTO getCommonListByCurrentInwardType() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCurrentInwardType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CURRENT_INWARD_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PARTNERDOCVERIFICATIONMODE)
    public GenericDataDTO getCommonListByPartnerdocverificationmode() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPartnerdocverificationmode]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by partnerdocverificationmode  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PARTNERDOCVERIFICATIONMODE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PARTNERDOCVERIFICATIONMODE_OFFLINE)
    public GenericDataDTO getCommonListBypartnerdocverificationmode_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPartnerdocverificationmode_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by partnerdocverificationmode_offline  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PARTNERDOCVERIFICATIONMODE_OFFLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PARTNERDOCSUBTYPE_CONTRACT_OFFLINE)
    public GenericDataDTO getCommonListBypPartnerdocsubtype_contract_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByPartnerdocsubtype_contract_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by partnerdocsubtype_contract_offline  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PARTNERDOCSUBTYPE_CONTRACT_OFFLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DUNNING_SECTOR)
    public GenericDataDTO getDunningSector()throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getDunningSector]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.DUNNING_SECTOR);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.UOM_TYPE)
    public GenericDataDTO getCommonListByUOMTYPE() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByUomType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.UOM_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.BANK_TYPE)
    public GenericDataDTO getBankType(HttpServletRequest req,HttpServletResponse res) throws Exception{
        long startTime = System.nanoTime();  // Start measuring
        try {
            String SUB_MODULE = getModuleNameForLog() + "[getBankType]";
            ApplicationLogger.logger.info(SUB_MODULE);
            return getCommonListByType(TypeConstants.BANK_TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CONNECTION_TYPE)
    public GenericDataDTO getConnectionType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getConnectionType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CONNECTION_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.LINK_TYPE)
    public GenericDataDTO getLinkType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getLinkType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.LINK_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CIRCUIT_AREA)
    public GenericDataDTO getCircuitArea() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getCircuitArea]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CIRCUIT_AREA);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INVOICE_FORMAT)
    public GenericDataDTO getInvoiceFormat() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getInvoiceFormat]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by InvoiceFormat  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVOICE_FORMAT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_BINDING_TYPE)
    public GenericDataDTO getPLanBindingType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getPLanBindingType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.PLAN_BINDING_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.STORAGE_TYPE)
    public GenericDataDTO getstorageType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getstorageType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.STORAGE_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.SOC_TYPE)
    public GenericDataDTO getsocType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getsocType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.SOC_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.HOSTING_TYPE)
    public GenericDataDTO gethostingType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getstorageType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.HOSTING_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.FIREWALL_TYPE)
    public GenericDataDTO getfirewallType() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getfirewallType]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.FIREWALL_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_SCREEN)
    public GenericDataDTO getcustomerscreendetails() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getcustomerscreendetails]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByTypeSorted(TypeConstants.CUSTOMER_SCREEN);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PLAN_SCREEN)
    public GenericDataDTO getplanscreendetails() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getplanscreendetails]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.PLAN_SCREEN);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.LEAD_SCREEN)
    public GenericDataDTO getleadscreendetails() throws Exception{
        String SUB_MODULE = getModuleNameForLog() + "[getleadscreendetails]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.LEAD_SCREEN);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DTV_CATEGORY)
    public GenericDataDTO getCommonListByDTVCategory() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDTVCategory]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.DTV_CATEGORY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.WARRANTYTIMEUNIT)
    public GenericDataDTO getCommonListByWarrantyTimeUnit() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.WARRANTYTIMEUNIT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.ABBS)
    public GenericDataDTO getCommonListByPayTypeAbbs() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.ABBS);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.TDS)
    public GenericDataDTO getCommonListByPayTypeTDS() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.TDS);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.VAT_RECIEVABLE)
    public GenericDataDTO getCommonListByPayTypeVatRecievable() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.VAT_RECIEVABLE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.QR)
    public GenericDataDTO getCommonListByPayTypeQr() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.QR);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.POS_ADJUSTEMENT)
    public GenericDataDTO getCommonListByPayTypePos() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.POS_ADJUSTEMENT);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.ONLINE)
    public GenericDataDTO getCommonListByPayTypeOnline() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.ONLINE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.CHEQUE)
    public GenericDataDTO getCommonListByPayTypeCheque() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CHEQUE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CASH)
    public GenericDataDTO getCommonListByPayTypeCash() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.CASH);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.OTHER_ADJUSTMENT)
    public GenericDataDTO getCommonListByPayTypeOtherAdjustment() throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByWarrantyTimeUnit]";
        ApplicationLogger.logger.info(SUB_MODULE);
        return getCommonListByType(TypeConstants.OTHERADJUSTMENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_SERVICE_ADD_ACTION_EVENT)
    public  GenericDataDTO getCommonListByCUstomerServiceAddAction() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CAFAction  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_SERVICE_ADD_ACTION_EVENT);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_SERVICE_ADD_CATEGORY_EVENT)
    public  GenericDataDTO getCommonListByCustomerServiceAddCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDunningType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by CAFCategory  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_SERVICE_ADD_CATEGORY_EVENT);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.CUSTDOCSUBTYPE_MIGRATION_OFFLINE)
    public GenericDataDTO getCommonListByCustdocsubtype_migration_offline() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocsubtype_migration_offline]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by custdocsubtype_migration_offline  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTDOCSUBTYPE_MIGRATION_OFFLINE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_CATEGORY)
    public GenericDataDTO getCommonListByCustomerCategory() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocsubtype_migration_offline]";
        logger.info("GET Common list by customer_category  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_CATEGORY);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.TEAM_TYPE)
    public GenericDataDTO getCommonListByTeamType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCustdocsubtype_migration_offline]";
        logger.info("GET Common list by customer_category  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TEAM_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.PAYMENT_GATEWAY)
    public GenericDataDTO getCommonListBypayment_gateway() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBypayment_gateway]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by payment_gateway  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.PAYMENT_GATEWAY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INTEGRATION_EVENT_TYPE)
    public GenericDataDTO getCommonListByEventName() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByEventName]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by payment_gateway  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INTEGRATION_EVENT_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.THIRD_PARTY_INTEGRATION)
    public GenericDataDTO getCommonListByClientName() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByClientName]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by payment_gateway  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.THIRD_PARTY_INTEGRATION);
    }



    @ApiIgnore
    @GetMapping(UrlConstants.CUSTOMER_ATTRIBUTE)
    public GenericDataDTO getCommonListByCUSTOMER_ATTRIBUTE() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBypayment_gateway]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by payment_gateway  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.CUSTOMER_ATTRIBUTE);
    }


    @ApiIgnore
    @GetMapping(UrlConstants.OTP_AUTH_TYPE)
    public GenericDataDTO getCommonListByOTPAuthType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListBypayment_gateway]";
        //    ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by payment_gateway  : request: {};response:{}", SUB_MODULE, APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.OTP_AUTH_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TASKTATUS)
    public GenericDataDTO getCommonListByTaskStatus() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseStatus]";
        ApplicationLogger.logger.info(SUB_MODULE);
        //      logger.info("GET Common list by CaseStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TASKTATUS);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.BUILDING_REFRENCE)
    public GenericDataDTO getCommonListByBuildingRefrence(HttpServletResponse res,HttpServletRequest req) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            MDC.put("type", "Fetch");
            String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseStatus]";
            ApplicationLogger.logger.info(SUB_MODULE);
            //      logger.info("GET Common list by CaseStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
            MDC.remove("type");
            return getCommonListByType(TypeConstants.BUILDING_REFRENCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @ApiIgnore
    @GetMapping(UrlConstants.TASKTYPE)
    public GenericDataDTO getCommonListByTaskType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseStatus]";
        ApplicationLogger.logger.info(SUB_MODULE);
        //      logger.info("GET Common list by CaseStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TASKTYPE);
    }


    @ApiIgnore
    @GetMapping(UrlConstants.BUILDINGKTYPE)
    public GenericDataDTO getCommonListByBuildingType(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long startTime = System.nanoTime();  // Start measuring
        try {
            MDC.put("type", "Fetch");
            String SUB_MODULE = getModuleNameForLog() + "[getCommonListByCaseStatus]";
            ApplicationLogger.logger.info(SUB_MODULE);
            //      logger.info("GET Common list by CaseStatus  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
            MDC.remove("type");
            return getCommonListByType(TypeConstants.BUILDINGTYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INVENTORY_DOC_TYPE)
    public GenericDataDTO getCommonListByInventoryDocType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByNetworkDeviceType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVENTORY_DOC_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.KNOWLEDGE_BASE_DOC)
    public GenericDataDTO getCommonListForKnowledgeBase() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListForKnowledgeBase]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVENTORY_DOC_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DOCUMENT_FOR)
    public GenericDataDTO getCommonListByDocumentForType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDocumentForType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVENTORY_DOC_TYPE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.DOCUMENT_TYPE)
    public GenericDataDTO getCommonListByDocumentType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByDocumentType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVENTORY_DOC_TYPE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.MULTIPLE_CURRENCY)
    public GenericDataDTO getCommonListByMultipleCurrency() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByMultipleCurrency]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by NetworkDeviceType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.MULTIPLE_CURRENCY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INSTALLMENT_FREQUENCY)
    public GenericDataDTO getCommonListByInstallmentFrequency() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByInstallmentFrequency]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by InstallmentFrequency  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INSTALLMENT_FREQUENCY);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.NATURE)
    public GenericDataDTO getCommonListByNature() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByNature]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Nature  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.NATURE);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.INVENTORYJOBTYPE)
    public GenericDataDTO getInventoryJobType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByInventoryJobType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by InventoryJobType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.INVENTORYJOBTYPE);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.FEASIBILITY)
    public GenericDataDTO getCommonListByFeasibility() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByFeasibility]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by Feasibility  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.FEASIBILITY);
    }
    @ApiIgnore
    @GetMapping(UrlConstants.TICKET_CLASSIFICATION)
    public GenericDataDTO getCommonListByTicketClassification() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByTicketClassification]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by TicketClassification  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.TICKET_CLASSIFICATION);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.Change_Plan_Billing_Cycle)
    public GenericDataDTO getCommonListByChangePlanBillingCycle() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByChangePlanBillingCycle]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by ChangePlanBillingCycle  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.Change_Plan_Billing_Cycle);
    }

    @ApiIgnore
    @GetMapping(UrlConstants.Change_Plan_Type)
    public GenericDataDTO getCommonListByChangePlanType() throws Exception {
        MDC.put("type", "Fetch");
        String SUB_MODULE = getModuleNameForLog() + "[getCommonListByChangePlanType]";
        //     ApplicationLogger.logger.info(SUB_MODULE);
        logger.info("GET Common list by ChangePlanType  : request: {};response:{}" ,SUB_MODULE,APIConstants.SUCCESS);
        MDC.remove("type");
        return getCommonListByType(TypeConstants.Change_Plan_Type);
    }
}



