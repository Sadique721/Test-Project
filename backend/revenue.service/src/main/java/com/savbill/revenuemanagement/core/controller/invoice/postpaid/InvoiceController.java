package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.InvoiceIntigration.InvoiceIntigrationService;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.auditLog.service.AuditLogService;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.dto.common.*;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.DBR.CustomDailyRevenue;
import com.savbill.revenuemanagement.core.entity.DBR.CustomMonthlyRevenue;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.exceptions.FileNotCreatedException;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.spring.SpringContext;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.billrun.BillRunService;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.ledger.DebitDocService;
import com.savbill.revenuemanagement.core.service.partner.PartnerLedgerDetailsService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.service.prepaid.DebitDocSearchPojo;
import com.savbill.revenuemanagement.core.util.ResponseUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.isp.IspInvoicePayload;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service.BatchPaymentAssignmentService;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service.BatchPaymentService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateCustplanMappingMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class InvoiceController {

    private static String MODULE = " [DebitDocController] ";

    @Autowired
    PdfUtil pdfUtil;
    //@Autowired
    //MessageSender messageSender;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    SubscriberService subscriberService;

//    @Autowired
//    private CustomersService customersService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

    @Autowired
    private TrialDebitDocumentDetailRepository trialDebitDocumentDetailRepository;

    @Autowired
    private TrialDebitDocumentTAXRelRepository trialDebitDocumentTAXRelRepository;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    CustomerServiceMapRepository customerServiceMapRepository;
    @Autowired
    CreditDocService creditDocService;

    @Autowired
    private BatchPaymentService batchPaymentService;
    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    MessageReceiverWithThread messageReceiverWithThread;
    @Autowired
    private TrialDebitDocService entityService;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;


    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    private static final String RETURN_URI_LIST = "postpaid/invoice/trialinvoicelist";
    @Autowired
    DbrService dbrService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;


    private static final Logger logger = Logger.getLogger(InvoiceController.class);
    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    @Autowired
    TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private InvoiceIntigrationService invoiceIntigrationService;

    @Autowired
    MvnoRepository mvnoRepository;
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

    @PreAuthorize("validatePermission(\"" + MenuConstants.prepaid_invoice_master + "\" ,\"" + MenuConstants.postpaid_invoice_master + "\" ,\"" + MenuConstants.pre_cust_invoices + "\",\"" + MenuConstants.post_cust_invoices + "\")")
    @PostMapping("/invoice/search")
    public ResponseEntity<?> searchInvoice(@ModelAttribute SearchDebitDocsPojo entity, @RequestParam(name = "isKraSynced", required = false) Boolean isKraSynced, @RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "isInvoiceVoid", defaultValue = "true") boolean isInvoiceVoid, @RequestParam(name = "isOutstandingDue", defaultValue = "false") boolean isOutstandingDue, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<DebitDocSearchPojo> debitDocList = null;
        try {
            if (entity != null) {
                PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
                requestDTO = setDefaultPaginationValues(requestDTO);
                debitDocList = prepaidInvoiceService.searchInvoice(entity, requestDTO, isInvoiceVoid, false,isOutstandingDue,isKraSynced);
                if (null != debitDocList && 0 < debitDocList.getSize()) {
                    response.put("invoicesearchlist", debitDocList.getContent());
                } else {
                    response.put("invoicesearchlist", new ArrayList<>());
                }
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            RESP_CODE = APIConstants.SUCCESS;
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, debitDocList);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.prepaid_invoice_master + "\" ,\"" + MenuConstants.postpaid_invoice_master + "\" ,\"" + MenuConstants.pre_cust_invoices + "\",\"" + MenuConstants.post_cust_invoices + "\")")
    @PostMapping("/invoice/search/customerPortal")
    public ResponseEntity<?> searchInvoiceCustomerPortal(@ModelAttribute SearchDebitDocsPojo entity, @RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "isInvoiceVoid", defaultValue = "true") boolean isInvoiceVoid, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<DebitDocSearchPojo> debitDocList = null;
        try {
            if (entity != null) {
                PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
                requestDTO = setDefaultPaginationValues(requestDTO);
                debitDocList = prepaidInvoiceService.searchInvoice(entity, requestDTO, isInvoiceVoid, true, false,null);
                if (null != debitDocList && 0 < debitDocList.getSize()) {
                    response.put("invoicesearchlist", debitDocList.getContent());
                } else {
                    response.put("invoicesearchlist", new ArrayList<>());
                }
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            RESP_CODE = APIConstants.SUCCESS;
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + entity.getCustomerid() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, debitDocList);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_void + MenuConstants.post_cust_invoices_void + "\")")
    @GetMapping(value = "/voidInvoice")
    public GenericDataDTO voidInvoice(@RequestParam(name = "invoiceId") Integer invoiceId, @RequestParam(name = "invoiceCancelRemarks", required = false) String invoiceCancelRemarks, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = prepaidInvoiceService.voidInvoice(invoiceId, invoiceCancelRemarks, req);
            DebitDocument debitDocument = debitDocRepository.findById(invoiceId).orElse(null);

            if (debitDocument != null && genericDataDTO.getResponseCode() != HttpStatus.EXPECTATION_FAILED.value()) {
                PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(debitDocument.getCustomer().getId(), debitDocument.getCustomer().getUsername(), null, debitDocument.getTotalamount(), debitDocument.getId().longValue(), null, false, debitDocument.getTotalamount(), null, null, null, "null", "false", null, 0L, debitDocument, debitDocument.getCustomer().getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), null, null, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), true, debitDocument.getIsDirectChargeInvoice(), true, null, null, null);
//                messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
                kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
            }

            if (debitDocument.getPostpaidPlan() != null) {
                String remark = "Void invoice for invoice no: " + debitDocument.getDocnumber() + " For Plan: " + debitDocument.getPostpaidPlan().getName() + " remark: " + invoiceCancelRemarks;
            }
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Void Invoice" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Void Invoice Id: " + invoiceId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/invoiceDetails/{invoiceId}/{custId}")
    public ResponseEntity<?> getChargeListByTypeAndCategory(@PathVariable Integer invoiceId,
                                                            @PathVariable Integer custId) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
        try {
            Optional<Customers> customerOpt = customersRepository.findById(custId);
            if (!customerOpt.get().getStatus().equalsIgnoreCase("NewActivation") && !customerOpt.get().getStatus().equalsIgnoreCase("ActivationPending")) {
                DebitDocSearchPojo debitDocSearchPojo = prepaidInvoiceService.getInvoiceDetails(invoiceId, custId);
                response.put("invoiceDetails", debitDocSearchPojo);

                List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(invoiceId);
                debitDocDetails = debitDocDetails.stream().peek(debitDocDetail ->
                {
                    if (debitDocDetail.getDiscount() < 0) {
                        debitDocDetail.setSubtotal(debitDocDetail.getSubtotal() - debitDocDetail.getDiscount());
                        debitDocDetail.setDiscount(0d);
                    }
                }).collect(Collectors.toList());
                response.put("debitDocDetails", debitDocDetails);

                List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelRepository.findAllByDebitdocumentid(invoiceId);
                response.put("debitDocumentTAXRels", debitDocumentTAXRels);

                Set<String> taxnames = debitDocumentTAXRels.stream().map(DebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
                List<DebitDocumentTAXRelPojo> debitDocumentTAXRelDtos = new ArrayList<>();
                for (String taxname : taxnames) {
                    DebitDocumentTAXRel documentTAXRel = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
                    Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(DebitDocumentTAXRel::getAmount).sum();
                    DebitDocumentTAXRelPojo debitDocumentTAXRelDto = new DebitDocumentTAXRelPojo(taxname, documentTAXRel.getPercentage(), amount, documentTAXRel.getChargeid());
                    debitDocumentTAXRelDtos.add(debitDocumentTAXRelDto);
                }
                response.put("debitDocumentTAXRelDtos", debitDocumentTAXRelDtos);
            } else {
                TrialDebitDocSearchPojo debitDocSearchPojo = prepaidInvoiceService.getInvoiceDetailsTrial(invoiceId, custId);
                response.put("invoiceDetails", debitDocSearchPojo);

                List<TrialDebitDocumentDetail> debitDocDetails = trialDebitDocumentDetailRepository.findAllByDebitdocumentid(invoiceId);
                debitDocDetails = debitDocDetails.stream().peek(debitDocDetail ->
                {
                    if (debitDocDetail.getDiscount() == null)
                        debitDocDetail.setDiscount(0d);
                    if (debitDocDetail.getDiscount() < 0) {
                        debitDocDetail.setSubtotal(debitDocDetail.getSubtotal() - debitDocDetail.getDiscount());
                        debitDocDetail.setDiscount(0d);
                    }
                }).collect(Collectors.toList());
                response.put("debitDocDetails", debitDocDetails);

                List<TrialDebitDocumentTAXRel> debitDocumentTAXRels = trialDebitDocumentTAXRelRepository.findAllByTrialdebitdocumentid(invoiceId);
                response.put("debitDocumentTAXRels", debitDocumentTAXRels);

                Set<String> taxnames = debitDocumentTAXRels.stream().map(TrialDebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
                List<TrialDebitDocumentTAXRelPojo> debitDocumentTAXRelDtos = new ArrayList<>();
                for (String taxname : taxnames) {
                    TrialDebitDocumentTAXRel documentTAXRel = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
                    Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(TrialDebitDocumentTAXRel::getAmount).sum();
                    TrialDebitDocumentTAXRelPojo debitDocumentTAXRelDto = new TrialDebitDocumentTAXRelPojo(taxname, documentTAXRel.getPercentage(), amount, documentTAXRel.getChargeid());
                    debitDocumentTAXRelDtos.add(debitDocumentTAXRelDto);
                }
                response.put("debitDocumentTAXRelDtos", debitDocumentTAXRelDtos);
            }

            RESP_CODE = APIConstants.SUCCESS;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseUtil.apiResponses(RESP_CODE, response);

    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CANCLE_INVOICE_ALL + "\",\"" + AclConstants.OPERATION_CANCLE_INVOICE_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_cancel_regenerate + MenuConstants.post_cust_invoices_cancel_regenerate + "\")")
    @RequestMapping(value = {"/cancelAndRegenerate/{debitDocId}"}, method = RequestMethod.POST)
    public GenericDataDTO cancelAndRegenerateInvoice(@PathVariable Integer debitDocId, @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom, HttpServletRequest req, @RequestParam(name = "isCaf", required = false) Boolean isCaf, @RequestParam(name = "invoiceCancelRemarks", required = false) String invoiceCancelRemarks) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        DebitDocument debitDocument = null;
        TrialDebitDocument trialDebitDocument = null;
        try {
            genericDataDTO.setResponseMessage("SUCCESS!");
            genericDataDTO.setResponseCode(HttpStatus.OK.value());

            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Cancel and cancelAndRegenerate invoice for Id : " + debitDocId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            DebitDocument debitDocument1 = debitDocRepository.findById(debitDocId).orElse(null);
            debitDocService.validate(debitDocument1);
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            custPlanMapppingList = custPlanMappingRepository.findAllByDebitdocid(debitDocId.longValue()).stream().peek(x -> x.setIsInvoiceCreated(false)).collect(Collectors.toList());
//        Integer cpr=custPlanMapppingList.get(0).getId();
            Set<Integer> childIds = custPlanMapppingList.stream().filter(i -> i.getCustomer().getParentCustomers() != null).map(i -> i.getCustomer().getId()).collect(Collectors.toSet());
            CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
            if (custPlanMapppingList.size() == 0) {
                CustChargeDetails custChargeDetails = custChargeDetailsRepository.findAllByDebitdocid(debitDocId.longValue());
                if (custChargeDetails != null && custChargeDetails.getCustPlanMapppingId() != null) {
                    custPlanMapppingList.add(custPlanMappingRepository.findById(custChargeDetails.getCustPlanMapppingId()).get());
                    custPlanMapppingList.stream().peek(x -> x.setIsInvoiceCreated(false)).collect(Collectors.toList());
                } else {
                    List<Integer> debitDocDetailsIds = debitDocDetailRepository.findDebitDocDetailsIdsByMvnoDebitDocId(debitDocId);
                    if (debitDocDetailsIds != null && !debitDocDetailsIds.isEmpty()) {
                        List<Integer> debitDocumentIds = debitDocDetailRepository.findDebitDocIdByDebitDocDetailsIds(debitDocDetailsIds);
                        if (debitDocumentIds != null && !debitDocumentIds.isEmpty()) {
                            debitDocumentIds = debitDocRepository.findDebitDocumentIdsByBillRunStatusIsVoidOrIsDeleteIsTrueAndDebitDocIdsIn(debitDocumentIds);
                            if (debitDocumentIds != null && !debitDocumentIds.isEmpty()) {
                                List<Integer> debitDocDetails = debitDocDetailRepository.findAllDebitDocDetailIdsByDebitDocIds(debitDocumentIds);
                                if (debitDocDetails != null && !debitDocDetails.isEmpty()) {
                                    debitDocDetailsIds = debitDocDetailsIds.stream().filter(detailId -> !debitDocDetails.contains(detailId)).collect(Collectors.toList());
                                    debitDocDetailRepository.updateMvNodeBitDocumentId(null, debitDocDetails);
                                    List<DebitDocDetails> details = debitDocDetailRepository.findAllById(debitDocDetails);
                                    Double totalPrice = details.stream().filter(x -> x.getTotalamount() != null).mapToDouble(x -> x.getTotalamount()).sum();
                                    customerBillingMessage.setTotalPrice(totalPrice);

                                }
                            }
                        }
                    }
                    customerBillingMessage.setMvnoCustomer(true);
                    customerBillingMessage.setDebitDocDetailIds(debitDocDetailsIds);
                    customerBillingMessage.setIspFromDate(debitDocument1.getStartdate().toLocalDate());
                    customerBillingMessage.setIspToDate(debitDocument1.getEndate().toLocalDate());
                }
            }

            debitDocument1.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
            debitDocument1.setRemarks(invoiceCancelRemarks);
            debitDocRepository.save(debitDocument1);
            LoggedInUser user = dbrService.getLoggedInUser();
            Map<String, Object> data = new HashMap<>();
            if (user != null && user.getStaffId() != null)
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, user.getStaffId());

            data.put(CustomerBillingMessage.CUST_ID, debitDocument1.getCustomer().getId());
            if (debitDocument1.getCustomer().getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                customerBillingMessage.setCustType(CommonConstants.CUST_TYPE_POSTPAID);
                data.put(CustomerBillingMessage.POSTPAIDADVANCE, "Advance");
            }
            data.put("oldDebitDocId", debitDocument1.getId());
            if (!custPlanMapppingList.isEmpty() && custPlanMapppingList.get(0).getRenewalId() != null) {
                data.put(customerBillingMessage.RENEWAL_ID, custPlanMapppingList.get(0).getRenewalId());
            }
            List<Long> invMappingIds = customerInventoryMappingRepo.findAllByCustomerId(debitDocument1.getCustomer().getId().longValue());
            if (!CollectionUtils.isEmpty(invMappingIds)) {
                data.put(CustomerBillingMessage.CUSTOMER_INVENTORY_CAF_TO_CUSTOMER, true);
            }
            if (debitDocument1.getInventoryMappingId() != null) {
                data.put(CustomerBillingMessage.CUSTOMER_INVENTORY_MAPP_ID, Collections.singletonList(debitDocument1.getInventoryMappingId()));
            } else {
                data.put(CustomerBillingMessage.CUSTOMER_INVENTORY_MAPP_ID, invMappingIds);
            }
            customerBillingMessage.setData(data);
            if (debitDocument1.getIsDirectChargeInvoice())
                customerBillingMessage.setType(Constants.INVOICE_TYPE.CUSTOMER_CHARGE);
            else if (debitDocument1.getInventoryMappingId() != null)
                customerBillingMessage.setType(Constants.INVOICE_TYPE.INVENTORY);
            else
                customerBillingMessage.setType(Constants.INVOICE_TYPE.CANCEL_REGENERATE);
            if (custPlanMapppingList.size() > 0 && custPlanMapppingList != null) {
                customerBillingMessage.setRenewalId(custPlanMapppingList.get(0).getRenewalId());
            }
            if (childIds != null && childIds.size() > 0) {
                List<Integer> childListIds = new ArrayList<>(childIds);
                customerBillingMessage.setChildIds(childListIds);
            }
            List<CustomerServiceMapping> customerServiceMappingList = customerServiceMapRepository.findAllByCustId(debitDocument1.getCustomer().getId());
            custPlanMappingRepository.saveAll(custPlanMapppingList);
            messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
//        prepaidInvoiceService.createCNforChangePlanAndCancelAndRegenrate(customerBillingMessage, debitDocument1, customerServiceMappingList);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(e.getErrCode());
            RESP_CODE = e.getErrCode();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Cancel and cancelAndRegenerate invoice for Id : " + debitDocId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Cancel and cancelAndRegenerate invoice for Id : " + debitDocId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        MDC.remove("type");
        return genericDataDTO;
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
//            //logger.info(new ObjectMapper().writeValueAsString(response));
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

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
//            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
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

    @PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_ledger + "\",\"" + MenuConstants.post_cust_ledger + "\")")
    @PostMapping("/customerLedgers")
    public ResponseEntity<?> getCustomerLedgersByTime(@Valid @RequestBody CustomerLedgerDtlsPojo pojo, HttpServletRequest req)
            throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext
                    .getBean(CustomerLedgerDtlsService.class);
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);
            if (ledgerAllInfoPojo == null && ledgerAllInfoPojo.getCustId() == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Searching Ledger of Customer Id: " + pojo.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + "No ledger data found" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                response.put("customerLedgerDtls", ledgerAllInfoPojo);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Searching Ledger of Customer Id: " + pojo.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Searching Ledger of Customer Id: " + pojo.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Searching Ledger of Customer Id: " + pojo.getCustId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.invoice_revenue_report + "\")")
    @GetMapping("/daywisedbr")
    public List<CustomerDBRPojo> getDayWiseDBR(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                               @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
        //       String SUB_MODULE = getModuleNameForLog() + "[getDayWiseDBR]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<CustomerDBRPojo> response = new ArrayList<>();
        List<CustomDailyRevenue> customDailyRevenues = dbrService.getDailyDbrDeatils(startdate, endate);
        List<LocalDate> localDates = customDailyRevenues.stream().map(x -> x.getDate()).distinct().collect(Collectors.toList());
        if (localDates != null && !localDates.isEmpty()) {
            localDates.stream().forEach(data -> {
                List<CustomDailyRevenue> tmp = customDailyRevenues.stream().filter(x -> x.getDate().equals(data)).collect(Collectors.toList());
                CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
                customerDBRPojo.setDbr(tmp.stream().mapToDouble(t -> t.getRevenue()).sum());
                customerDBRPojo.setStartdate(data);
                customerDBRPojo.setPendingamt(tmp.stream().mapToDouble(t -> t.getOutstanding()).sum());
                response.add(customerDBRPojo);
            });

        }
        return response;
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/monthlywisedbr1")
    public List<CustomerDBRPojo> getMonthWiseDBR(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                                 @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate) throws Exception {
        //String SUB_MODULE = getModuleNameForLog() + "[getMonthWiseDBR]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        List<CustomerDBRPojo> response = new ArrayList<>();
        List<CustomMonthlyRevenue> customDailyRevenues = dbrService.getMonthWiseDbrDeatils();
        while (startdate.compareTo(endate) < 0) {
            LocalDate tmpStartDate = startdate;
            Integer month = tmpStartDate.getMonthValue();
            Integer year = tmpStartDate.getYear();

            CustomerDBRPojo customerDBRPojo = new CustomerDBRPojo();
            Double totDBR = customDailyRevenues.stream().filter(value -> (value.getMonth() == month && value.getYear().equalsIgnoreCase(String.valueOf(year)))).mapToDouble(CustomMonthlyRevenue::getRevenue).sum();
            Double totPending = customDailyRevenues.stream().filter(value -> (value.getMonth() == month && value.getYear().equalsIgnoreCase(String.valueOf(year)))).mapToDouble(CustomMonthlyRevenue::getOutstanding).sum();
            customerDBRPojo.setMonth(tmpStartDate.getMonth().toString() + "-" + tmpStartDate.getYear());
            customerDBRPojo.setDate(tmpStartDate);
            customerDBRPojo.setDbr(totDBR);
            customerDBRPojo.setPendingamt(totPending);
            response.add(customerDBRPojo);
            startdate = startdate.plusMonths(1);
        }
        return response;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/getCustomer")
    public CustomerDBRResponse getbycustid(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                           @RequestParam("endate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endate,
                                           @RequestParam("custid") Long custid, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            CustomerDBRResponse list = customerLedgerDtlsService.getbycustid(startdate, endate, custid);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer revenue report by id : " + custid + " with Startdate : " + startdate + " and enddate : " + endate + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer revenue report by id : " + custid + " with Startdate : " + startdate + " and enddate : " + endate + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return new CustomerDBRResponse();
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_DBR_REPORT_ALL + "\",\"" + AclConstants.OPERATION_DBR_REPORT_VIEW + "\")")
    @GetMapping("/getDbrByCustomerIdAndDate")
    public List<CustomerDBRPojo> getDbrByCustomerIdAndDate(@RequestParam("startdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startdate,
                                                           @RequestParam("custid") Long custid) throws Exception {
        List<CustomerDBRPojo> list = customerLedgerDtlsService.getbycustid(startdate, custid);
        return list;
    }

    @GetMapping("/AdjustedPaymentAgainstInvoice/{invoiceId}")
    public ResponseEntity<?> PaymentInvoiceDetailsFetch(@PathVariable Integer invoiceId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "fetch");
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<ViewAdjustedPaymentPojo> creditDocumentList = prepaidInvoiceService.FindAdjustedPaymentAgainstBill(invoiceId);
            response.put("Paymentlist", creditDocumentList);

            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }


    @PostMapping("/trial/invoice/search")
    public ResponseEntity<?> searchTrialInvoice(@ModelAttribute SearchTrialDebitDocsPojo entity, @RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "isInvoiceVoid", required = false, defaultValue = "false") boolean isInvoiceVoid, @RequestParam(name = "isOutstandingDue", required = false, defaultValue = "false") boolean isOutstandingDue) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<TrialDebitDocumentPojo> page = null;
        try {
            if (entity != null) {
                TrialDebitDocService trialDebitDocService = SpringContext.getBean(TrialDebitDocService.class);
                requestDTO = setDefaultPaginationValues(requestDTO);
                page = trialDebitDocService.searchTrialInvoice(entity, requestDTO, false, isInvoiceVoid, isOutstandingDue);

                response.put("invoicesearchlist", page.getContent());
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, page);
    }


    @PostMapping("/trial/invoice/search/customerPortal")
    public ResponseEntity<?> searchTrialInvoiceCustomerPortal(@ModelAttribute SearchTrialDebitDocsPojo entity, @RequestBody PaginationRequestDTO requestDTO) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<TrialDebitDocumentPojo> page = null;
        try {
            if (entity != null) {
                TrialDebitDocService trialDebitDocService = SpringContext.getBean(TrialDebitDocService.class);
                requestDTO = setDefaultPaginationValues(requestDTO);
                page = trialDebitDocService.searchTrialInvoice(entity, requestDTO,true, false, false);

                response.put("invoicesearchlist", page.getContent());
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response, page);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.pre_cust_invoices_list + MenuConstants.post_cust_invoices_list + "\")")
    @GetMapping("/paymentmapping/{invoiceId}")
    public ResponseEntity<?> PaymentInvoiceMapping(@PathVariable Integer invoiceId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            //prepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(prepaidInvoiceService.class);
            List<CreditDocument> creditDocumentList = creditDocService.FindPaymentToMap(invoiceId);
            if (creditDocumentList != null && !creditDocumentList.isEmpty())
                creditDocumentList = creditDocumentList.stream().filter(x -> !x.getStatus().equals(CommonConstants.PAYMENT_STATUS_PENDDING)).collect(Collectors.toList());
//			CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
//			creditDebitMappingPojo.setInvoiceId(invoiceId);
//			creditDebitMappingPojo.setCreditDocumentList(creditDocumentList);
//			prepaidInvoiceService.InvoicePaymentDone(creditDebitMappingPojo);
            response.put("Paymentlist", creditDocumentList);

            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PAYMENT_SYSTEM_ALL + "\",\"" + AclConstants.OPERATION_PAYMENT_SYSTEM_VIEW + "\")")
    @GetMapping("/payment/search")
    public ResponseEntity<?> searchPaymentWithPagination(@ModelAttribute SearchPaymentPojo entity, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<CreditDocumentSearchPojo> creditDocList = null;
        PaginationRequestDTO requestDTO = new PaginationRequestDTO();
        requestDTO.setPage(entity.getPage());
        requestDTO.setPageSize(entity.getPageSize());
        try {
            if (entity != null) {
                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
                creditDocList = creditDocService.searchPayment(entity, requestDTO);
                if (null != creditDocList && 0 < creditDocList.getSize()) {
                    response.put("creditDocumentPojoList", creditDocList.getContent());
                } else {
                    response.put("creditDocumentPojoList", new ArrayList<>());
                }
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch credit note" + entity.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch credit note" + entity.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch credit note" + entity.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, creditDocList);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CUSTOMER_ALL + "\",\""
            + AclConstants.OPERATION_CUSTOMER_VIEW_PAYMENT_HISTORY + "\")")
    @GetMapping(value = "/paymentHistory/{custId}")
    public GenericDataDTO getPaymentHistory(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getDbcdrProcessing()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id:" + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            Customers customers = subscriberService.get(custId);
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id:" + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            List<PaymentHistoryDTO> paymentHistories = creditDocService.getByCustId(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } catch (Exception e) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.record_payment + "\",\"" + MenuConstants.GENERATE_CREDIT_NOTE + "\")")
    @PostMapping(value = "/record/payment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecordPayment(@Valid @RequestParam String spojo, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        RecordPaymentPojo pojo = null;
        try {
            pojo = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(spojo, new TypeReference<RecordPaymentPojo>() {
            });
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
//            creditDocService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            if (dbrService.getLoggedInUser().getBuIds() != null && dbrService.getLoggedInUser().getBuIds().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            if (file != null) {
                creditDocService.uploadDocument(pojo, file);
            }
//            Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
            String status = customersRepository.findStatusById(pojo.getCustomerid());
            CreditDocument saved;
//            CreditDocument savedTrialCreditDocument;
            if(status != null  && (status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING))){
                saved = creditDocService.saveTrialCreditDocument(pojo, false, false, false, null, null, null, false, null, null);
                response.put("CreditDocId", saved.getId());
//                savedTrialCreditDocument = creditDocService.saveTrialCreditDocument(pojo, false, false, false, null, null, null, false, null, null);
//                response.put("CreditDocId", savedTrialCreditDocument.getId());
            }
            else {
                 saved = creditDocService.save(pojo, false, false, false, null, null, null, false, null, null);
                response.put("CreditDocId", saved.getId());
            }
            RESP_CODE = APIConstants.SUCCESS;
//
            Long refId=saved.getCustomer().getId().longValue();
            String refName=saved.getCustomer().getCustname();
           String invoiceNumber = saved.getInvoiceNumber();
            Integer invoiceId = saved.getInvoiceId() != null ? saved.getInvoiceId() : null;
            Integer debitDocGraceDays=null;
            String paymode = pojo.getPaymode() == null ? "" : pojo.getPaymode().toUpperCase();
            String module;
            String operation;

            if (paymode.toUpperCase().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                module = AclConstants.ACL_CLASS_CREDIT_NOTES;
                operation = AclConstants.OPERATION_ADD_CREDIT_NOTES;
            } else {
                module = AclConstants.ACL_CLASS_RECORD_Payment;
                operation = AclConstants.OPERATION_ADD_RECORD_Payment;
            }
            auditLogService.addAuditEntry(module,operation,req.getRemoteAddr(),saved.getRemarks(),refId,refName,invoiceNumber,invoiceId,debitDocGraceDays);

            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + pojo.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (FileNotCreatedException fnce) {
            RESP_CODE = HttpStatus.UNPROCESSABLE_ENTITY.value();
            response.put(APIConstants.ERROR_TAG, "Failed to create file: " + fnce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + pojo.getType() +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + fnce.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + pojo.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (DataIntegrityViolationException dive) {
            RESP_CODE = HttpStatus.CONFLICT.value();
            String errorMessage = extractConstraintMessage(dive);
            response.put(APIConstants.ERROR_TAG, errorMessage);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + pojo.getType() +
                    LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + errorMessage +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            ex.printStackTrace();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + pojo.getType() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    private String extractConstraintMessage(DataIntegrityViolationException dive) {
        Throwable cause = dive.getCause();
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
                return "Reference No Already exists.";
            }
            cause = cause.getCause();
        }
        return "Data integrity violation occurred.";
    }

    @PostMapping(value = "/checksufficentwallteblanceforplan")
    public GenericDataDTO checkSufficentwallteBlanceForplan(
            @RequestBody CustomerPlanDTO requestDTOs,
            @RequestHeader(value = "rf", defaultValue = "bss") String requestFrom,
            HttpServletRequest req) throws Exception {

        String SUBMODULE = MODULE + " [checkSufficentwallteBlanceForplan()] ";
        TraceContext traceContext = tracer.currentSpan().context();

        org.slf4j.MDC.put("type", "Fetch");
        org.slf4j.MDC.put("userName", getLoggedInUser().getUsername());
        org.slf4j.MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        org.slf4j.MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO response = new GenericDataDTO();

        try {

            CustomerLedgerDtlsPojo pojo = new CustomerLedgerDtlsPojo();
            pojo.setCustId(requestDTOs.getCustId());
            ResponseEntity<?> walletResponse = getWalletAmount(pojo, req);
            Object walletData = walletResponse.getBody();


            Double walletBalance = 0.0;
            if (walletData instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) walletData;
                Object val = map.get("customerWalletDetails");
                if (val != null) {
                    walletBalance = Double.parseDouble(val.toString());
                }
            }


            PostpaidPlan plan = postpaidPlanRepo.findPostpaidPlanById(requestDTOs.getPlanId());
            if (plan == null) {
                response.setResponseCode(HttpStatus.NOT_FOUND.value());
                response.setResponseMessage("Plan not found for planId: " + requestDTOs.getPlanId());
                return response;
            }
            Double offerPrice = plan.getOfferprice();


            if (walletBalance < offerPrice) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Insufficient wallet balance");
                return response;
            }
            else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Wallet balance is sufficient");
                response.setData(walletData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while checking wallet balance: " + e.getMessage());
        } finally {
            org.slf4j.MDC.remove("type");
            org.slf4j.MDC.remove("userName");
            org.slf4j.MDC.remove(LogConstants.TRACE_ID);
            org.slf4j.MDC.remove("spanId");
        }

        return response;
    }

    @PostMapping("/wallet")
    public ResponseEntity<?> getWalletAmount(@Valid @RequestBody CustomerLedgerDtlsPojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext.getBean(CustomerLedgerDtlsService.class);
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);
            if (infoPojo == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                if (ledgerAllInfoPojo.getCustomerLedgerInfoPojo() != null)
                    response.put("customerWalletDetails", -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance());
                else
                    response.put("customerWalletDetails", 0.0);

                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }
//this api for the advanced dunning email and sms call through feign client in cms
    @PostMapping("/wallets/list")
    public ResponseEntity<?> getWalletAmounts(@Valid @RequestBody List<CustomerLedgerDtlsPojo> pojoList, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        HashMap<String, Object> response = new HashMap<>();
        Map<Long, Double> walletAmounts = new HashMap<>();

        try {
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext.getBean(CustomerLedgerDtlsService.class);

            for (CustomerLedgerDtlsPojo pojo : pojoList) {
                CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
                CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(), infoPojo);

                if (infoPojo == null) {
                    walletAmounts.put(Long.valueOf(pojo.getCustId()), 0.0);
                } else {
                    double closingBalance = ledgerAllInfoPojo.getCustomerLedgerInfoPojo() != null
                            ? -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance()
                            : 0.0;
                    walletAmounts.put(Long.valueOf(pojo.getCustId()), closingBalance);
                }
            }

            RESP_CODE = APIConstants.SUCCESS;
            response.put("customerWalletDetails", walletAmounts);

            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch multiple customer wallets"
                    + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS
                    + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch multiple customer wallets"
                    + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED
                    + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch multiple customer wallets"
                    + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED
                    + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return apiResponse(RESP_CODE, response);
    }



    @PostMapping("/invoicePaymentAdjust")
    public ResponseEntity<?> InvoicePaymentAdjust(@RequestBody CreditDebitMappingPojo creditDebitMappingPojo)
            throws Exception {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {

            Integer invoiceId = creditDebitMappingPojo.getInvoiceId();
            DebitDocument debitDocument = debitDocRepository.findById(invoiceId).orElse(null);
            CreditDocService prepaidInvoiceService = SpringContext.getBean(CreditDocService.class);
            String Response = prepaidInvoiceService.adjustManualPaymentToInvoice(creditDebitMappingPojo);
            response.put("InvoicePamentAdjust", Response);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/invoicemapping/{paymentId}")
    public ResponseEntity<?> InvoiceToPaymentMapping(@PathVariable Integer paymentId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "fetch");
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            List<ViewAdjustedInvoicePojo> viewAdjustedInvoicePojos = creditDocService.FindInvoiceToPayment(paymentId);
            response.put("Invoicelist", viewAdjustedInvoicePojos);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    /**
     * get invoice detail from list
     *
     * @param customerid
     * @return
     */
    @GetMapping("/invoiceList/byCustomer/{customerid}")
    public ResponseEntity<?> getAllInvoiceByCustomerShow(@PathVariable Integer customerid, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (customerid != null) {
                List<DebitShowDocumentPojo> debitDocPojoList = prepaidInvoiceService
                        .convertResponseModelIntoShowPojo(prepaidInvoiceService.getAllByCustomerShow(customerid));
                response.put("invoiceList", debitDocPojoList);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch invoice list by customer id : " + customerid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch invoice list by customer id : " + customerid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch invoice list by customer id : " + customerid + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/invoiceListForCreditNote/byCustomer/{customerid}")
    public ResponseEntity<?> getAllInvoiceByCustomerForCreditNote(@PathVariable Integer customerid) {
        MDC.put("type", "fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (customerid != null) {
                PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
//                List<DebitDocumentPojoCreditNote> debitDocPojoList = prepaidInvoiceService.convertResponseModelIntoPojoCreditNoteOptimized(prepaidInvoiceService.getAllByCustomerForCreditNoteOptimized(customerid));
                List<Map<String, Object>> invoiceList = prepaidInvoiceService.getAllByCustomerForCreditNoteFast(customerid);
                response.put("invoiceList", invoiceList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_invoice_master + "\",\"" + MenuConstants.prepaid_invoice_master + "\")")
    @GetMapping("/billrun/All")
    public ResponseEntity<?> getBillRunList() {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            List<BillRun> billRunList = billRunService.getAllEntities();
            response.put("billRunlist", billRunService.convertResponseModelIntoPojo(billRunList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @ApiOperation(value = "Used to check whether Revenue Management service is up or not.")
    @GetMapping("/serviceStatus")
    public String checkServiceStatus() {
        try {
            logger.debug("Revenue Management Service is Up");
            return "{\"success\": true,\"message\": \"Revenue Management Service is Up.\"}";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/invoice/send/{debitdocid}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseEntity<?> getInvoiceToCustomer(@PathVariable Integer debitdocid) {
        MDC.put("type", "fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (debitdocid != null) {
                PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
                prepaidInvoiceService.sendInvoiceEmail(debitdocid);
                response.put("msg", "invoice mail send successfully");
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.PREPAID_CUSTOMER_PLANS_PROMIS_TO_PAY + "\",\"" + MenuConstants.POSTPAID_CUSTOMER_PLANS_PROMIS_TO_PAY + "\")")
    @PostMapping(value = "/promiseToPayInBulk")
    public GenericDataDTO addPromiseToPay(@RequestBody PromiseToPayPojoInBulk request, HttpServletRequest req) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getBasicSubscriberDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == request.getCustId()) {
                genericDataDTO.setResponseMessage("Please provide customer id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            Set<CustServiceMappingDTO> customersMapppingPojos = subscriberService.addPromiseToPayInBulk(request);

            List<Integer> custServiceId = customersMapppingPojos.stream().map(i -> i.getId()).collect(Collectors.toList());
//            List<Integer>custPackId =  request.getPromiseToPay().stream().map(PromiseToPayPojo::getCustPlanMapping).collect(Collectors.toList());
            Set<CustPlanMappping> custPlanMapppingList = new HashSet<>();
//            for (Integer id : custPackId){
//                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(id).get();
//                if (custPlanMappping.getPlanGroup()!=null){
//                    custPlanMapppingList.addAll(custPlanMappingRepository.findAllByRenewalId(custPlanMappping.getRenewalId()));
//                }
//
//            }
            custPlanMapppingList.addAll(custPlanMappingRepository.findAllByCustServiceMappingIdIn(custServiceId));
            List<CustPlanMapppingDto> dtos = new ArrayList<>();
            for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
                CustPlanMapppingDto custPlanMapppingDto = new CustPlanMapppingDto(custPlanMappping, "Ingrace");
                dtos.add(custPlanMapppingDto);
            }
            UpdateCustplanMappingMessage updateCustplanMappingMessage = new UpdateCustplanMappingMessage();
            updateCustplanMappingMessage.setCustPlanMapppingDtos(dtos);
//            messageSender.send(updateCustplanMappingMessage,RabbitMqConstants.QUEUE_SEND_CUSTPLANMAPPINGS_REVENUE_TO_CMS_P2P);
            kafkaMessageSender.send(new KafkaMessageData(updateCustplanMappingMessage, UpdateCustplanMappingMessage.class.getSimpleName()));

            genericDataDTO.setDataSet(customersMapppingPojos);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(e.getErrCode());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @RequestMapping(value = "/documentForInvoice/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (null == customers.get()) {
                return ResponseEntity.notFound().build();
            }
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(docId.intValue());
            if (null == creditDocument) {
                return ResponseEntity.notFound().build();
            }

            resource = pdfUtil.getBarterDoc(customers.get().getUsername().trim(), creditDocument.get().getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.payment_create_batch + "\")")
    @PostMapping("/createBatchPayment")
    public ResponseEntity<?> createBatchPayment(@Valid @RequestBody BatchPaymentPojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getFirstName());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentService batchPaymentService = SpringContext.getBean(BatchPaymentService.class);
            boolean status = batchPaymentService.isPaymentBatchAlreadyExists(pojo.getBatchName());
            if (!status) {
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                GenericDataDTO genericDataDTO = batchPaymentService.saveBatch(pojo);
                if (genericDataDTO.getResponseCode() != 200) {
                    throw new CustomValidationException(APIConstants.FAIL, "Unable save, Found duplicate CreditDocument entry under Batch Payment Mapping.", null);
                }
                response.put("msg", "Batch Payment Created Successfully");
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Batch Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, "Batch Payment Name Already Exists!");
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Batch Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "Batch Payment with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Batch Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff error{},exception{}", APIConstants.FAIL, e.getStackTrace());
        }
        return mvnoIds;
    }


    @PostMapping("/batchPayment/search")
    public ResponseEntity<?> searchBatchPayment(@ModelAttribute SearchBatchPaymentPojo entity, HttpServletRequest req, @RequestBody PaginationRequestDTO paginationRequestDTO) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (entity != null) {
                Page<BatchPaymentDetailPojo> batchPaymentDetailPojos = batchPaymentService.searchBatch(entity, paginationRequestDTO);
//                List<BatchPaymentDetailPojo> batchPaymentDetailPojos = batchPaymentService.serachBatch(creditDocuments);
                response.put("batchPaymentList", batchPaymentDetailPojos);

                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search batchPayment using keyword " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search batchPayment using keyword " + paginationRequestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.pay_batch_payment + "\")")
    @GetMapping("/batchPaymentDetailList")
    public ResponseEntity<?> getBatchPaymentDetailList(@RequestParam(name = "staffId", required = true) Long staffId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentService batchPaymentService = SpringContext.getBean(BatchPaymentService.class);
            if (staffId != null) {
                List<BatchPaymentDetailPojo> list = batchPaymentService.getBatchPaymentDetailListByStaffId(staffId);
                Collections.reverse(list);
                response.put("batchPaymentDetailList", list);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch batch payment details for staff" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch batch payment details for staff" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/batchPaymentApprove")
    public ResponseEntity<?> batchPaymentApprove(@Valid @RequestBody BatchAssignPojo batchAssignPojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentAssignmentService batchPaymentAssignmentService = SpringContext.getBean(BatchPaymentAssignmentService.class);
            batchPaymentAssignmentService.batchPaymentApprove(batchAssignPojo);
            response.put("message", "Batch Approved Successfully");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approving batch payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approving batch payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/batchPaymentReject")
    public ResponseEntity<?> batchPaymentReject(@Valid @RequestBody BatchAssignPojo batchAssignPojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentAssignmentService batchPaymentAssignmentService = SpringContext.getBean(BatchPaymentAssignmentService.class);
            batchPaymentAssignmentService.batchPaymentReject(batchAssignPojo);
            response.put("message", "Batch Rejected Successfully");
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Rejecting Batchpayment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Rejecting Batchpayment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.payment_batch_audit + "\")")
    @GetMapping("/batchPaymentAuditDetail")
    public ResponseEntity<?> getBatchPaymentAuditDetail(@RequestParam(name = "batchId", required = true) Long batchId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            BatchPaymentService batchPaymentService = SpringContext.getBean(BatchPaymentService.class);
            if (batchId != null) {
                List<BatchPaymentAuditDetails> list = batchPaymentService.getBatchPaymentAuditDetail(batchId);
                response.put("batchPaymentAuditDetails", list);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch batch payment audit details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getStackTrace());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch batch payment audit details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.pay_batch_pay_create + "\")")
    @PostMapping("/addBatchPaymentMappingInExistingBatch")
    public ResponseEntity<?> addBatchPaymentMapping(@Valid @RequestBody BatchPaymentPojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentService batchPaymentService = SpringContext.getBean(BatchPaymentService.class);
            if (batchPaymentService.addBatchPaymentMappingInExistingBatch(pojo)) {
                response.put("message", "Mapping Added Successfully");
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create batch payment Mapping" + LogConstants.LOG_BY_NAME + pojo.getBatchName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getStackTrace());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create batch payment Mapping" + LogConstants.LOG_BY_NAME + pojo.getBatchName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/batchPaymentMappingList")
    public ResponseEntity<?> getBatchPaymentMapping(@RequestParam(name = "batchId", required = true) Long batchId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            BatchPaymentService batchPaymentService = SpringContext.getBean(BatchPaymentService.class);
            response.put("mappingList", batchPaymentService.getMappingList(batchId));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All BatchPaymentMapping list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All BatchPaymentMapping list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/invoiceByMvno/{mvnoId}")
    public ResponseEntity<?> getInvoiceByMvnoId1(@PathVariable(name = "mvnoId") Integer mvnoId, @RequestParam(name = "isInvoiceVoid", defaultValue = "false") boolean isInvoiceVoid, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<CustomDebitDocumentDTO> debitDocList = null;
        try {
            response.put("totalAmount", debitDocService.getTotalAmountDebitDocumentsByMvno(mvnoId, isInvoiceVoid));
            response.put("debitDocDetails", debitDocService.getDebitDocDetailByChargeAndMvno1(mvnoId, isInvoiceVoid));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Invoice of Mvno Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            RESP_CODE = APIConstants.SUCCESS;
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PostMapping("/invoiceByMvnoId/{mvnoId}")
    public ResponseEntity<?> getInvoiceByMvnoId(@PathVariable(name = "mvnoId") Integer mvnoId, @RequestBody DebitDocDetailDTO debitDocDetailDTO, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        List<CustomDebitDocumentDTO> debitDocList = null;
        try {
            response.put("totalAmount", debitDocService.getTotalAmountDebitDocumentsByMvno(mvnoId, debitDocDetailDTO.getIsInvoiceVoid()));
            response.put("debitDocDetails", debitDocService.getDebitDocDetailByChargeAndMvno(mvnoId, debitDocDetailDTO.getIsInvoiceVoid(), debitDocDetailDTO.getFromDate(), debitDocDetailDTO.getToDate()));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Invoice of Mvno Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Searching Invoice of Customer Id: " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @GetMapping(value = "/failedPaymentHistory/{custId}")
    public GenericDataDTO getFailedPaymentHistory(@PathVariable Integer custId, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getDbcdrProcessing()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            if (custId == null) {
                genericDataDTO.setResponseMessage("ID not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id:" + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            Customers customers = subscriberService.get(custId);
            if (customers == null) {
                genericDataDTO.setResponseMessage("Records not found!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id:" + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            }
            List<PaymentHistoryDTO> paymentHistories = creditDocService.getByCustIdForFailedPayments(custId);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(paymentHistories);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);


        } catch (Exception e) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer payment history by id " + custId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + MenuConstants.prepaid_invoice_master + "\" ,\"" + MenuConstants.postpaid_invoice_master + "\" ,\"" + MenuConstants.pre_cust_invoices + "\",\"" + MenuConstants.post_cust_invoices + "\")")
    @PostMapping("/mvnoInvoice/list/{invoiceId}")
    public ResponseEntity<?> invoiceListOfMvnoCustomer(@PathVariable Integer invoiceId, @RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<MvnoDebitDocDetailsPojo> mvnoDebitDocDetailsPojos = null;
        try {
            mvnoDebitDocDetailsPojos = prepaidInvoiceService.invoiceListOfMvnoCustomer(invoiceId, requestDTO);
            RESP_CODE = APIConstants.SUCCESS;
            response.put("mvnoDebitDocDetailsPojos", mvnoDebitDocDetailsPojos.getContent());
            logger.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Mvno Invoice List by invoiceId " + invoiceId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Mvno Invoice List by invoiceId " + invoiceId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, mvnoDebitDocDetailsPojos);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.record_payment + "\",\"" + MenuConstants.GENERATE_CREDIT_NOTE + "\")")
    @PostMapping(value = "/onlineCustomerPayment")
    public ResponseEntity<?> onlineCustomerPayment(@RequestBody OnlinePaymentDTO request, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        RecordPaymentPojo pojo = null;
        try {

            postpaidInvoiceService.onlineCustomerPayment(request);
            response.put("OnlinePayment", request);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Online Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Online Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            org.apache.log4j.MDC.remove("type");
            org.apache.log4j.MDC.remove("userName");
            org.apache.log4j.MDC.remove("traceId");
            org.apache.log4j.MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @RequestMapping(value = "/invoice/createXML/{debitdocid}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseEntity<?> createInvoiceXML(@PathVariable Integer debitdocid) {
        MDC.put("type", "fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (debitdocid != null) {
                PrepaidInvoiceService prepaidInvoiceService = SpringContext.getBean(PrepaidInvoiceService.class);
                Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitdocid);
                if (debitDocument.isPresent())
                    prepaidInvoiceService.setInvoiceXml(debitDocument.get());
                else
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invoice Not Found..!", null);
                response.put("msg", "XML Created successfully..!");
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error("Unable to Fetch Invoice ByCustomer  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}");
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to Fetch Invoice ByCustomer :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}");
        }
        MDC.remove("type");
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping(value = "/isp/recordPayment")
    public GenericDataDTO isprecordPayment(@RequestBody PaymentDto paymentDto, HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        RecordPaymentPojo pojo = null;
        try {
            pojo = creditDocService.getrecordPaymentPojo(paymentDto);
            CreditDocument creditDocument = creditDocService.save(pojo, false, false, false, null, null, null, false, 1, "superadmin");
            ApproveDto approveDto = new ApproveDto(pojo.getCustomerid(), creditDocument.getId());
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setData(approveDto);
            genericDataDTO.setResponseMessage("Payment Succesfull");
            logger.error(LogConstants.REQUEST_FROM + " Integration service " + LogConstants.REQUEST_FOR + " Isp Record Payment " + LogConstants.REQUEST_BY + " Superadmin " + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + " Integration service " + LogConstants.REQUEST_FOR + " Isp Record Payment " + LogConstants.REQUEST_BY + " Superadmin " + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + " Integration service " + LogConstants.REQUEST_FOR + " Isp Record Payment " + LogConstants.REQUEST_BY + " Superadmin " + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }

        return genericDataDTO;
    }


    @PostMapping("/cafWallet")
    public ResponseEntity<?> getCafWalletAmount(@Valid @RequestBody CustomerLedgerDtlsPojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            Customers customers=customersRepository.findCustomerById(pojo.getCustId());
            if(customers!=null){
                if(customers.getStatus().equalsIgnoreCase("NewActivation")) {
                    List<TrialDebitDocument> trialDebitDocumentList = trialDebitDocRepository.findByCustomerIdBasedOnBillrunStatusAndIsDeleteFalse(pojo.getCustId());
                    response.put("customerWalletDetails", debitDocService.getWalletBalanceForCaf(trialDebitDocumentList));
                    RESP_CODE=APIConstants.SUCCESS;
                }
            }else{
                throw  new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Customer with id :- "+pojo.getCustId()+" not found in revenue management !!",null);
            }
        }catch (FeignException fe){
            fe.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.ERROR_TAG, fe.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + fe.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    @GetMapping("/invoice/reSendPayload/{debitDocumentId}")
    public ResponseEntity<?> reSendIspPayload(@PathVariable Integer debitDocumentId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (debitDocumentId != null) {
                Optional<DebitDocument> document = debitDocRepository.findById(debitDocumentId);
                if (document.isPresent()) {
                    List<ClientService> clientServices = clientServiceRepository.findAllByNameAndMvnoIdIn(CommonConstants.IS_ISP_INVOICE_PAYLOAD_SEND, Arrays.asList(1));
                    if (clientServices != null && !clientServices.isEmpty()) {
                        if (clientServices.get(clientServices.size() - 1).getValue().equalsIgnoreCase("1")) {
                            IspInvoicePayload invoicePayload = postpaidInvoiceService.generateIspInvoiceDetailPayload(document.get().getId(), document.get().getCustomer().getId());
                            postpaidInvoiceService.sendIspPayloadToIntegration(invoicePayload, document.get());
                            response.put("msg", "ISP Payload Sent to Integration Service Successfully");
                        } else
                            response.put("msg", "Not Allowed to generate ISP Payload");
                    }
                } else {
                    response.put("noInvoiceFound", "Please ensure the invoice is generated!");
                }
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send ISP Payload to Integration" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send ISP Payload to Integration" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/invoice/reSendQrPayload/{debitDocumentId}")
    public ResponseEntity<?> reSendQrPayload(@PathVariable Integer debitDocumentId, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        try {
            invoiceIntigrationService.sendInvoiceDetailsToIntigration(Collections.singletonList(debitDocumentId));
            response.put("message", "Invoice data send to Revenue Authority successfully");
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send TRA Payload to Integration" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send TRA Payload to Integration" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getIspDebitdocNumbers")
    public List<DebitDocNumberMappingPojo> reSendIspPayload(HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        List<DebitDocNumberMappingPojo> numberMappingPojos = new ArrayList<>();
        try {
            List<Integer> custIds = mvnoRepository.findCustInvoiceRefNumber();
            numberMappingPojos = debitDocRepository.findDebitDocumentByCustId(custIds);
            response.put("InvoiceDetails", numberMappingPojos);
            response.put(String.valueOf(APIConstants.SUCCESS), "Success");
            RESP_CODE = APIConstants.SUCCESS;
        } catch (Exception e) {
            response.put("No Invoice Found", "No Invoice Found For MVNO");
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Isp DocList" + LogConstants.REQUEST_BY + "SuperAdmin" + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.ERROR_TAG);
            e.printStackTrace();
        }

        return numberMappingPojos;

    }
// this api for tax percentage which is used in dunning email and msg call through feign client in cms
    @PostMapping("/taxPercentage")
    public ResponseEntity<Map<Integer, List<Double>>> getTaxPercentagesByCustomers(
            @RequestBody List<Integer> customerIds, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        logger.info("Fetching tax percentages for customers: {}"+customerIds);

        try {

            if (customerIds == null || customerIds.isEmpty()) {
                logger.warn("Customer list is empty in request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.emptyMap());
            }

            Map<Integer, List<Double>> taxPercentages = prepaidInvoiceService.getTaxPercentagesByCustomers(customerIds);

            if (taxPercentages == null || taxPercentages.isEmpty()) {
                logger.warn("No tax data found for customers: {}"+ customerIds);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.emptyMap());
            }

//            logger.info("Successfully fetched tax percentages: {}"+ taxPercentages);
            return ResponseEntity.ok(taxPercentages);

        } catch (CustomValidationException ce) {
            logger.error("Validation error while fetching tax percentages", ce);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Collections.emptyMap());

        } catch (Exception e) {
            logger.error("Unexpected error while fetching tax percentages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyMap());

        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/getDebitdocNumbers")
    public ResponseEntity<Map<Integer, List<String>>> getDebitDocNumber(
            @RequestBody List<Integer> debitdocIds, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        logger.info("Fetching DebitDoc Number For debit doc Id: {}"+debitdocIds);

        try {

            if (debitdocIds == null || debitdocIds.isEmpty()) {
                logger.warn("Debit doc Id List Is Null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.emptyMap());
            }

            Map<Integer, List<String>> taxPercentages = prepaidInvoiceService.getDebitDOcIdByCustomers(debitdocIds);
            if (taxPercentages == null || taxPercentages.isEmpty()) {
                logger.warn("Debit doc Id List Is Null: {}"+ debitdocIds);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.emptyMap());
            }
//            logger.info("Successfully fetched tax percentages: {}"+ taxPercentages);
            return ResponseEntity.ok(taxPercentages);

        } catch (CustomValidationException ce) {
            logger.error("Validation error while fetching tax debitdoc List", ce);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Collections.emptyMap());

        } catch (Exception e) {
            logger.error("Validation error while fetching tax debitdoc List", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyMap());

        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/walletInternal")
    public ResponseEntity<?> getWalletAmountInternal(@RequestBody CustomerLedgerDtlsPojo pojo, HttpServletRequest req) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext.getBean(CustomerLedgerDtlsService.class);
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);
            if (infoPojo == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                if (ledgerAllInfoPojo.getCustomerLedgerInfoPojo() != null)
                    response.put("customerWalletDetails", -ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance());
                else
                    response.put("customerWalletDetails", 0.0);

                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer wallet" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
        }
        return apiResponse(RESP_CODE, response);
    }
    @PostMapping("/duedaywithgracdays")
    public GenericDataDTO updateGraceDays(@RequestBody GraceDayUpdateDto graceDayUpdateDto) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = prepaidInvoiceService.updateGraceDays(graceDayUpdateDto.getDebitDocId(),graceDayUpdateDto.getDebitDocGraceDays());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info(LogConstants.REQUEST_FROM + LogConstants.REQUEST_FOR + "update graceday " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE);
        }
        return genericDataDTO;
    }

    @GetMapping("/getLastUnpaidInvoice")
    public ResponseEntity<GenericDataDTO> getLatestUnpaidInvoice(@RequestParam("username") String username) {
        try {
            GenericDataDTO response = debitDocService.getLatestUnpaidInvoiceByUsername(username);
            return ResponseEntity.status(response.getResponseCode()).body(response);

        } catch (Exception e) {
            GenericDataDTO errorResponse = new GenericDataDTO();
            errorResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setResponseMessage("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/getDueDateAndBillAmount")
    public ResponseEntity<GenericDataDTO> getDueDateAndBillAmount(@RequestParam String acctno) {
        try {
            GenericDataDTO response = debitDocService.getDueDateAndTotalAmountByAcctno(acctno);
            return ResponseEntity.status(response.getResponseCode()).body(response);
        } catch (Exception e) {
            GenericDataDTO error = new GenericDataDTO();
            error.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.setResponseMessage("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getAllDueDateAndBillAmount")
    public ResponseEntity<GenericDataDTO> getAllDueDateAndBillAmount(@RequestParam String acctno) {
        try {
            GenericDataDTO response = debitDocService.getAllDueDateAndTotalAmountByAcctno(acctno);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GenericDataDTO error = new GenericDataDTO();
            error.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.setResponseMessage("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}



