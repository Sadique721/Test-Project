package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.OnePay;
import com.savbill.integrationsystem.PaymentIntegration.DTO.OnePayDto;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.commonMethods.IntegrationGenericMethods;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OnePayIntegrationService {

    @Autowired
    private PaymentConfigService paymentConfigService;


    @Autowired
    private IntegrationGenericMethods integrationGenericMethods;
    @Autowired
    private CMSClient cmsClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private RevenueClient revenueClient;


    private static final Logger logger = LoggerFactory.getLogger(OnePayIntegrationService.class);

    public GenericDataDTO OnePayPaymentInitiateService(CustomerPaymentDTO customerPaymentDTO, String authToken) throws Exception {
        ApplicationLogger.logger.info("::::::::::Inside OnePayMoneyPaymentInitiateService Method:::::::::::::::");
        GenericDataDTO dataDTO = new GenericDataDTO();
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        String planName = null;

        try {
            planPrice = Double.valueOf(customerPaymentDTO.getAmount());
            walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(), authToken);
//            if (customerPaymentDTO.getPlanId() != null) {
//                logger.info("::::::::::Calling Revenue client:::::::::::::::");
//                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), authToken);
//                logger.info("::::::::::Calling CMS client:::::::::::::::");
//            }
            Map<String, Object> responseMap = cmsClient.getAccountNoByCustId(customerPaymentDTO.getCustomerId(), authToken);
            if (responseMap != null && responseMap.containsKey("accountNumber")) {
                String accountNumber = responseMap.get("accountNumber").toString();
                customerPaymentDTO.setAccountNumber(accountNumber);
            }
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            customerPaymentDTO.setPayerMobileNumber(customerPaymentDTO.getMobileNumber());



            ApplicationLogger.logger.info("::::::::::::Save Data for One Money in Customer Payment and Send to CMS:::::::");
            dataDTO = verifyPhoneNumber(customerPaymentDTO);
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage("Exception while performing payment for OnePay Money.");
            ApplicationLogger.logger.error(":::::::::::::::::::::::::::Error in Initiate Request for OnePay Money Payment:::::::::::::::::{}", e, e.getMessage());
            e.printStackTrace();
        }
        return dataDTO;
    }


    public GenericDataDTO verifyPhoneNumber(CustomerPaymentDTO customerPaymentDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CloseableHttpResponse response = null;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        String errorMessage = null;
        Integer response_code = 0;
        Long responseTime = null;

        try {

            OnePayDto onePayDto = fetchGatewayParameters(customerPaymentDTO);
            String jsonPayload = onePayDto.getJsonPayload(); // This is a JSON string
            JsonNode rootNode = objectMapper.readTree(jsonPayload);

            // Now extract individual values
            String merchantId = rootNode.path("MerchantUserId").asText();
            String channel = rootNode.path("Channel").asText();
            String onepayPhoneNo = rootNode.path("OnepayPhoneNo").asText();
            onePayDto.setChannel(channel);
            onePayDto.setOnepayPhoneNo(onepayPhoneNo);
            onePayDto.setMerchantUserId(merchantId);
//            String jsonBody = objectMapper.writeValueAsString(onePayDto.getJsonPayload());

            HttpPost httpPost = new HttpPost(onePayDto.getGatewayUrl() + URLConstants.OnePay.WALLET_VERIFY_PHONE_NUMBER);
            httpPost.setEntity(new StringEntity(onePayDto.getJsonPayload(), ContentType.APPLICATION_JSON));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            rootNode = objectMapper.readTree(responseBody);
            if (response != null) {
                response_code = rootNode.path("RespCode").asInt();
                if (response_code != 000) {
                    errorMessage = rootNode.path("RespDescription").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Performing Request To Verify PhoneNumber in OnePay Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.WALLET_VERIFY_PHONE_NUMBER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, onepayPhoneNo);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                }
                else if (response_code >= 400) {
                    errorMessage = rootNode.path("RespDescription").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for request to Verify PhoneNumber in OnePay Payment : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.WALLET_VERIFY_PHONE_NUMBER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, onepayPhoneNo);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                } else {
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Payment Request executed successfully for referenceId: " + null + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    ApplicationLogger.logger.info("Scheduler will executed for fetching payment status after " + onePayDto.getScheduleTime() + " minutes");
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.WALLET_VERIFY_PHONE_NUMBER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, onepayPhoneNo);
                    String responseCode = rootNode.path("RespCode").asText();
                    if (responseCode.equals("000")) {
                        CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
                        genericDataDTO = callDirectPaymentResponseApi(customerPaymentDTO, onePayDto, customerPayment);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage("Exception while verifying phone number.");
            ApplicationLogger.logger.error("Exception during OnePay verifyPhoneNumber call: ", e);
        }

        return genericDataDTO;
    }


    public OnePayDto fetchGatewayParameters(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        try {

            ApplicationLogger.logger.info(":::::::::Fetch GatewayParameters for OnePay Money:::::::::");
            HashMap<String, Object> gatewayParametersMap = extractParamsAndPayload(customerPaymentDTO);
            String jsonPayload = gatewayParametersMap.get("jsonPayload").toString();
            String gatewayUrl = gatewayParametersMap.get("gatewayUrl").toString();
            String callBackUrl = gatewayParametersMap.get("callBackUrl").toString();
            String sheduleTime = gatewayParametersMap.get("sheduleTime").toString();
            String secretKey = gatewayParametersMap.get("secretKey").toString();
            String channel = gatewayParametersMap.get("channel").toString();
            String merchantUserId = gatewayParametersMap.get("merchantUserId").toString();
            return new OnePayDto(gatewayUrl, callBackUrl, jsonPayload, secretKey, sheduleTime, channel, merchantUserId, customerPaymentDTO.getPayerMobileNumber());
        } catch (Exception e) {
            ApplicationLogger.logger.error("::::::::::::::::::::::::Error while fetch gateway parameters for OnePay Money:::::::::", e, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Object> extractParamsAndPayload(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        try {
//            Fetch gateway parameters
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.ONE_PAY, customerPaymentDTO.getMvnoId());
            String merchantId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEPAY_MERCHANTID);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEYPAY_SECRET_KEY);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEYPAY_GATEWAY_URL);
            String webHookUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEYPAY_CALLBACK_URL) + URLConstants.OnePay.CALLBACK_URL_ENDPOINT;
            String sheduleTime = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEYPAY_SCHEDULE_TIME);
            String channel = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.ONEPAY.ONEPAY_CHANNEL);
//            String hash = generateHash(channel,secretKey,merchantId,customerPaymentDTO.getPayerMobileNumber());
            String data = channel + merchantId + customerPaymentDTO.getPayerMobileNumber();
            String hash = generateHash(secretKey, data);
            /** Set payload for OnePay Money */
//            OnePay onePay = setPayload(customerPaymentDTO);
            OnePay obj = new OnePay();
            obj.setMerchantId(merchantId);
            obj.setChannel(channel);
            obj.setOnepayPhoneNo(customerPaymentDTO.getPayerMobileNumber());
            obj.setHash(hash);
            String jsonPayload = objectMapper.writeValueAsString(obj);

            map.put("jsonPayload", jsonPayload);
            map.put("gatewayUrl", gatewayUrl);
            map.put("secretKey", secretKey);
            map.put("callBackUrl", webHookUrl);
            map.put("sheduleTime", sheduleTime);
            map.put("channel", channel);
            map.put("merchantUserId", merchantId);

        } catch (Exception e) {
            ApplicationLogger.logger.error("::::::::::::::::::::::Failed to Extract Params and Payload of One Pay Payment for mvnoId::::::::::::::::::::::::" + customerPaymentDTO.getMvnoId());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return map;
    }


    public static String generateHash(String secretKey, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        hmac.init(keySpec);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) {
            hash.append(String.format("%02X", b));
        }
        return hash.toString();
    }

    public static String computeHashValue(
            String signature,
            String secretKey
    ) throws Exception {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(signature.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHexString(rawHmac);
    }

    // Convert byte array to hex string
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        String hexAlphabet = "0123456789ABCDEF";
        for (byte b : bytes) {
            result.append(hexAlphabet.charAt((b >> 4) & 0xF));
            result.append(hexAlphabet.charAt(b & 0xF));
        }
        return result.toString();
    }

    public GenericDataDTO callDirectPaymentResponseApi(CustomerPaymentDTO customerPaymentDTO, OnePayDto onePayDto, CustomerPayment customerPayment) throws Exception {
        String apiUrl = onePayDto.getGatewayUrl() + URLConstants.OnePay.DIRECT_API;
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        JsonObject agdRequest = new JsonObject();
        // Generate hash and sequence number
        int length = 30 + (int) (Math.random() * 21);
        CloseableHttpResponse response = null;
        String responseBody = null;
        String errorMessage = null;
        long responseTime = 0L;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        Long invoiceNo = null;
        try {

        String sequenceNo = RandomStringUtils.randomAlphanumeric(length).toUpperCase();
            if(customerPayment.getInvoiceId() != null) {
                invoiceNo = customerPayment.getInvoiceId().longValue();
            }else
            {
                invoiceNo = customerPayment.getOrderId();
            }
//        String hash = generateHashForDirectPayment(URLConstants.OnePay.VERSION, onePayDto.getChannel(), onePayDto.getMerchantUserId(), onePayDto.getOnepayPhoneNo(), customerPaymentDTO.getAmount(), URLConstants.OnePay.REMARK, customerPayment.getInvoiceId().toString(), sequenceNo, onePayDto.getCallBackUrl(), URLConstants.OnePay.EXPIRED_SECONDS, onePayDto.getSecretKey());
        String signature = URLConstants.OnePay.VERSION + onePayDto.getChannel() + onePayDto.getMerchantUserId() + onePayDto.getOnepayPhoneNo() + customerPaymentDTO.getAmount() +
                URLConstants.OnePay.REMARK + invoiceNo + sequenceNo + onePayDto.getCallBackUrl() + URLConstants.OnePay.EXPIRED_SECONDS;

        String hash = computeHashValue(signature, onePayDto.getSecretKey());
        // Prepare request payload
        agdRequest.addProperty("Version", URLConstants.OnePay.VERSION);
        agdRequest.addProperty("Channel", onePayDto.getChannel());
        agdRequest.addProperty("MerchantUserId", onePayDto.getMerchantUserId());
        agdRequest.addProperty("InvoiceNo",invoiceNo);
        agdRequest.addProperty("SequenceNo", sequenceNo);
        agdRequest.addProperty("Amount", customerPaymentDTO.getAmount());
        agdRequest.addProperty("Remark", URLConstants.OnePay.REMARK);
        agdRequest.addProperty("WalletUserID", onePayDto.getOnepayPhoneNo());
        agdRequest.addProperty("CallBackUrl", onePayDto.getCallBackUrl());
        agdRequest.addProperty("ExpiredSeconds", URLConstants.OnePay.EXPIRED_SECONDS);
        agdRequest.addProperty("HashValue", hash);


            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setEntity(new StringEntity(agdRequest.toString(), ContentType.APPLICATION_JSON));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);

            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity, "UTF-8");

            JsonNode rootNode = objectMapper.readTree(responseBody);
            System.out.println("Pyment response ..." + responseBody);
            int responseCode = rootNode.path("RespCode").asInt();

            if (responseCode != 000) {

                ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR +
                        "Error while AGD call : " + LogConstants.REQUEST_BY + MDC.get("userName") +
                        LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage +
                        LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.DIRECT_API, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPayment.getOrderId().toString());
                genericDataDTO = setApiResponseForOnePay(customerPaymentDTO, rootNode, responseCode);

            }

            else if (responseCode >= 400) {

                ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR +
                        "Invalid data sent for AGD : " + LogConstants.REQUEST_BY + MDC.get("userName") +
                        LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage +
                        LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.DIRECT_API, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPayment.getOrderId().toString());
                genericDataDTO = setApiResponseForOnePay(customerPaymentDTO, rootNode, responseCode);

            } else {
//                String respCode = rootNode.path("RespCode").asText();
//                String respDescription = rootNode.path("RespDescription").asText();
//
//                Map<String, Object> responseMap = new HashMap<>();
//                responseMap.put("code", respCode);
//                responseMap.put("message", respDescription);
//
//                genericDataDTO.setData(responseMap);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("success");
                String orderId = customerPaymentDTO.getOrderId();
                String status = rootNode.path("RespDescription").asText();
                String referIntegrationId = rootNode.path("ReferIntegrationId").asText();
                ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR +
                        "AGD call success for user : " + LogConstants.REQUEST_BY + MDC.get("userName") +
                        LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.DIRECT_API, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName(), customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPayment.getOrderId().toString());
                scheduleForFetchingPaymentStatus(customerPaymentDTO, onePayDto, referIntegrationId);
                paymentIntegrationService.updateIntegrationId(orderId, referIntegrationId);
                genericDataDTO = setApiResponseForOnePay(customerPaymentDTO, rootNode, responseCode);


            }

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error("Failed to call callDirectPayment Response API", ex);
            ex.printStackTrace();
            throw ex;
        }
      /*  finally {
            if (response != null) {
                response.close();
            }
        }*/
    }

    private void scheduleForFetchingPaymentStatus(CustomerPaymentDTO customerPaymentDTO, OnePayDto onePayDto, String referIntegrationId) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call orderStatus API
            try {
                orderStatus(customerPaymentDTO, onePayDto, referIntegrationId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, Long.valueOf(onePayDto.getScheduleTime().trim()), TimeUnit.MINUTES);
    }

    public void orderStatus(CustomerPaymentDTO customerPaymentDTO, OnePayDto onePayDto, String referIntegrationId) throws JsonProcessingException {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet();
        Long responseTime = null;
        String responseBody = null;
        JsonObject agdRequest = new JsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(dto.getJsonPayload());
        String orderId = customerPaymentDTO.getOrderId();
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            String signature = onePayDto.getMerchantUserId() + referIntegrationId;
            String hash = computeHashValue(signature, onePayDto.getSecretKey());
            agdRequest.addProperty("MerchantUserId", onePayDto.getMerchantUserId());
            //**** set actual ReferIntegrationID get from payment
            agdRequest.addProperty("ReferIntegrationID", referIntegrationId);
            agdRequest.addProperty("HashValue", hash);

            HttpPost httpPost = new HttpPost(onePayDto.getGatewayUrl() + URLConstants.OnePay.CHECK_STATUS);
            httpPost.setEntity(new StringEntity(agdRequest.toString(), ContentType.APPLICATION_JSON));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");

            response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            ApplicationLogger.logger.info("Request execute for fetching order status of OnePay for referenceId: " + orderId);
            HttpEntity responseEntity = response.getEntity();
            responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            String status = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                int responseCode = rootNode.path("RespCode").asInt();

                if (responseCode != 000) {
                    status = "FAILED";
                    errorMessage = rootNode.path("RespDescription").asText();
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Fetching Order status In Onepay" +
                            " Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.CHECK_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPaymentDTO.getOrderId().toString());
                }
                else if (response_code >= 400) {
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Fetching Order status of OnePay  : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.CHECK_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPaymentDTO.getOrderId().toString());
                } else {
                    status = rootNode.path("RespDescription").asText();
                    if (status.equalsIgnoreCase("Success")) {
                        status = "Successful";
                    }
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Fetch Order Status Successfully of OnePay Pay for orderId: " + orderId + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.CHECK_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPaymentDTO.getOrderId().toString());
                    paymentIntegrationService.updateStatusAndSendToCMS(orderId, referIntegrationId, status, null);
                }
            }


        } catch (Exception e) {
//            apiAuditsService.extractDataAndSavePostApiAudits(onePayDto.getGatewayUrl() + URLConstants.OnePay.DIRECT_API, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, null, customerPaymentDTO.getMvnoId(), APIConstants.ONE_PAY, customerPaymentDTO.getOrderId().toString());
            e.printStackTrace();
            ApplicationLogger.logger.error("Failed to fetch OnePay Pay Order status " + e.getMessage());
        }
    }

    public GenericDataDTO setApiResponseForOnePay(CustomerPaymentDTO customerPaymentDTO, JsonNode rootNode, Integer response_code) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> responseMap = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        try {
            String errorMessage = null;
            if (rootNode != null) {
//                response_code = response.getStatusLine().getStatusCode();
                dataMap.put("customerUserName", customerPaymentDTO.getCustomerUserName());
                dataMap.put("customerUUID", customerPaymentDTO.getCustomerUUID());
                dataMap.put("orderId", customerPaymentDTO.getOrderId());
                dataMap.put("merchantName", customerPaymentDTO.getMerchantName());
//                Handle Multiple response codes
                if (response_code != 202) {
                    errorMessage = rootNode.path("RespDescription").asText();
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Initiating OnePay Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                }
                if (response_code >= 400) {
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Initiating OnePay Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    // Map the response to a HashMap
                    responseMap.put("code", response_code);
                    responseMap.put("data", dataMap);
                    responseMap.put("message", "Request To Pay Execute Successfully For OnePay");
                    genericDataDTO.setData(responseMap);
                    genericDataDTO.setResponseCode(HttpStatus.SC_OK);
                    genericDataDTO.setResponseMessage("success");
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Payment Initiatied Successfully" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("Failed to initiate OnePay payment " + e.getMessage());
        }
        return genericDataDTO;
    }

}
