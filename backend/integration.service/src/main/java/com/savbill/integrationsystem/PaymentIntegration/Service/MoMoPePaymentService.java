package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.MoMoPeDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.MoMoPePayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
public class MoMoPePaymentService {


    @Autowired
    PaymentConfigService paymentConfigService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Value(value = "${momo.scheduled.time}")
    private long scheduleTime;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private RevenueClient revenueClient;


    /**
     * In this Method we Firsst Initiate Payment by Sending Data to CMS and save in customerpayment table
     * after that we fetch geteway parameters for momope and select target enviroment and based on that
     * we further process the request.
     * case 1: target enviroment: sandbox
     *    for that methos performs -->  createUser, generateApiKey, getAccessToken, requestToPay.
     * case 2: target environment : production
     *   for that method performs: getAccessToken, requestToPay
     */

    public GenericDataDTO momoPePaymentInitiateService(CustomerPaymentDTO customerPaymentDTO, String authToken) throws Exception {
        String accesToken;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        if(customerPaymentDTO.getPlanId() != null) {
            ApplicationLogger.logger.info("MOMO_PAY >> Initiate Service | customerId={} | planId={} | mobile={}",
                    customerPaymentDTO.getCustomerId(), customerPaymentDTO.getPlanId(), customerPaymentDTO.getMobileNumber());
            walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(),authToken);
            planPrice =  cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(),authToken);
            ApplicationLogger.logger.info("MOMO_PAY >> WalletAmount={} | PlanPrice={}", walletAmount, planPrice);

        }
        customerPaymentDTO.setWalletAmount(walletAmount);
        customerPaymentDTO.setPlanPrice(planPrice);
        customerPaymentDTO.setPayerMobileNumber(customerPaymentDTO.getMobileNumber());

        CustomerPayment customerPayment = sendAndSaveMomoPeDataForPayment(customerPaymentDTO, "Initiate");
        if(customerPayment != null) {
            MoMoPeDTO fieldsDTO = fetchGatewayParameters(customerPaymentDTO);
            ApplicationLogger.logger.info("Initiated Momo payment request and fetch Gateway Params..");
            if (fieldsDTO.getTargetEnvironment().equals("sandbox")) {
//            createUser(fieldsDTO);
//            String apiKey = generateApiKey(fieldsDTO);
                ApplicationLogger.logger.info("Execute Momo payment request for sandbox Environment having referenceId : " + customerPaymentDTO.getCustomerUUID());
                accesToken = getAccessToken(fieldsDTO, fieldsDTO.getApiKey(), customerPaymentDTO.getMvnoId(), customerPayment);

                ApplicationLogger.logger.info("MOMO_PAY >> Calling requestToPay | referenceId={} | environment={}",
                        customerPaymentDTO.getCustomerUUID(), fieldsDTO.getTargetEnvironment());
                CloseableHttpResponse response = requestToPay(fieldsDTO, accesToken, customerPaymentDTO.getMvnoId(), customerPayment);
                ApplicationLogger.logger.info("MOMO_PAY >> requestToPay Response Status={} | Reason={}",
                        response.getStatusLine().getStatusCode(),
                        response.getStatusLine().getReasonPhrase());

                genericDataDTO = setApiResponseForMomoPe(customerPaymentDTO, response);
            } else {
                ApplicationLogger.logger.info("Execute Momo payment request for Production Environment having referenceId : " + customerPaymentDTO.getCustomerUUID());
                accesToken = getAccessToken(fieldsDTO, fieldsDTO.getApiKey(), customerPaymentDTO.getMvnoId(), customerPayment);
                CloseableHttpResponse response = requestToPay(fieldsDTO, accesToken, customerPaymentDTO.getMvnoId(), customerPayment);
                genericDataDTO = setApiResponseForMomoPe(customerPaymentDTO, response);
            }
        }
        else{
            ApplicationLogger.logger.info("Transaction is not intiated due to error in system");
        }
        return genericDataDTO;
    }

   /* public void createUser(MoMoPeDTO dto) throws UnsupportedEncodingException {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.CREATE_USER);
            request.addHeader("X-Reference-Id", dto.getReferenceId());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", dto.getSubscriptionKey());
            String jsonBody = String.format("{\nproviderCallbackHost: \"%s\"\n}", extractDomain(dto.getCallBackUrl()));
            StringEntity entity = new StringEntity(jsonBody);
            request.setEntity(entity);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            ApplicationLogger.logger.debug("MoMo Payment Initiate User Response : \n\n" + responseBody + "\n\n");
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 201) {
                    errorMessage = rootNode.path("message").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Create User In MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }
                if (response_code >= 400) {
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "User Created Successfully for refernceId: " + dto.getReferenceId() + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }

        } catch (Exception e) {
            ApplicationLogger.logger.error("Error Creating user", e.getMessage());
        }
    }

    public String generateApiKey(MoMoPeDTO dto) {
        String apiKey = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.GENERATE_API_KEY + dto.getReferenceId() + URLConstants.MoMoPeUrlConstants.API_KEY);
            request.setHeader("Ocp-Apim-Subscription-Key", dto.getSubscriptionKey());
            CloseableHttpResponse response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 201) {
                    errorMessage = rootNode.path("message").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Generate Api Key In MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }
                if (response_code >= 400) {
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for api key generation : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    apiKey = rootNode.path("apiKey").asText();
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Api Key Created Successfully for refernceId: " + dto.getReferenceId() + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Generating APi Key ", e.getMessage());
        }
        return apiKey;
    }*/

    public String getAccessToken(MoMoPeDTO dto, String apiKey, Integer mvnoId,CustomerPayment customerPayment) throws Exception {
        String accessToken = null;
        try {
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.GENERATE_ACCESS_TOKEN);

            // Prepare Basic Auth Header (Base64 encode referenceId:apiKey)
            String authHeader = Base64.getEncoder().encodeToString((dto.getApiUser() + ":" + dto.getApiKey()).getBytes());

            // Set headers
            request.setHeader("Authorization", "Basic " + authHeader);
            request.setHeader("Ocp-Apim-Subscription-Key", dto.getSubscriptionKey());
            request.setHeader("Content-Type", "application/json");

            // Execute the request

            CloseableHttpResponse response = client.execute(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            ApplicationLogger.logger.info("Request sent for Token genaration for user: "+dto.getApiUser());
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    errorMessage = rootNode.path("message").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Generate Token In MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.GENERATE_ACCESS_TOKEN, null, response, request, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
                }
                if (response_code >= 400) {
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for token generation : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    accessToken = rootNode.path("access_token").asText();
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Token Generate Successfully for user : " + dto.getApiUser()  + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.GENERATE_ACCESS_TOKEN, null, response, request, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Generating Token ", e.getMessage());

        }
        return accessToken;
    }

    public CloseableHttpResponse requestToPay(MoMoPeDTO dto, String accessToken, Integer mvnoId,CustomerPayment customerPayment) {
        CloseableHttpResponse response = null;
        HttpPost request = new HttpPost();
        Long responseTime = null;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        String responseBody = null;
        try {
            CloseableHttpClient client = HttpClients.createDefault();
//            Set necessary headers required
            request = new HttpPost(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY);
            request.setHeader("Authorization", "Bearer " + accessToken);
            request.addHeader("X-Callback-Url", dto.getCallBackUrl());
            request.addHeader("X-Reference-Id", dto.getReferenceId());
            request.addHeader("X-Target-Environment", dto.getTargetEnvironment());
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", dto.getSubscriptionKey());
            StringEntity entity = new StringEntity(dto.getJsonPayload());
            entity.setContentType("application/json");
            request.setEntity(entity);
            response = client.execute(request);
            ApplicationLogger.logger.info("Request for payment sent to MomoPe for user : "+dto.getApiUser()+"with referenceId: "+dto.getReferenceId());
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity responseEntity = response.getEntity();
            responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 202) {
                    errorMessage = rootNode.path("message").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Performing Request To Pay In MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY, null, response, request, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
                }
                if (response_code >= 400) {
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for request to pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY, null, response, request, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
                } else {
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Payment Request executed successfully for referenceId: "+dto.getReferenceId() + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    ApplicationLogger.logger.info("Scheduler will executed for fetching payment status after "+scheduleTime+" minutes");
                    apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY, null, response, request, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
                    scheduleForFetchingPaymentStatus(dto,accessToken,mvnoId);
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY, null, response, request, null, responseTime, e.getMessage(), requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , APIConstants.MOMO_PAY,customerPayment.getOrderId().toString());
            ApplicationLogger.logger.error("Error While Performing Request To Pay  ", e.getMessage());
        }
        return response;
    }

    public GenericDataDTO setApiResponseForMomoPe(CustomerPaymentDTO customerPaymentDTO, CloseableHttpResponse response) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> responseMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
        try {
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            if (response != null) {
                response_code = response.getStatusLine().getStatusCode();
                dataMap.put("customerUserName", customerPaymentDTO.getCustomerUserName());
                dataMap.put("customerUUID", customerPaymentDTO.getCustomerUUID());
                dataMap.put("orderId", customerPaymentDTO.getOrderId());
                dataMap.put("merchantName", customerPaymentDTO.getMerchantName());
//                Handle Multiple response codes
                if (response_code != 202) {
                    errorMessage = rootNode.path("message").asText();
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Initiating MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }
                if (response_code >= 400) {
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Initiating MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    // Map the response to a HashMap
                    responseMap.put("code", response_code);
                    responseMap.put("data", dataMap);
                    responseMap.put("message", "Request To Pay Execute Successfully For MomoPe");
                    genericDataDTO.setData(responseMap);
                    genericDataDTO.setResponseCode(HttpStatus.SC_OK);
                    genericDataDTO.setResponseMessage("success");
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Payment Initiatied Successfully" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }

        } catch (Exception e) {
            ApplicationLogger.logger.error("Failed to initiate MomoPe payment " + e.getMessage());
        }
        return genericDataDTO;
    }
    private void scheduleForFetchingPaymentStatus(MoMoPeDTO dto, String accessToken, Integer mvnoId) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call paymentStatus API
            paymentStatus(dto,accessToken,mvnoId);
        }, scheduleTime, TimeUnit.MINUTES);
    }

    public void paymentStatus(MoMoPeDTO dto,String accessToken, Integer mvnoId){
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet();
        Long responseTime = null;
        String responseBody = null;
        try{
            List<CustomerPayment> paymentList = customerPaymentRepository.findAllByStatusAndIsScheduled("Successful",false);
            for(CustomerPayment payments : paymentList) {
                singlePaymentStatus(dto,accessToken,payments);
            }
        }catch (Exception e){
            apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS, null, response, request,  responseTime, e.getMessage(), requestInitiationTime, responseBody, null, null,null);
            e.printStackTrace();
            ApplicationLogger.logger.error("Failed to fetch MomoPe Transaction status " + e.getMessage());
        }
    }

    public void singlePaymentStatus(MoMoPeDTO dto,String accessToken, CustomerPayment payments){
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet();
        Long responseTime = null;
        String responseBody = null;
        try{

                CloseableHttpClient client = HttpClients.createDefault();
                ApplicationLogger.logger.info("====MoMoPeDTO "+dto.toString()+"===");
                ApplicationLogger.logger.info("====accessToken "+accessToken+"===");
                ApplicationLogger.logger.info("====payments "+payments.toString()+"===");
                ApplicationLogger.logger.warn("::::::::::::::: Request URL for fetching payment status to MomoPe for referenceId: "+ URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS + payments.getCustomerUUID());
                request = new HttpGet(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS + payments.getCustomerUUID());
                request.setHeader("Authorization", "Bearer " + accessToken);
                request.addHeader("X-Target-Environment", dto.getTargetEnvironment());
                request.setHeader("Ocp-Apim-Subscription-Key", dto.getSubscriptionKey());
                response = client.execute(request);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                ApplicationLogger.logger.warn("::::::::::::::: Request execute for fetching payment status to MomoPe for referenceId: "+payments.getCustomerUUID());
                HttpEntity responseEntity = response.getEntity();
                responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                ApplicationLogger.logger.info("====responseBody "+responseBody+"===");
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String errorMessage = null;
                if (response != null) {
                    Integer response_code = response.getStatusLine().getStatusCode();
                    ApplicationLogger.logger.info("====response_code "+response_code+"===");
                    if (response_code != 200) {
                        errorMessage = rootNode.path("mesage").asText();
                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Fetching transaction status In MoMe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSaveGetApiAuditsForMomoPay(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS, null, response, request,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,payments.getOrderId().toString());
                    }
                    if (response_code >= 400) {
                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for token generation : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS, null, response, request,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,payments.getOrderId().toString());
                    } else {
                        String financialTransactionId = rootNode.path("financialTransactionId").asText();
                        String customerUuid = rootNode.path("referenceId").asText();
                        String status = rootNode.path("status").asText();
                        ApplicationLogger.logger.warn("::::: MOMO PAY ::::::::::::::: "+LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Fetch Payment Status Successfully for referenceId : " + payments.getCustomerUUID() + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                        paymentIntegrationService.updateTransactionStatusAndSendToCMS(customerUuid, financialTransactionId, status, null);
                        apiAuditsService.extractDataAndSaveGetApiAuditsForMomoPay(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS, null, response, request,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,payments.getOrderId().toString());

                    }
                }
        }catch (Exception e){
            apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.MoMoPeUrlConstants.REQUEST_TO_PAY_TRANSACTION_STATUS, null, response, request,  responseTime, e.getMessage(), requestInitiationTime, responseBody, null, null,payments.getOrderId().toString());
            e.printStackTrace();
            ApplicationLogger.logger.error("Failed to fetch MomoPe Transaction status " + e.getMessage());
        }
    }



    public HashMap<String, Object> extractParamsAndPayload(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        try {
//            Fetch gateway parameters
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY, customerPaymentDTO.getMvnoId());
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_GATEWAY_URL);
            String subscriptionKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_SUBSCRIPTION_KEY);
            String callBackUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_CALLBACK_URL);
            String targetEnviroment = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_TARGET_ENVIROMENT);
            String currency = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_CURRENCY);
            String payerMessage = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_PAYERMESSAGE);
            String payeeNote = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_PAYEENOTE);
            String apiKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_API_KEY);
            String apiUser = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.MOMOPE.MOMOPE_API_USER);
//            Set Body or Json Payload required for request to pay.
            MoMoPePayment moMoPePayment = new MoMoPePayment();
            moMoPePayment.setAmount(customerPaymentDTO.getAmount());
            moMoPePayment.setCurrency(currency);
            moMoPePayment.setExternalId(customerPaymentDTO.getAccountNumber());
            MoMoPePayment.Payer payer = new MoMoPePayment.Payer();
            payer.setPartyIdType("MSISDN");
            payer.setPartyId(customerPaymentDTO.getMobileNumber());
            moMoPePayment.setPayer(payer);
            moMoPePayment.setPayerMessage(payerMessage);
            moMoPePayment.setPayeeNote(payeeNote);
            String jsonPayload = objectMapper.writeValueAsString(moMoPePayment);

            map.put("jsonPayload", jsonPayload);
            map.put("gatewayUrl", gatewayUrl);
            map.put("subscriptionKey", subscriptionKey);
            map.put("callBackUrl", callBackUrl);
            map.put("targetEnviroment", targetEnviroment);
            map.put("apiKey",apiKey);
            map.put("apiUser",apiUser);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While fetch Gateway Parametes For MomoPe", e.getMessage());
        }
        return map;

    }

    public MoMoPeDTO fetchGatewayParameters(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        HashMap<String, Object> gatewayParametersMap = extractParamsAndPayload(customerPaymentDTO);
        String referenceId = customerPaymentDTO.getCustomerUUID().toString();
        String jsonPayload = gatewayParametersMap.get("jsonPayload").toString();
        String gatewayUrl = gatewayParametersMap.get("gatewayUrl").toString();
        String subscriptionKey = gatewayParametersMap.get("subscriptionKey").toString();
        String callBackUrl = gatewayParametersMap.get("callBackUrl").toString();
        String targetEnviroment = gatewayParametersMap.get("targetEnviroment").toString();
        String apiKey = gatewayParametersMap.get("apiKey").toString();
        String apiUser = gatewayParametersMap.get("apiUser").toString();

        return new MoMoPeDTO(referenceId, gatewayUrl, subscriptionKey, callBackUrl, targetEnviroment,jsonPayload,apiKey,apiUser);
    }

    public CustomerPayment sendAndSaveMomoPeDataForPayment(CustomerPaymentDTO customerPaymentDTO, String status) {
        try {
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            Long orderId = generateId(customerPaymentDTO.getCustomerId().longValue());
            custPayDTOMessage.setOrderId(orderId);
            if (customerPaymentDTO.getCustomerId() != null)
                custPayDTOMessage.setCustId(customerPaymentDTO.getCustomerId());
            if (customerPaymentDTO.getPartnerId() != null)
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            if (customerPaymentDTO.getAccountNumber() != null)
                custPayDTOMessage.setAccountNumber(customerPaymentDTO.getAccountNumber());
            custPayDTOMessage.setPayment(Double.valueOf(customerPaymentDTO.getAmount()));
            custPayDTOMessage.setStatus(status);
            custPayDTOMessage.setGatewayStatus(status);
            custPayDTOMessage.setPlanId(customerPaymentDTO.getPlanId());
            custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
            custPayDTOMessage.setMerchantName(customerPaymentDTO.getMerchantName());
            custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
            custPayDTOMessage.setCustomerUsername(customerPaymentDTO.getCustomerUserName());
            custPayDTOMessage.setMvnoid(customerPaymentDTO.getMvnoId());
            custPayDTOMessage.setBuid(customerPaymentDTO.getBuid());
            custPayDTOMessage.setIsAdvancePayment(customerPaymentDTO.getIsAdvancePayment());
            if(customerPaymentDTO.getCustServiceMappingId() != null){
                custPayDTOMessage.setCustServiceMappingId(customerPaymentDTO.getCustServiceMappingId());
            }
            customerPaymentDTO.setOrderId(orderId.toString());
            if(customerPaymentDTO.getPartnerId() != null) {
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            }
            custPayDTOMessage.setCustomerUUID(customerPaymentDTO.getCustomerUUID());
            CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(getLatestId());
            if(customerPaymentDTO.getInvoiceId() != null){
                customerPayment.setInvoiceId(customerPaymentDTO.getInvoiceId());
            }if(customerPaymentDTO.getIsAdvancePayment()!=null) {
                customerPayment.setIsAdvancePayment(customerPaymentDTO.getIsAdvancePayment());
            }if(customerPaymentDTO.getWalletAmount() != null) {
                customerPayment.setWalletAmount(customerPaymentDTO.getWalletAmount());
            }if(customerPaymentDTO.getPlanPrice() != null) {
                customerPayment.setPlanPrice(customerPaymentDTO.getPlanPrice());
            }if(customerPaymentDTO.getPayerMobileNumber() != null) {
                customerPayment.setPayerMobileNumber(customerPaymentDTO.getPayerMobileNumber());
            }
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
            ApplicationLogger.logger.info("Send Initiated Request of MoMoPe Data to CMS for referenceId: "+customerPayment.getCustomerUUID());

            ApplicationLogger.logger.info("MOMO_PAY >> Sending Kafka message for OrderId={} | CustomerUUID={}",
                    orderId, customerPaymentDTO.getCustomerUUID());
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
            ApplicationLogger.logger.info("MOMO_PAY >> Saving CustomerPayment to DB | OrderId={} | CustomerUUID={}",
                    orderId, customerPaymentDTO.getCustomerUUID());
            return customerPaymentRepository.save(customerPayment);
        } catch (Exception e) {
            ApplicationLogger.logger.error("MOMO_PAY >> ERROR while processing sendAndSaveMomoPeDataForPayment | Message={} | StackTrace={}",
                    e.getMessage(), e);
            ApplicationLogger.logger.error("Error While Sending Data of MomoPe to CMS Through Kafka. ", e.getMessage());
            return null;
        }
    }

    public static String extractDomain(String fullUrl) {
        try {
            // Convert the string to a URL object
            URL url = new URL(fullUrl);
            // Extract the host (domain) from the URL
            return url.getHost();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return Long.parseLong(id);
    }

    public static int gen() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

//    public Long getLatestId(){
//        Long latestId = 0L;
//        latestId = customerPaymentRepository.getLatestId();
//        if(Objects.isNull(latestId)){
//            latestId = 1L;
//        }
//        else {
//            latestId = latestId+1L;
//        }
//        return latestId;
//    }


    public ResponseEntity<Map<String, Object>> getPaymentDetailsByHash(CustomerPaymentDTO customerPaymentDTO,String authToken) throws Exception {
        try {
            String hash = customerPaymentDTO.getHash();
            ResponseEntity<Map<String, Object>> response = cmsClient.getPaymentDetailsByHash(hash, authToken);
                return response;
        } catch (Exception e) {
            throw new Exception("Error fetching payment details", e);
        }
    }
}
