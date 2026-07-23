package com.savbill.revenuemanagement.core.controller.creditdoc;

import brave.Tracer;
import brave.propagation.TraceContext;
//import com.savbill.cpm.pojo.CustomerNotesDto;
import com.savbill.revenuemanagement.core.auditLog.service.AuditLogService;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.SearchPaymentPojo;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustomerVoucherDTO;
import com.savbill.revenuemanagement.core.dto.invoice.CreditDocumentSearchPojo;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.dto.invoice.WriteOffRequestDTO;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.rabbitmq.messages.CreditDocMessage;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static com.savbill.revenuemanagement.core.util.ResponseUtil.setPaginationDetails;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)

public class CreditDocController {

    private static String MODULE = " [CreditDocController] ";

    @Autowired
    private CreditDocRepository creditDocRepository;


    @Autowired
    CreditDocService creditDocService;

    @Autowired
    private Tracer tracer;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private CustomersRepository customersRepository;

    private static final Logger logger = LoggerFactory.getLogger(CreditDocController.class);

    @PostMapping(value = "/getWithdrawPayments/{customerId}")
    public GenericDataDTO voidInvoice(@PathVariable(name = "customerId") Integer customerId, @RequestBody PaginationRequestDTO paginationRequestDTO) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Fetching All payments for customer id " + customerId + "  : Response : {{}}", genericDataDTO.getResponseCode());
            genericDataDTO.setDataList(creditDocService.getWithdrawPayments(customerId,paginationRequestDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully");
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getMessage());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/withdraw/payment")
    public ResponseEntity<?> createRecordPayment(@RequestBody RecordPaymentPojo pojo) throws Exception {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            creditDocService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = creditDocService.withDrawal(pojo, true, false, false);
            response.put("recordpayment", pojo);
            RESP_CODE = APIConstants.SUCCESS;
      } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to createRecordPayment  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to createRecordPayment  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, null);
    }

    @PostMapping("/transfer/payment")
    public ResponseEntity<?> createTransferRecordPayment(@RequestBody RecordPaymentPojo pojo,HttpServletRequest req) throws Exception {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            creditDocService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = creditDocService.walletTransfer(pojo, false, false, false);
            response.put("recordpayment", pojo);
            RESP_CODE = APIConstants.SUCCESS;
            Optional<Customers> senderCustomers=customersRepository.findById(pojo.getCustomerid());
            String senderAcc=senderCustomers.get().getAcctno();
            logger.info("customer id"+pojo.getToCustomerId());
            Optional<Customers> reciverCustomers=customersRepository.findById(pojo.getToCustomerId());
            String reciverAcc=reciverCustomers.get().getAcctno();
            String remark = "Transfer amount " + pojo.getAmount()
                    + " from " + senderAcc
                    + " to " + reciverAcc;



            auditLogService.addTranferAuditEntry(AclConstants.ACL_CLASS_TRANSFER_PAYMENT,AclConstants.OPERATION_TRANSFER_PAYMENT,req.getRemoteAddr(),remark,pojo.getCustomerid().longValue(),pojo.getToCustomerId().longValue(),pojo.getAmount());
//


        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to createRecordPayment(wallet Transfer)  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to createRecordPayment(wallet Transfer)  for " + pojo.getCustomerid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, null);
    }

    @GetMapping("/getCNAmount/{debitDocId}")
    public String calculateCreditNoteAmount(@PathVariable(name = "debitDocId") Integer debitDocId) throws Exception {
        try {
            logger.debug("Calculate Credit Note Amount");
            return creditDocService.previewCreditNoteAmount(debitDocId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
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
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    @PostMapping("/addamouttowallet")
    public ResponseEntity<?> addamoutToWallet(@RequestBody CustomerVoucherDTO pojo) throws Exception {
        MDC.put("type", "Crete");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
             creditDocService.addToWallet(pojo);
            response.put("recordpayment", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to createRecordPayment  for " + pojo.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to createRecordPayment  for " + pojo.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, null);
    }
    @PostMapping(value = "/writeOffByDebitDocId", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO writeOffByDebitDocId(@RequestBody WriteOffRequestDTO request) {
        logger.info("********** Inside writeOffByDebitDocId method **********");
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return creditDocService.writeOffByDebitDocId(request);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping("/payment/filter")
    public ResponseEntity<?> searchPaymentByKraSyncStatus(@ModelAttribute SearchPaymentPojo entity,
                                                           @RequestParam(name = "isKraSynced", required = false) Boolean isKraSynced,
                                                           HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<CreditDocumentSearchPojo> creditDocList = null;
        PaginationRequestDTO requestDTO = new PaginationRequestDTO();
        requestDTO.setPage(entity.getPage());
        requestDTO.setPageSize(entity.getPageSize());
        entity.setIsKraSynced(isKraSynced);
        try {
            creditDocList = creditDocService.getCreditDocuments1(entity, requestDTO);
            if (creditDocList != null && creditDocList.hasContent()) {
                response.put("creditDocumentPojoList", creditDocList.getContent());
            } else {
                response.put("creditDocumentPojoList", new ArrayList<>());
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR
                    + "search Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR
                    + "search Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()
                    + LogConstants.LOG_STATUS_CODE + RESP_CODE, ce);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR
                    + "search Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()
                    + LogConstants.LOG_STATUS_CODE + RESP_CODE, ex);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstants.TRACE_ID);
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, creditDocList);
    }
    @PostMapping("/payment/approve")
    public ResponseEntity<?> approvePayment(@RequestBody SearchPaymentPojo entity, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            creditDocService.validatePaymentActionRequest(entity, CommonConstants.OPERATION_ADD);
            response.put("payment", creditDocService.approvePayment(entity));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve Payment" + LogConstants.LOG_BY_NAME + entity.getUserName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve Payment " + LogConstants.LOG_BY_NAME + entity.getUserName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update approve Payment" + LogConstants.LOG_BY_NAME + entity.getUserName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response,null);
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

//    @PostMapping("/addCreditDoc")
//    public Integer addCreditDoc(
//            @RequestBody CreditDocMessage message) {
//
//        try {
//            return creditDocService.addCreditDoc(message);
//        } catch (Exception e) {
//            try {
//                java.io.FileWriter fw = new java.io.FileWriter("E:\\infinvisglobal\\revenue_error.txt", true);
//                java.io.PrintWriter pw = new java.io.PrintWriter(fw);
//                pw.println("CRDB ERROR at " + java.time.LocalDateTime.now());
//                e.printStackTrace(pw);
//                pw.close();
//            } catch(Exception ignored) {}
//            e.printStackTrace();
//            System.err.println("CRDB addCreditDoc FAILED in Revenue Service: " + e.getMessage());
//            throw new RuntimeException("CRDB addCreditDoc FAILED in Revenue Service: " + e.getMessage(), e);
//        }
//    }
}
