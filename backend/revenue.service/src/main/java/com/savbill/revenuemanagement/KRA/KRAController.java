package com.savbill.revenuemanagement.KRA;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.ClientServiceConstant;
import com.savbill.revenuemanagement.core.constants.LogConstants;
import com.savbill.revenuemanagement.core.constants.UrlConstants;
import com.savbill.revenuemanagement.core.controller.common.ApiBaseController;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.savbill.revenuemanagement.core.util.ResponseUtil.apiResponse;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class KRAController extends ApiBaseController {
    @Autowired
    private Tracer tracer;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private KRAUtils kraUtils;
    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private CreditDocRepository creditDocRepository;

    private static final String MODULE = " [KRAController] ";

    private static final Logger log = LoggerFactory.getLogger(KRAController.class);
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;


    @PostMapping("/intg/kra/invoice")
    public ResponseEntity<?> processEtimsAddInvoiceBatch(@RequestBody List<Integer> debitDocIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<DebitDocument> debitDocuments = debitDocRepository.findAllById(debitDocIds);
            List<DebitDocument> unsyncedDebitDocuments = debitDocuments.stream()
                    .filter(debitDocument -> !Boolean.TRUE.equals(debitDocument.getIsKraSynced()))
                    .collect(Collectors.toList());
            response.put("requestedCount", debitDocIds.size());
            response.put("processedCount", unsyncedDebitDocuments.size());
            response.put("skippedSyncedCount", debitDocuments.size() - unsyncedDebitDocuments.size());
            if (unsyncedDebitDocuments.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No unsynced invoices found for KRA sync");
                return apiResponse(APIConstants.SUCCESS, response);
            }
            kraUtils.processEtimsAddInvoice(unsyncedDebitDocuments);
            response.put(APIConstants.MESSAGE, "Invoice sync payload prepared successfully");
            return apiResponse(APIConstants.SUCCESS, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All DebitDocument list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    @PostMapping("/intg/kra/creditNote")
    public ResponseEntity<?> processEtimsAddCreditNoteBatch(@RequestBody List<Integer> creditDocIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<CreditDocument> creditDocuments = creditDocRepository.findAllById(creditDocIds);
            List<CreditDocument> unsyncedCreditDocuments = creditDocuments.stream()
                    .filter(creditDocument -> !Boolean.TRUE.equals(creditDocument.getIsKraSynced()))
                    .collect(Collectors.toList());
            response.put("requestedCount", creditDocIds.size());
            response.put("processedCount", unsyncedCreditDocuments.size());
            response.put("skippedSyncedCount", creditDocuments.size() - unsyncedCreditDocuments.size());
            if (unsyncedCreditDocuments.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No unsynced credit notes found for KRA sync");
                return apiResponse(APIConstants.SUCCESS, response);
            }
            kraUtils.processEtimsAddCreditNote(unsyncedCreditDocuments);
            response.put(APIConstants.MESSAGE, "Credit note sync payload prepared successfully");
            return apiResponse(APIConstants.SUCCESS, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All CreditDocument list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

}
