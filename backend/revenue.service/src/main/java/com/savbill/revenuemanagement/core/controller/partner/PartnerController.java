package com.savbill.revenuemanagement.core.controller.partner;


import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerDtlsPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerInfoPojo;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ledger.DebitDocService;
import com.savbill.revenuemanagement.core.service.partner.PartnerLedgerDetailsService;
import com.savbill.revenuemanagement.core.service.partner.PartnerService;
import com.savbill.revenuemanagement.core.service.prepaid.CustomerLedgerDtlsService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.savbill.revenuemanagement.core.util.ResponseUtil.setPaginationDetails;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class PartnerController {
    private static String MODULE = " [PartnerController] ";

    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;


    @Autowired
    DebitDocService debitDocService;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerDebitDocRepository partnerDebitDocRepository;

    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;


    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }


    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/partnerLedger")
    public ResponseEntity<?> getPartnerLedgerDetails(@Valid @RequestBody PartnerLedgerGetDTO pojo) throws Exception {
        MDC.put("type", "Fetch");

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PartnerLedgerInfoPojo infoPojo = partnerLedgerDetailsService.getByTime(pojo);

            Partner partner=partnerRepository.findById(pojo.getPartner_id()).orElse(null);
            if(partner!=null)
            {
                if(partner.getPriceBookId()!=null && partner.getPriceBookId().getCommission_on().equalsIgnoreCase("Plan level"))
                {
                    List<PartnerLedgerDetailsPlanLevelDTO> detailsPlanLevelDTOs=partnerLedgerDetailsService.convertIntoPlanLevelDTO(infoPojo);
                    infoPojo.setPartnerLedgerDetailsPlanLevelDTO(detailsPlanLevelDTOs);
                }

                if(partner.getPriceBookId()!=null && partner.getPriceBookId().getCommission_on().equalsIgnoreCase("Service level"))
                {
                    List<PartnerLedgerDetailsServiceLevelDTO> detailsServiceLevelDTOs=partnerLedgerDetailsService.convertIntoServiceLevelDTO(infoPojo);
                    infoPojo.setPartnerLedgerDetailsServiceLevelDTO(detailsServiceLevelDTOs);
                }
            }


            PartnerLedgerAllInfoPojo ledgerAllInfoPojo = partnerLedgerDetailsService.partInfoByTime(pojo.getPartner_id(), infoPojo);
            if (ledgerAllInfoPojo == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error("Unable to Fetch parent ledger details  :  request: { From : {}}; Response : {{}};Error :{}", MODULE, RESP_CODE, response);
            } else {
                response.put("partnerLedgerDtls", ledgerAllInfoPojo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(" fetching details " + ledgerAllInfoPojo.getPartnername() + "  :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch parent ledger details  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch parent ledger details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/balanceAndCommissionInfoForShiftLocation/{custId}")
    public ResponseEntity<?> getBalanceAndCommissionInfoForShiftLocation(@PathVariable Integer custId) throws Exception {
        MDC.put("type", "Fetch");

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        ShiftInfo shiftInfo=new ShiftInfo();
        try {
            Customers customers = customersRepository.getByCustomerId(custId);
            if (customers != null) {
                CustomerLedgerDtlsPojo pojo=new CustomerLedgerDtlsPojo();
                pojo.setCustId(custId);
                CustomerLedgerInfoPojo infoPojo=customerLedgerDtlsService.getByTime(pojo);
                double closingBalance = infoPojo.getClosingBalance();
                if(infoPojo!=null && infoPojo.getClosingBalance()!=null && closingBalance < 0.1)
                {
                    if (debitDocService.isAllInvoiceStatusClearedForCustomer(customers))
                    {
                        shiftInfo.setIsInvoiceClear(true);
                        Partner oldPartner = partnerRepository.findById(customers.getPartner()).orElse(null);
                        Double transferableCommission = 0.0;
                        Double transferableBalance = 0.0;

                        if (oldPartner != null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID) {
                            transferableBalance = debitDocService.getTransferableBalance(customers, oldPartner);
                            transferableCommission = debitDocService.getTransferableCommission(customers, oldPartner);
                            shiftInfo.setTransferBalance(transferableBalance);
                            shiftInfo.setTransferCommission(transferableCommission);
                        }
                    }
                    else
                    {
                        shiftInfo.setIsInvoiceClear(false);
                        shiftInfo.setTransferCommission(0.0);
                        shiftInfo.setTransferBalance(0.0);
                    }
                }
                else
                {
                    shiftInfo.setIsInvoiceClear(false);
                    shiftInfo.setTransferCommission(0.0);
                    shiftInfo.setTransferBalance(0.0);
                }
            }
            if (customers == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error("Unable to Fetch Customer Details  :  request: { From : {}}; Response : {{}};Error :{}", MODULE, RESP_CODE, response);
            } else {
                response.put("balanceAndCommissionInfo",shiftInfo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(" fetching details " + shiftInfo + "  :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch ShiftInfo Details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch ShiftInfo Details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/partnerInvoiceGenerate/{day}/{month}/{year}")
    public ResponseEntity<?> partnerInvoiceGenerate(@PathVariable String day,@PathVariable String month,@PathVariable String year) throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocalDate startOfMonth= LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)).minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endOfMonth = LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
            LocalDate nextBillDate = LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
            LoggedInUser loggedInUser=getLoggedInUser();
            if(loggedInUser!=null && loggedInUser.getMvnoId()!=null) {
                if(!partnerService.isPartnerInvoiceWillGenerate(nextBillDate,startOfMonth, endOfMonth, loggedInUser.getMvnoId()))
                {
                    response.put("message","No Partner is Eligible to Generate an Invoice on the Said Date!");
                    RESP_CODE = APIConstants.NOT_FOUND;
                    return apiResponse(RESP_CODE, response);
                }
                partnerService.generatePartnerCommissionInvoice(nextBillDate,startOfMonth, endOfMonth, loggedInUser.getMvnoId());
            }
            else {
                if(!partnerService.isPartnerInvoiceWillGenerate(nextBillDate,startOfMonth, endOfMonth,null))
                {
                    response.put("message","No Partner is Eligible to Generate an Invoice on the Said Date!");
                    RESP_CODE = APIConstants.NOT_FOUND;
                    return apiResponse(RESP_CODE, response);

                }
                partnerService.generatePartnerCommissionInvoice(nextBillDate,startOfMonth, endOfMonth, null);
            }

            RESP_CODE = APIConstants.SUCCESS;
            response.put("message","Partner Invoice has been generated successfully.");
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("message",ce.getMessage());

            logger.error("Unable to Generate Partner Invoices :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("message",ex.getMessage());
            logger.error("Unable to Generate Partner Invoices :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/getAllPartnerDebitDocument/{partnerId}")
    public ResponseEntity<?> getAllPartnerDebitDocument(@PathVariable Integer partnerId) throws Exception {
        MDC.put("type", "Fetch");

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<PartnerDebitDocument> debitDocuments=new ArrayList<>();
        try {
            if(partnerId!=null) {
                debitDocuments=partnerService.getAllPartnerDebitDocument(partnerId);
            }

            if (debitDocuments == null || (debitDocuments!=null && debitDocuments.isEmpty())) {
                response.put("message","No Record Found");
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error("Unable to Fetch Partner Debit Document Details  :  request: { From : {}}; Response : {{}};Error :{}", MODULE, RESP_CODE, response);
            } else {
                response.put("partnerDebitDocs",debitDocuments);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(" fetching details " + partnerId + "  :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch Partner Debit Document Details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch Partner Debit Document Details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


    @PostMapping("/getPlanCommissionDetailList")
    public ResponseEntity<?> getPlanCommissionDetailList(@Valid @RequestBody PlanCommissionPojo pojo) throws Exception {
        MDC.put("type", "Fetch");

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PlanCommissionDetailList detailList = partnerLedgerDetailsService.getDetail(pojo);

            if (detailList == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error("Unable to Fetch Plan Commission Detail details  :  request: { From : {}}; Response : {{}};Error :{}", MODULE, RESP_CODE, response);
            } else {
                response.put("planCommissionDetails", detailList);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("Fetch Plan Commission Detail details"+"  :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch Plan Commission Detail details  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to Fetch Plan Commission Detail details :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


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


    @GetMapping("/partnerInvoiceDetails/{id}")
    public ResponseEntity<?> partnerInvoiceGenerate(@PathVariable String id) throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if(id!=null)
            {
                PartnerDebitDocument partnerDebitDocument=partnerDebitDocRepository.findById(Integer.parseInt(id)).orElse(null);
                if(partnerDebitDocument==null)
                {
                    response.put("message","No Invoice found");
                    RESP_CODE = APIConstants.NOT_FOUND;
                    return apiResponse(RESP_CODE, response);
                }
                RESP_CODE = APIConstants.SUCCESS;
                response.put("message",partnerDebitDocument);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("message",ce.getMessage());

            logger.error("Unable to Generate Partner Invoices :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("message",ex.getMessage());
            logger.error("Unable to Generate Partner Invoices :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }
}
