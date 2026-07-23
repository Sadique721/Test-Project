package com.savbill.integrationsystem.paymentStatus.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.paymentStatus.Service.PaymentStatusService;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class PaymentStatusController {
    @Autowired
    private Tracer tracer;

    @Autowired
    private PaymentStatusService paymentStatusService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentStatusController.class);

    @GetMapping("/ByOrderId")
    public ResponseEntity<?> getOnlinePaymentAuditForCustomer(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
        logger.info("Endpoint /ByOrderId call start");
        HashMap<String, Object> response = new HashMap<>();
        Integer respCode = APIConstants.FAIL;

        try {
            TraceContext traceContext = (tracer.currentSpan() != null) ? tracer.currentSpan().context() : null;
            MDC.put("type", "Fetch");
            MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
            MDC.put("spanId", (traceContext != null) ? traceContext.spanIdString() : "N/A");

            List<CustomerPayment> onlinePayAuditList = paymentStatusService.getOnlinePayAuditListByOrderId(orderId);

            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList);
                response.put("Status", "Success");
                response.put("message", "Success");
                response.put("statusCode", 200);
                respCode = APIConstants.SUCCESS;
            } else {
                response.put("onlineAuditData", Collections.emptyList());
                response.put("Status", "No Record Found");
                response.put("message", "No records found for the given Order ID.");
                response.put("statusCode", 204);
                respCode = APIConstants.NOT_FOUND;
            }
        } catch (Exception e) {
            logger.error("Error fetching online payment audit for Order ID {}: {}", orderId, e.getMessage(), e);
            response.put("Status", "Failed");
            response.put("message", "An unexpected error occurred while processing your request.");
            response.put("statusCode", 500);
        } finally {
            MDC.clear();
        }
        logger.info("Endpoint /ByOrderId end start");
        return ResponseEntity.status(getHttpStatus(respCode)).body(response);
    }

    // Utility method to map response codes to HTTP status (Compatible with Java 8)
    private HttpStatus getHttpStatus(Integer responseCode) {
        if (APIConstants.SUCCESS.equals(responseCode)) {
            return HttpStatus.OK;
        } else if (APIConstants.NOT_FOUND.equals(responseCode)) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
