package com.savbill.integrationsystem.PaywayIntigration;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.TradelanceIntigration.ForWardPaymentRequest;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class PaywayIntigrationController {

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private PaywayService paywayService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    private final Logger logger = LoggerFactory.getLogger(PaywayIntigrationController.class);

    @PostMapping("/api/account/validateaccount")
    public ResponseEntity<?> validateAccount(@Valid @RequestBody AccountValidationRequestDTO request, @RequestHeader("clientname") String clientName,
                                             HttpServletRequest req) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",req.getHeader("apikey"));
        try {
            String apikey = req.getHeader("apikey");
            logger.debug("Using Authorization token: {}", apikey);
            if (clientName == null || clientName.isEmpty()) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required")) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required", PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required"));
            }
            if ("payway".equals(clientName)) {
                try {
                    if(request.getAccountNo() == null || request.getAccountNo().isEmpty()){
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Account not found")) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Account not found",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Account not found"));
                    }
                    logger.info("Fetching customer data from revenueClient for account: " + request.getAccountNo());
                    ResponseEntity<?> response = revenueClient.getCustomerByAccountNo(request.getAccountNo(), apikey);
                    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found.")) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Customer data not found",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found."));
                    }

                    Map<String, Object> customerData = (Map<String, Object>) response.getBody();
                    customerData.put("Status",200);
                    customerData.put("StatusDescription", "SUCCESS");
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, validateMobileNumber(customerData, request.getPhoneNumber()) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "SUCCESS",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return validateMobileNumber(customerData, request.getPhoneNumber());
                } catch (FeignException.BadRequest e) {
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.badRequest().body(new ErrorResponseDTO(extractErrorMessage(e))), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.badRequest().body(new ErrorResponseDTO(extractErrorMessage(e)));
                } catch (FeignException.NotFound e) {
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.badRequest().body(new ErrorResponseDTO(extractErrorMessage(e))), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(extractErrorMessage(e)));
                } catch (FeignException e) {
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(extractErrorFromFeignException(e))), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(extractErrorFromFeignException(e)));
//                } catch (Exception e) {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
                }
            } else if ("tradelance".equals(clientName)) {
                try {
                    if(request.getAccountNo() == null || request.getAccountNo().isEmpty()){
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Account not found")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Account not found",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Account not found"));
                    }
                    logger.info("Fetching customer data from revenueClient for account: " + request.getAccountNo());
                    ResponseEntity<?> response = revenueClient.getCustomerByAccountNo(request.getAccountNo(), apikey);
                    if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found.")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Customer data not found",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found."));
                    }
                    Map<String, Object> customerData = (Map<String, Object>) response.getBody();
                    ResponseEntity<?> responseData  = validateMobileNumber(customerData, request.getPhoneNumber());
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, responseData, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return responseData;
                } catch (FeignException.BadRequest e) {
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException.NotFound e) {
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException e) {
                    String errorMessage = extractErrorFromFeignException(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                }
            } else {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed"));
            }
        } catch (CustomValidationException e) {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
            ApplicationLogger.logger.error("Validation error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
        } catch (Exception e) {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_VALIDATE_ACCOUNT, request.getAccountNo());
            ApplicationLogger.logger.error("Account validation failed: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage()));
        }
    }

//    @PostMapping("/api/payway/listpackages")
    public ResponseEntity<?> getListpackages(@RequestBody PackageListRequest request,@RequestHeader("clientname") String clientName,
                                            HttpServletRequest req) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",req.getHeader("apikey"));
        MDC.put("type", "FETCH");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        String apikey = req.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
            if (clientName == null || clientName.isEmpty() ) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required"));
            }
            if(request.getAccountNo()== null|| request.getAccountNo().isEmpty()){
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Customer Account No. is required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Customer Account No. is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Bad Request. Customer Account No. is required"));
            }
            if ("payway".equals(clientName)) {
                try {
                    ApplicationLogger.logger.info("Calling CMSClient.getCustomerPlanListByAccountNo with accountNo={} and apikey={}",
                            request.getAccountNo(), apikey);
                    ResponseEntity<?> response = cmsClient.getCustomerPlanListByAccountNo(request.getAccountNo(), apikey);
                    ApplicationLogger.logger.info("CMSClient.getCustomerPlanListByAccountNo response status={} body={}",
                            response.getStatusCode(), response.getBody());
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, response, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                    return response;
                } catch (FeignException.BadRequest e) {
                    ApplicationLogger.logger.error("Feign BadRequest when calling CMSClient.getCustomerPlanListByAccountNo. " +
                            "Status: {}, Response Body: {}", e.status(), e.contentUTF8(), e);
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException.NotFound e) {
                    ApplicationLogger.logger.error("Feign NotFound when calling CMSClient.getCustomerPlanListByAccountNo. " +
                            "Status: {}, Response Body: {}", e.status(), e.contentUTF8(), e);
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException e) {
                    ApplicationLogger.logger.error("Generic FeignException when calling CMSClient.getCustomerPlanListByAccountNo. " +
                            "Status: {}, Response Body: {}", e.status(), e.contentUTF8(), e);
                    String errorMessage = extractErrorFromFeignException(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                }
            } else {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed"));
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Validation error: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
        } catch (Exception e) {
            ApplicationLogger.logger.error("Account validation failed: " + e.getMessage(), e);
            ApplicationLogger.logger.error("Unhandled exception occurred in getListpackages", e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_LIST_PACKAGES, request.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Something went wrong. Please try again later"));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
//    @PostMapping("/api/payway/accountbalance")
    public ResponseEntity<?> getAccountBalance(@Valid @RequestBody AccountBalanceRequest requestDTO,
                                               @RequestHeader("clientname") String clientName,
                                               HttpServletRequest req) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",req.getHeader("apikey"));
        MDC.put("type", "FETCH");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        String apikey = req.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
            if (clientName == null || clientName.isEmpty() ) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required"));
            }
            if (requestDTO.getAccountNo() == null || requestDTO.getAccountNo().isEmpty() ||
                    requestDTO.getPhoneNumber() == null || requestDTO.getPhoneNumber().isEmpty()) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Customer Account No. and valid Phone number are required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Customer Account No. and valid Phone number are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Customer Account No. and valid Phone number are required"));
            }
            if ("payway".equals(clientName)) {
                try {
                    String token = req.getHeader("Authorization");
                    logger.debug("Using Authorization token: {}", token);
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    ResponseEntity<?> responseEntity = revenueClient.getAcountbalance(requestDTO.getAccountNo(), requestDTO.getPhoneNumber(), apikey);
                    if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found.")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Customer data not found.",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDTO("Customer data not found."));
                    }
                    Map<String, Object> customerData = (Map<String, Object>) responseEntity.getBody();
                    if (customerData != null && customerData.containsKey("Balance")) {
                        Object balanceObj = customerData.get("Balance");

                        if (balanceObj instanceof Double) {
                            BigDecimal balance = BigDecimal.valueOf((Double) balanceObj).setScale(2, RoundingMode.HALF_UP);
                            customerData.put("Balance", balance);
                        } else if (balanceObj instanceof Number) {
                            BigDecimal balance = BigDecimal.valueOf(((Number) balanceObj).doubleValue()).setScale(2, RoundingMode.HALF_UP);
                            customerData.put("Balance", balance);
                        } else if (balanceObj instanceof String) {
                            try {
                                BigDecimal balance = new BigDecimal(balanceObj.toString()).setScale(2, RoundingMode.HALF_UP);
                                customerData.put("Balance", balance);
                            } catch (NumberFormatException e) {
                                customerData.put("Balance", BigDecimal.ZERO);
                            }
                        }
                    }
                    customerData.put("StatusCode", "SUCCESS");
                    ResponseEntity<?> responseEntity1 = validateMobileNumber(customerData, requestDTO.getPhoneNumber());
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, responseEntity1, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                    return responseEntity1 ;
                } catch (FeignException.BadRequest e) {
                    String errorMessage = extractErrorMessage(e);
                    logger.error("Bad request error: {}", errorMessage);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException.NotFound e) {
                    String errorMessage = extractErrorMessage(e);
                    logger.error("Account balance data not found: {}", errorMessage);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException e) {
                    String errorMessage = extractErrorFromFeignException(e);
                    logger.error("Feign client exception: {}", errorMessage);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                } catch (JsonProcessingException e) {
                    logger.error("JSON processing error: {}", e.getMessage());
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage()));
                }
            } else {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,"Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed"));
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Validation error: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Account validation failed: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));

        } catch (Exception e) {
            ApplicationLogger.logger.error("Account validation failed: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Something went wrong. Please try again later"));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

//    @PostMapping("/api/payway/newtx")
    public ResponseEntity<?> forwardPayment(@Valid @RequestBody ForWardPaymentRequest paymentRequest, @RequestHeader("clientname") String clientname, HttpServletRequest request) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("Authorization",request.getHeader("Authorization"));
        Map<String,Object> response = new HashMap<String,Object>();
        MDC.put("type", "UPDATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        String apikey = request.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
            if (clientname == null || clientname.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname is required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_NEWTX, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (apikey == null || apikey.isEmpty()) {
                response.put("error","Bad Request. Correct apikey is required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct apikey is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_NEWTX, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if ("payway".equals(clientname)) {
                try {
                    String token = request.getHeader("Authorization");
                    logger.debug("Using Authorization token: {}", token);
                    ResponseEntity<?> responseEntity = paywayService.processForwardPayment(paymentRequest, apikey);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,responseEntity , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success","PAYWAY-NEWTX",paymentRequest.getAccountNo());
                    return responseEntity;
                } catch (CustomValidationException ex) {
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_NEWTX, paymentRequest.getAccountNo());
                    logger.error("Validation error: {}", ex.getMessage(), ex);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponseDTO(ex.getMessage()));
                }
            } else {
                response.put("error","Authorization failed");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_NEWTX, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_NEWTX, paymentRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

//    @PostMapping("/api/payway/transactionstatus")
    public ResponseEntity<?> getTransactionStatus (@Valid @RequestBody TransactionRequestDTO transaction, @RequestHeader("clientname") String clientName,
                                                   HttpServletRequest request) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",request.getHeader("apikey"));
        Map<String, Object> response = new HashMap<>();
        MDC.put("type", "UPDATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        String apikey = request.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
                if (clientName == null || clientName.isEmpty()) {
                    response.put("error" , "Bad Request. Correct clientname is required");
                    response.put("StatusCode","FAILED");
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                if (apikey == null || apikey.isEmpty()) {
                    response.put("error" , "Bad Request. Correct apikey is required");
                    response.put("StatusCode","FAILED");
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct apikey is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                if ("payway".equals(clientName)) {
                        try {
                            String token = request.getHeader("Authorization");
                            logger.debug("Using Authorization token: {}", token);
                            ResponseEntity<?> responseEntity = paywayService.processTransactionStatus(transaction, apikey);
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction, responseEntity, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                            return responseEntity;
                        } catch (CustomValidationException ex) {
                            logger.error("Validation error: {}", ex.getMessage(), ex);
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new ErrorResponseDTO(ex.getMessage()));
                        }
                    } else {
                        response.put("error","Authorization failed");
                        response.put("StatusCode","FAILED");
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction,ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
        } catch (Exception e) {
                logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
                String errorMessage = e.getMessage();
                response.put("error", "Something went wrong. Please try again later");
                response.put("message", errorMessage);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),transaction, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_TRANSACTION_STATUS, transaction.getTransactionId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        } finally {
                    MDC.remove("type");
                    MDC.remove("userName");
                    MDC.remove("traceId");
                    MDC.remove("spanId");
        }
}
//    @PostMapping("/api/payway/reconciliationstatement")
    public ResponseEntity<?> getReconciliationStatement(@RequestBody ReconciliationReqDTO reconciliationReqDTO,
            @RequestHeader(value = "clientname", required = false) String clientName,
            HttpServletRequest request) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",request.getHeader("apikey"));
        Map<String, Object> response = new HashMap<>();
        MDC.put("type", "UPDATE");
        MDC.put("userName", getLoggedInUser().getUsername());
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        String apikey = request.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
            // Validate client name and authorization
            if (clientName == null || clientName.isEmpty() || apikey == null || apikey.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname and apikey are required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reconciliationReqDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_RECONSILE_STATEMENT, null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            // Validate client identity
            if (!"payway".equals(clientName)) {
                response.put("error","Authorization failed");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reconciliationReqDTO, ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_RECONSILE_STATEMENT, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            try {
                ResponseEntity<?> responseEntity = paywayService.processReconciliationstatement(reconciliationReqDTO, apikey);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reconciliationReqDTO, responseEntity, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_RECONSILE_STATEMENT, null);
                return responseEntity;
            } catch (CustomValidationException ex) {
                logger.error("Validation error: {}", ex.getMessage(), ex);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reconciliationReqDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_RECONSILE_STATEMENT, null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO(ex.getMessage()));
            }
        } catch (Exception e) {
            logger.error("Unexpected error in getReconciliationStatement: {}", e.getMessage(), e);
            response.put("error","Something went wrong. Please try again later.");
            response.put("StatusCode","FAILED");
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),reconciliationReqDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_RECONSILE_STATEMENT, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }



//    @PostMapping("/api/payway/upgrade")
    public ResponseEntity<?> upgradePlan (@Valid @RequestBody UpgradePlanRequest upgradePlanRequest, @RequestHeader("clientname") String clientName, HttpServletRequest request) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("apikey",request.getHeader("apikey"));
        Map<String, Object> response = new HashMap<>();
        MDC.put("type", "UPDATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        String apikey = request.getHeader("apikey");
        logger.debug("Using Authorization token: {}", apikey);
        try {
            if (clientName == null || clientName.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname and apikey are required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (apikey == null || apikey.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname and apikey are required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if ("payway".equals(clientName)) {
                try {
                    try {
                        String accountNo = upgradePlanRequest.getAccountNo();
                        String packageName = upgradePlanRequest.getPackageName();
                        ResponseEntity<?> responseEntity = cmsClient.upgradePlanByAccountNoAndPlanName(accountNo, packageName, apikey);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),responseEntity , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success","PAYWAY-UPGRADE",upgradePlanRequest.getAccountNo());
                        return responseEntity;
                    } catch (FeignException.BadRequest e) {
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException.NotFound e) {
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException e) {
                        String errorMessage = extractErrorFromFeignException(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                    }
                } catch (CustomValidationException ex) {
                    logger.error("Validation error: {}", ex.getMessage(), ex);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponseDTO(ex.getMessage()));
                }
            } else {
                response.put("error","Authorization failed");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Something went wrong. Please try again later",PaymentGatewayConfigurationConstant.AUDITCONSTANT.PAYWAY_UPGRADE,upgradePlanRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
    private String extractErrorMessage(FeignException e) {
        try {
            if (e.responseBody().isPresent()) {
                byte[] byteArray = new byte[e.responseBody().get().remaining()];
                e.responseBody().get().get(byteArray);
                String bodyStr = new String(byteArray, StandardCharsets.UTF_8);
                if (bodyStr.trim().startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode errorNode = objectMapper.readTree(bodyStr);
                if (errorNode.has("error")) {
                    return errorNode.get("error").asText();
                } else {
                    return "Unknown error occurred";
                }
                }else {
                    return bodyStr;
                }
            } else {
                return "Error response body is empty or null";
            }
        } catch (IOException ioException) {
            return "Error decoding response body: " + ioException.getMessage();
        }
    }
    private String extractErrorFromFeignException(FeignException e) {
        try {
            String responseBody = e.contentUTF8(); // Get the content of the exception
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("error").asText("Something went wrong while processing the request");
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Failed to extract error from FeignException: " + ex.getMessage(), ex);
            return "An unexpected error occurred";
        }
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {

        }
        return loggedInUser;
    }
    private ResponseEntity<?> validateMobileNumber(Map<String, Object> customerData, String phoneNumber) {
//        String fetchedMobileNo = String.valueOf(customerData.get("mobileNumber"));

//        String  s1= String.valueOf(phoneNumber.length());
//        if(!fetchedMobileNo.equals(s1)) {
//           throw new IllegalArgumentException("Invalid mobile number. Input value in Digit : " + fetchedMobileNo);
//        }else {
            customerData.remove("mobileNumber");
            return ResponseEntity.ok(customerData);
//        }
    }


}
