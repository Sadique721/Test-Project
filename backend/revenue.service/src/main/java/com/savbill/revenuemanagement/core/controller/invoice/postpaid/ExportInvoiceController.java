package com.savbill.revenuemanagement.core.controller.invoice.postpaid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.InvoiceIntigration.InvoiceIntigrationService;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.PaginationDetails;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.entity.debitdoc.ExportInvoiceAudit;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.customer.CustChargeDetailsRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.*;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.ExportInvoiceService;
import com.savbill.revenuemanagement.core.service.common.PdfUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.partner.PartnerLedgerDetailsService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.pojo.*;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.service.BatchPaymentService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class ExportInvoiceController {

    private static final Logger logger = Logger.getLogger(ExportInvoiceController.class);
    //@Autowired
    //MessageSender messageSender;
    private static final String MODULE = " [DebitDocController] ";
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;

//    @Autowired
//    private CustomersService customersService;
    public Integer SORT_ORDER;
    public String SORT_BY;
    @Autowired
    PdfUtil pdfUtil;
    @Autowired
    CreditDocRepository creditDocRepository;
    @Autowired
    SubscriberService subscriberService;
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
//    @Autowired
//    private AuditLogService auditLogService;
    @Autowired
    MessageReceiverWithThread messageReceiverWithThread;
    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    DbrService dbrService;
    @Autowired
    TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    KafkaMessageSender kafkaMessageSender;
    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    MvnoRepository mvnoRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;
    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;
    @Autowired
    private TrialDebitDocumentDetailRepository trialDebitDocumentDetailRepository;
    @Autowired
    private TrialDebitDocumentTAXRelRepository trialDebitDocumentTAXRelRepository;
    @Autowired
    private BatchPaymentService batchPaymentService;
    @Autowired
    private TrialDebitDocService entityService;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;
    @Autowired
    private ExportInvoiceService exportInvoiceService;
    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;
    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;
    @Autowired
    private Tracer tracer;
    @Autowired
    private InvoiceIntigrationService invoiceIntigrationService;

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

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
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

    @GetMapping("/generateBulkInvoicePdf")
    public ResponseEntity<?> generateBulkInvoicePdf(HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();

        try {
            String threadName = Constants.SCHEDULERS_NAME.MANUAL_INVOICE_PDF_GENERATE;
            exportInvoiceService.startInvoicePdfThread(false, getLoggedInUser().getMvnoId());
            response.put("message", "Invoice PDF Generation started successfully");
            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Successfully fetched tax percentages: {}"+ taxPercentages);
            return apiResponse(RESP_CODE, response);

        } catch (CustomValidationException ce) {
            logger.error("Error while starting invoice pdf generation", ce);
            ce.printStackTrace();
            response.put("message", "Custom Error while generating Invoice PDF : " + ce.getMessage());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            return apiResponse(RESP_CODE, response);

        } catch (Exception e) {
            logger.error("Error while starting invoice pdf generation", e);
            e.printStackTrace();
            response.put("message", "Error while generating Invoice PDF : " + e.getMessage());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            return apiResponse(RESP_CODE, response);

        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/sendBulkInvoiceNotication")
    public ResponseEntity<?> sendBulkInvoiceNotication(HttpServletRequest req) {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();

        try {
            exportInvoiceService.startInvoiceNotificationThread(false, getLoggedInUser().getMvnoId());
            response.put("message", "Invoice Sent successfully");
            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Successfully fetched tax percentages: {}"+ taxPercentages);
            return apiResponse(RESP_CODE, response);

        } catch (CustomValidationException ce) {
            logger.error("Custom Error while Invoice Sending ::: "+ ce.getMessage());
            ce.printStackTrace();
            response.put("message", "Error while Invoice Sending : " + ce.getMessage());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            return apiResponse(RESP_CODE, response);

        } catch (Exception e) {
            logger.error("Error while Invoice Sending :: "+ e.getMessage());
            e.printStackTrace();
            response.put("message", "Error while Invoice Sending ::: " + e.getMessage());
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            return apiResponse(RESP_CODE, response);

        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/getsearchexportinvoiceaudit")
    public ResponseEntity<?> getsearchexportinvoiceaudit(@RequestBody PaginationRequestDTO paginationRequestDTO) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(paginationRequestDTO.getPage()-1, paginationRequestDTO.getPageSize());
            Page<ExportInvoiceAudit> exportInvoiceAudits = exportInvoiceService.getsearchexportinvoiceaudit(paginationRequestDTO.getFilters(), paginationRequestDTO.getFromDate(), paginationRequestDTO.getToDate(),paginationRequestDTO.getFilterBy(),pageable);
            if (exportInvoiceAudits == null || exportInvoiceAudits.isEmpty()) {
                response.put(APIConstants.ERROR_TAG, "No ExportInvoiceAudit records found.");
                logger.warn("No  ExportInvoiceAudit records found.");
                return apiResponse(RESP_CODE, response);
            }

            RESP_CODE = APIConstants.SUCCESS;
            response.put("exportinvoiceaudit", exportInvoiceAudits);
            response.put("message", "ExportInvoiceAudit records fetched successfully.");
            logger.info("ExportInvoiceAudit records fetched successfully.");
        } catch (Exception e) {
            logger.error("Error fetching all ExportInvoiceAudit: " + e.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/getAllexportinvoiceaudit")
    public ResponseEntity<?> getAllexportinvoiceaudit(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam Boolean isExport) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
            Page<ExportInvoiceAudit> exportInvoiceAudits = exportInvoiceService.getAllexportinvoiceaudit(pageable, isExport);
            if (exportInvoiceAudits == null || exportInvoiceAudits.isEmpty()) {
                response.put(APIConstants.ERROR_TAG, "No ExportInvoiceAudit records found.");
                logger.warn("No  ExportInvoiceAudit records found.");
                return apiResponse(RESP_CODE, response);
            }

            RESP_CODE = APIConstants.SUCCESS;
            response.put("exportinvoiceaudit", exportInvoiceAudits);
            response.put("message", "ExportInvoiceAudit records fetched successfully.");
            logger.info("ExportInvoiceAudit records fetched successfully.");
        } catch (Exception e) {
            logger.error("Error fetching all ExportInvoiceAudit: " + e.getMessage());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponse(RESP_CODE, response);
    }
}



