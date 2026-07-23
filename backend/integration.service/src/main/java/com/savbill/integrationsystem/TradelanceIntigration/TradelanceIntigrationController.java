package com.savbill.integrationsystem.TradelanceIntigration;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.AirtelIntigration.AirtelIntigrationService;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Service.MoMoPePaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentToWalletService;
import com.savbill.integrationsystem.PaywayIntigration.*;
import com.savbill.integrationsystem.PaywayIntigration.*;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
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
import java.util.*;

@RestController
@RequestMapping(value = URLConstants.BASE_PORTAL_API_URL)
public class TradelanceIntigrationController {

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private TradelanceService tradelanceService;

    @Autowired
    PaymentToWalletService paymentToWalletService;
    @Autowired
    private Tracer tracer;

    @Autowired
    AirtelIntigrationService airtelIntigrationService;

    @Autowired
    private MoMoPePaymentService moMoPePaymentService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    private final Logger logger = LoggerFactory.getLogger(TradelanceIntigrationController.class);

    @PostMapping("/api/tradelance/listpackages")
    public ResponseEntity<?> getListpackages(@Valid @RequestBody PackageListRequest request,
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
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required")) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required", PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required"));
            }
            if ("tradelance".equals(clientName)) {
                try {
                    ResponseEntity<?> response = cmsClient.getCustomerPlanListByAccountNo(request.getAccountNo(), apikey);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, response, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                    return response;
                } catch (FeignException.BadRequest e) {
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException.NotFound e) {
                    String errorMessage = extractErrorMessage(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                } catch (FeignException e) {
                    String errorMessage = extractErrorFromFeignException(e);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                }
            } else {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed"));
            }
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Validation error: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
        } catch (Exception e) {
            ApplicationLogger.logger.error("Account validation failed: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),request,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_LIST_PACKAGES, request.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Something went wrong. Please try again later"));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
    @PostMapping("/api/tradelance/accountbalance")
    public ResponseEntity<?> getAccountBalance(@Valid @RequestBody AccountBalanceRequest requestDTO,
                                               @RequestHeader("clientname") String clientName, HttpServletRequest req) {
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
        if (clientName == null || clientName.isEmpty()){
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Bad Request. Correct clientname is required"));
        }
        if (apikey == null || apikey.isEmpty()) {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct apikey is required")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct apikey is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct apikey is required"));
        }
        if (!"tradelance".equals(clientName)) {
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Authorization failed")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed.",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Authorization failed"));
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ResponseEntity<?> responseEntity = revenueClient.getAcountbalance(requestDTO.getAccountNo(), requestDTO.getPhoneNumber(), apikey);
            if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Customer data not found.")), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Customer data not found.",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
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
            customerData.remove("mobileNumber");
            ResponseEntity<?> responseEntity1 = validateMobileNumber(customerData, requestDTO.getPhoneNumber());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, responseEntity1, headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return responseEntity1 ;
        } catch (FeignException.BadRequest e) {
            String errorMessage = extractErrorMessage(e);
            logger.error("Bad request error: {}", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
        } catch (FeignException.NotFound e) {
            String errorMessage = extractErrorMessage(e);
            logger.error("Account balance data not found: {}", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
        } catch (FeignException e) {
            String errorMessage = extractErrorFromFeignException(e);
            logger.error("Feign client exception: {}", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage()));
        }catch (CustomValidationException e) {
        ApplicationLogger.logger.error("Validation error: " + e.getMessage(), e);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(e.getMessage()));
    }catch (Exception e) {
            logger.error("exception: {}", e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(req.getRequestURL().toString(),requestDTO, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" ,e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_ACCOUNT_BALANCE, requestDTO.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(e.getMessage()));
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/api/tradelance/newtx")
    public ResponseEntity<?> forwardPayment(@Valid @RequestBody ForWardPaymentRequest paymentRequest, @RequestHeader("clientname") String clientname, HttpServletRequest request) {
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
            if (clientname == null || clientname.isEmpty() || !("tradelance".equals(clientname))) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_NEWTX, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname is required"));
            }
            if (apikey == null || apikey.isEmpty()) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct apikey is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_NEWTX, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct apikey is required"));
            }
            String token = request.getHeader("apikey");
            logger.debug("Using Authorization token: {}", token);
            ResponseEntity<?> responseEntity = tradelanceService.processForwardPayment(paymentRequest, request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,responseEntity , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_NEWTX,paymentRequest.getAccountNo());
            return responseEntity;
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_NEWTX, paymentRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/api/tradelance/updatewifi")
    public ResponseEntity<?> changeWiFiSSIDPassword(@Valid @RequestBody ChangeWiFiPasswordRequest paymentRequest, @RequestHeader("clientname") String clientname, HttpServletRequest request) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("Authorization",request.getHeader("Authorization"));
        Map<String, Object> response = new HashMap<>();
        MDC.put("type", "UPDATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try {
            String apikey = request.getHeader("apikey");
            logger.debug("Using Authorization token: {}", apikey);
            if (clientname == null || clientname.isEmpty() || !("tradelance".equals(clientname))) {
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPDATEWIFI, paymentRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Bad Request. Correct clientname and apikey are required"));
            }
            ResponseEntity<?> responseEntity = tradelanceService.changeWiFiSSIDPassword(paymentRequest,apikey);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,responseEntity , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPDATEWIFI,paymentRequest.getAccountNo());
            return responseEntity;
        } catch (Exception e) {
            logger.error("exception: {}", e.getMessage());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),paymentRequest,ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , e.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPDATEWIFI, paymentRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Something went wrong. Please try again later"));
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @PostMapping("/api/tradelance/upgrade")
    public ResponseEntity<?> upgradePlan (@Valid @RequestBody UpgradePlanRequest upgradePlanRequest, @RequestHeader("clientname") String clientName,
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
                response.put("error" , "Bad Request. Correct clientname and apikey are required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (apikey == null || apikey.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname and apikey are required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname and apikey are required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if ("tradelance".equals(clientName)) {
                try {
                    try {
                        String accountNo = upgradePlanRequest.getAccountNo();
                        String packageName = upgradePlanRequest.getPackageName();
                        ResponseEntity<?> responseEntity = cmsClient.upgradePlanByAccountNoAndPlanName(accountNo, packageName, apikey);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),responseEntity , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success","TRADELANCE-UPGRADE",upgradePlanRequest.getAccountNo());
                        return responseEntity;
                    } catch (FeignException.BadRequest e) {
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException.NotFound e) {
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException e) {
                        String errorMessage = extractErrorFromFeignException(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                    }
                } catch (CustomValidationException ex) {
                    logger.error("Validation error: {}", ex.getMessage(), ex);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponseDTO(ex.getMessage()));
                }
            } else {
                response.put("error","Authorization failed");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),upgradePlanRequest.toString(),ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString())) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Something went wrong. Please try again later",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_UPGRADE,upgradePlanRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(response.toString()));
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @PostMapping("/api/tradelance/ussdpush")
    public ResponseEntity<?> ussdpush (@Valid @RequestBody UssdPushRequest ussdPushRequest, @RequestHeader("clientname") String clientName,
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
        logger.info("USSDPUSH >> API called | clientName={} | accountNo={} | amount={} | msisdn={} | apikey={}",
                clientName,
                ussdPushRequest.getAccountNo(),
                ussdPushRequest.getAmount(),
                ussdPushRequest.getMSISDN(),
                apikey);
        try {
            if (clientName == null || clientName.isEmpty()) {
                response.put("error" , "Bad Request. Correct clientname is required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct clientname is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (apikey == null || apikey.isEmpty()) {
                response.put("error" , "Bad Request. Correct apikey is required");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Bad Request. Correct apikey is required",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if ("tradelance".equals(clientName)) {
                try {
                    try {
                        String accountNo = ussdPushRequest.getAccountNo();
                        String mobileNumber = ussdPushRequest.getMSISDN();
                        GenericDataDTO genericDataDTO  = new GenericDataDTO();

                        if (mobileNumber == null || mobileNumber.length() < 12 ) {  // Ensure valid length
                            response.put("error", "Invalid Mobile Number");
                            response.put("StatusCode", "FAILED");
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Invalid Mobile Number.",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }

                        // Remove country code if it starts with "256"
                        if (mobileNumber.startsWith("256")) {
                            mobileNumber = mobileNumber.substring(3); // Remove the first three characters
                        }

                        String gateway = paymentToWalletService.getGatewayfromMobileNumber(mobileNumber);
                        logger.info("USSDPUSH >> Detected Gateway: {} for mobile {}", gateway, mobileNumber);



                        CustomerPaymentDTO customerPaymentDto;
                        String refId = null;
                        if (gateway.equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY)) {
                            logger.info("MOMO_PAY >> Initiating generateMoMoPayRequestByAccountNumber | accountNo={} | amount={} | apikey={}",
                                    accountNo, ussdPushRequest.getAmount(), apikey);
                            customerPaymentDto = cmsClient.generateMoMoPayRequestByAccountNumber(accountNo, ussdPushRequest.getAmount(), apikey);
                            logger.info("MOMO_PAY >> Response from generateMoMoPayRequestByAccountNumber: {}", customerPaymentDto);
                            if(customerPaymentDto.getCustomerId() == null){
                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), customerPaymentDto.getMobileNumber(), null);
                            }
                            customerPaymentDto.setAmount(ussdPushRequest.getAmount().toString());
                            customerPaymentDto.setMobileNumber(ussdPushRequest.getMSISDN());
                            customerPaymentDto.setAccountNumber(accountNo);

                            logger.info("MOMO_PAY >> Initiating momoPePaymentInitiateService | customerId={} | amount={} | mobile={} | uuid={}",
                                    customerPaymentDto.getCustomerId(),
                                    customerPaymentDto.getAmount(),
                                    customerPaymentDto.getMobileNumber(),
                                    customerPaymentDto.getCustomerUUID());
                            genericDataDTO = moMoPePaymentService.momoPePaymentInitiateService(customerPaymentDto, apikey);
                            refId = customerPaymentDto.getCustomerUUID();
                        } else if (gateway.equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL)) {
                            customerPaymentDto = cmsClient.generateAirtelRequestByAccountNumber(accountNo, ussdPushRequest.getAmount(), apikey);
                            if(customerPaymentDto.getCustomerId() == null){
                                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), customerPaymentDto.getMobileNumber(), null);
                            }
                            customerPaymentDto.setAmount(ussdPushRequest.getAmount().toString());
                            customerPaymentDto.setMobileNumber(mobileNumber);
                            customerPaymentDto.setAccountNumber(accountNo);
                            genericDataDTO = airtelIntigrationService.createAirtelpayment(customerPaymentDto, apikey);
                            refId = customerPaymentDto.getOrderId();
                        } else {
                            response.put("error", "Unsupported Mobile Network Prefix");
                            response.put("StatusCode", "FAILED");
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Unsupported Mobile Network Prefix.",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> responseBody = new HashMap<>();
                        if(genericDataDTO.getData() != null){
                            responseBody.putAll(objectMapper.convertValue(genericDataDTO.getData(), Map.class));
                            responseBody.put("RefId", refId); // Add RefId
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
                        } else {
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.OK).body(response) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Success",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                            return ResponseEntity.status(HttpStatus.OK).body(genericDataDTO.getData());
                        }

                    } catch (FeignException.BadRequest e) {
                        logger.error("FeignException.BadRequest occurred: {}", e.getMessage(), e);
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException.NotFound e) {
                        logger.error("FeignException.NotFound occurred: {}", e.getMessage(), e);
                        String errorMessage = extractErrorMessage(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(errorMessage));
                    } catch (FeignException e) {
                        logger.error("General FeignException occurred: {}", e.getMessage(), e);
                        String errorMessage = extractErrorFromFeignException(e);
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(),ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage)) , headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , errorMessage,PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(errorMessage));
                    }
                } catch (CustomValidationException ex) {
                    logger.error("CustomValidationException occurred: {}", ex.getMessage(), ex);
                    logger.error("Validation error: {}", ex.getMessage(), ex);
                    LocalDateTime requestCompletionTime = LocalDateTime.now();
                    Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                    apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(), ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage())), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , ex.getMessage(),PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponseDTO(ex.getMessage()));
                }
            } else {
                response.put("error","Authorization failed");
                response.put("StatusCode","FAILED");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(), ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in USSD PUSH API. Message={} | StackTrace={}", e.getMessage(), e);
            logger.error("Unexpected error in processForwardPayment: {}", e.getMessage(), e);
            String errorMessage = e.getMessage();
            response.put("error", "Something went wrong. Please try again later");
            response.put("message", errorMessage);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            apiAuditsService.setAuditForCallback(request.getRequestURL().toString(),ussdPushRequest.toString(), ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString()), headers , responseTime , requestInitiationTime , getLoggedInUser().getUsername() , getLoggedInUser().getMvnoId() , "POST" , "Authorization failed",PaymentGatewayConfigurationConstant.AUDITCONSTANT.TRADELANCE_USSDPUSH,ussdPushRequest.getAccountNo());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.toString());
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
