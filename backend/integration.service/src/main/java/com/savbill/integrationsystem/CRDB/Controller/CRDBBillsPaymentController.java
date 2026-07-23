package com.savbill.integrationsystem.CRDB.Controller;

import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBPaymentPostRequestDTO;
import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBVerificationRequestDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBPaymentPostResponseDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBVerificationResponseDTO;
import com.savbill.integrationsystem.CRDB.Service.CRDBBillsPaymentService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


// Exposes the two HTTP endpoints required by the CRDB Bank Institutional Bills Payment integration.

@Slf4j
@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class CRDBBillsPaymentController {

    @Autowired
    private CRDBBillsPaymentService crdbBillsPaymentService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    // VERIFICATION ENDPOINT

    @PostMapping("crdb/verify")
    public CRDBVerificationResponseDTO verifyBillPayment(
            @RequestBody CRDBVerificationRequestDTO requestDTO,
            HttpServletRequest request) {

        log.info("********** CRDB Verification request received **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = buildHeaders(request);
        CRDBVerificationResponseDTO responseDTO;

        try {
            responseDTO = crdbBillsPaymentService.verifyBillPayment(requestDTO, request);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    request.toString(),
                    ResponseEntity.status(HttpStatus.OK).body(responseDTO),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    null,
                    null,
                    "POST",
                    "Success",
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.CRDB_TRANSACTION,
                    requestDTO.getPaymentReference());

            return responseDTO;

        } catch (Exception e) {
            log.error("CRDB verifyBillPayment – unhandled exception: ", e);
            log.error("Error for paymentReference={}", requestDTO.getPaymentReference(), e);

            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            CRDBVerificationResponseDTO errorResponse = CRDBVerificationResponseDTO.error(500, "Internal server error: " + errorMessage);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    request.toString(),
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    null,
                    null,
                    "POST",
                    e.getMessage(),
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.CRDB_TRANSACTION,
                    requestDTO.getPaymentReference() != null ? requestDTO.getPaymentReference() : "");

            return errorResponse;
        }
    }
    // PAYMENT POST ENDPOINT
    @PostMapping("/crdb/payment")
    public CRDBPaymentPostResponseDTO postPaymentNotification(
            @RequestBody CRDBPaymentPostRequestDTO requestDTO,
            HttpServletRequest request) {

        log.info("********** CRDB Payment Post request received **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = buildHeaders(request);
        CRDBPaymentPostResponseDTO responseDTO = new CRDBPaymentPostResponseDTO();

        try {
            responseDTO = crdbBillsPaymentService.postPaymentNotification(requestDTO, request);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    request.toString(),
                    ResponseEntity.status(HttpStatus.OK).body(responseDTO),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    null,
                    null,
                    "POST",
                    "Success",
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.CRDB_TRANSACTION,
                    requestDTO.getPaymentReference() // use account number
            );

            return responseDTO;

        } catch (Exception e) {
            log.error("CRDB postPaymentNotification – unhandled exception: ", e);

            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            CRDBPaymentPostResponseDTO errorResponse = CRDBPaymentPostResponseDTO.error(500, "Internal server error: " + errorMessage);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    request.toString(),
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    null,
                    null,
                    "POST",
                    e.getMessage(),
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.CRDB_TRANSACTION,
                    requestDTO.getTransactionRef() != null ? requestDTO.getTransactionRef() : "");

            return errorResponse;
        }
    }

    private HttpHeaders buildHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("headers", request.getHeaderNames().toString());
        return headers;
    }
}