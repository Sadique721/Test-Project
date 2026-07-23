package com.savbill.integrationsystem.Mpesa.Controller;

import com.savbill.integrationsystem.Mpesa.Service.MpesaVodacomCallbackService;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
@RequiredArgsConstructor
public class MpesaVodacomC2BCallbackController {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${selcom.reverseFlow.mvnoId}")
    private Integer selcomReverseFlowMvnoId;

    private final MpesaVodacomCallbackService mpesaC2BFlowService;
    private final ApiAuditsService apiAuditsService;

    @PostMapping(
            value = "/mpesa/c2b/payment",
            consumes = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.TEXT_XML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.TEXT_XML_VALUE
            }
    )
    public String processIncomingPayment(@RequestBody String xml,
                                         HttpServletRequest request) {

        log.info("********** Inside processIncomingPayment method **********");
        LocalDateTime requestInitiationTime = LocalDateTime.now();

        HttpHeaders headers = new HttpHeaders();
        headers.set("headers", request.getHeaderNames().toString());

        try {
            String token = jwtUtil.generateJwtToken(selcomReverseFlowMvnoId.longValue());
            String ackXml = mpesaC2BFlowService.handleIncomingXml(xml, request, token);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    xml,
                    ResponseEntity.status(HttpStatus.OK).body(ackXml),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    getLoggedInUser() != null ? getLoggedInUser().getUsername() : null,
                    getLoggedInUser() != null ? getLoggedInUser().getMvnoId() : null,
                    "POST",
                    "Success",
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_VODACOM_TRANSACTION,
                    ""
            );

            return ackXml;
        } catch (Exception e) {
            log.error("error in processIncomingPayment", e);

            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            apiAuditsService.setAuditForCallback(
                    request.getRequestURL().toString(),
                    xml,
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID REQUEST"),
                    headers,
                    responseTime,
                    requestInitiationTime,
                    getLoggedInUser() != null ? getLoggedInUser().getUsername() : null,
                    getLoggedInUser() != null ? getLoggedInUser().getMvnoId() : null,
                    "POST",
                    e.getMessage(),
                    PaymentGatewayConfigurationConstant.AUDITCONSTANT.MPESA_VODACOM_TRANSACTION,
                    ""
            );

            throw new RuntimeException(e);
        }
    }

    private LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext.getAuthentication() != null) {
                loggedInUser = (LoggedInUser) securityContext.getAuthentication().getPrincipal();
            }
        } catch (Exception ignored) {
        }
        return loggedInUser;
    }
}