package com.savbill.integrationsystem.kbzIntegration;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
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
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class KbzIntegrationService {
    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private CMSClient cmsClient;


    @Autowired
    private IntegrationGenericMethods integrationGenericMethods;


    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private PaymentConfigService paymentConfigService;

    private static final Logger logger = LoggerFactory.getLogger(KbzIntegrationService.class);

    private ObjectMapper mapper = new ObjectMapper();


    public GenericDataDTO kbzPaymentInitiateService(CustomerPaymentDTO customerPaymentDTO, String authToken) throws Exception {
        logger.info("::::::::::Inside kbzPaymentInitiateService Method:::::::::::::::");
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

            CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            logger.info("::::::::::::Save Data for KBZ Pay in Customer Payment and Send to CMS:::::::");
            if (customerPayment != null) {
                KbzPayRequest kbzPayRequest = fetchGatewayParameters(customerPaymentDTO);
                dataDTO = requestToPay(kbzPayRequest, customerPaymentDTO.getMvnoId(),customerPayment);
            } else {
                dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                dataDTO.setResponseMessage("Error while get customer payment details.");
                ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error in Initiate Request for KBZ Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + "Customer payment is not intiated" + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage("Exception while performing payment for KBZ Pay.");
            logger.error(":::::::::::::::::::::::::::Error in Initiate Request for KBZ Pay Payment:::::::::::::::::{}", e, e.getMessage());
            e.printStackTrace();
        }
        return dataDTO;
    }

    public GenericDataDTO requestToPay(KbzPayRequest kbzPayRequest, Integer mvnoId,CustomerPayment customerPayment) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        ObjectMapper mapper = new ObjectMapper();
        Long responseTime = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost();
        String responseBody = null;
        String errorMessage = null;
        Integer response_code = 0;
        try {
            KbzRequestWrapper kbzRequestWrapper = new KbzRequestWrapper(kbzPayRequest.getKbzPayPayload());
            String jsonPayload = mapper.writeValueAsString(kbzRequestWrapper);
            httpPost = new HttpPost(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.CREATE_ORDER);
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            response = httpClient.execute(httpPost);
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.info(":::::::::Receive Response from KBZ Pay: " + responseBody);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode responseNode = rootNode.get("Response");
            if (response != null) {
                response_code = response.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    errorMessage = responseNode.get("msg").asText();
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                    logger.error("Error while Send Request for KBZ Pay Pay: " + errorMessage);
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                } else if (response_code >= 400) {
                    errorMessage = responseNode.get("msg").asText();
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                    logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for KBZ Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                } else {
                    // Map the response to a HashMap
                    logger.info("::::::::::::::Generated Response For KBZ Pay Fetched Successfully::::::::::{}", response);
                    String sign = responseNode.get("sign").asText();
                    String prepay_id = responseNode.get("prepay_id").asText();
                    System.out.println("prepayId: " + prepay_id);
                    String nonceStr = responseNode.get("nonce_str").asText();
                    System.out.println("nonceStr: " + nonceStr);
                    sign = newHashGenerate(kbzPayRequest.getKbzPayPayload(), nonceStr, prepay_id, kbzPayRequest.getAppKey(), kbzPayRequest.getKbzPayPayload().getTimestamp());
                    System.out.println("New Sign: " + sign);
                    if(!responseNode.get("qrCode").asText().isEmpty())
                        {
                            String responseUrl = responseNode.get("qrCode").asText();
                            genericDataDTO = manageResponse(responseNode, responseUrl);
                        }else{String responseUrl = callPaymentApi(kbzPayRequest, prepay_id, nonceStr, sign);
                    genericDataDTO = manageResponse(responseNode, responseUrl);}
                    logger.info("Payment request Executed Successfully of KBZ Pay for user : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                    scheduleForFetchingPaymentStatus(kbzPayRequest, mvnoId,customerPayment);
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
            ApplicationLogger.logger.error("Failed to Pass request for  KBZ Pay." + e.getMessage());
            genericDataDTO.setResponseCode(response_code);
            genericDataDTO.setResponseMessage("Failed to perform payment for KBZ Pay.");
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    public GenericDataDTO manageResponse(JsonNode responseNode, String url) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        String result = responseNode.get("result").asText();
        String code = responseNode.get("code").asText();
        String message = responseNode.get("msg").asText();
        if ("SUCCESS".equalsIgnoreCase(result) && "0".equals(code)) {
            dataDTO.setData(url);
            dataDTO.setResponseCode(HttpStatus.OK.value());
            dataDTO.setResponseMessage("Payment request Executed Successfully of KBZ Pay: " + message);
        } else {
            switch (code) {
                case "AUTHENTICATION_FAIL":
                    dataDTO.setData(code);
                    dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    dataDTO.setResponseMessage(message);
                    return dataDTO;
                case "REQUEST_FAIL":
                    dataDTO.setData(code);
                    dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    dataDTO.setResponseMessage(message);
                    return dataDTO;
                case "PRECREATE_FAIL":
                    dataDTO.setData(code);
                    dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    dataDTO.setResponseMessage(message);
                    return dataDTO;
                default:
                    dataDTO.setData(code);
                    dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    dataDTO.setResponseMessage(message);
                    return dataDTO;
            }
        }
        return dataDTO;
    }


    public String callPaymentApi(KbzPayRequest kbzPayRequest, String prepayId, String nonceStr, String sign) {
        String url = null;
        try {
            String baseUrl = kbzPayRequest.getSavbillDomainUrl()+"?kbzurl="+kbzPayRequest.getPaymentUrl();
            url = baseUrl +
                    "?appid=" + kbzPayRequest.getKbzPayPayload().getBizContent().getAppId() +
                    "&merch_code=" + kbzPayRequest.getKbzPayPayload().getBizContent().getMerchantCode() +
                    "&nonce_str=" + nonceStr +
                    "&prepay_id=" + prepayId +
                    "&timestamp=" + kbzPayRequest.getKbzPayPayload().getTimestamp() +
                    "&sign=" + sign;
            System.out.println("Url: " + url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public KbzPayRequest fetchGatewayParameters(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        try {
            logger.info(":::::::::Fetch GatewayParameters for KbzPay:::::::::");
            HashMap<String, Object> gatewayParametersMap = extractParamsAndPayload(customerPaymentDTO);
            String secretKey = gatewayParametersMap.get("secretKey").toString();
            String gatewayUrl = gatewayParametersMap.get("gatewayUrl").toString();
            String appKey = gatewayParametersMap.get("appKey").toString();
//            String payload = gatewayParametersMap.get("payload").toString();
            String scheduleTime = gatewayParametersMap.get("scheduleTime").toString();
            String callBackUrl = gatewayParametersMap.get("webHookUrl").toString();
            String paymentUrl = gatewayParametersMap.get("paymentUrl").toString();
            String savbillDomainUrl = gatewayParametersMap.get("savbillDomainUrl").toString();
            KbzPayPayload kbzPayPayload = (KbzPayPayload) gatewayParametersMap.get("kbzPayObject");
            return new KbzPayRequest(kbzPayPayload, secretKey, gatewayUrl, callBackUrl, scheduleTime, appKey,paymentUrl,savbillDomainUrl);
        } catch (Exception e) {
            logger.error("::::::::::::::::::::::::Error while fetch gateway parameters for KbzPay:::::::::", e, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Object> extractParamsAndPayload(CustomerPaymentDTO customerPaymentDTO) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        try {
//            Fetch gateway parameters
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, customerPaymentDTO.getMvnoId());
            String merchantId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_MERCHANT_CODE);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_APP_ID);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_GATEWAY_URL);
            String webHookUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_CALLBACK_URL) + URLConstants.KbzPay.CALLBACK_URL_ENDPOINT;
            String currency = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_CURRENCY);
            String appKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_APP_KEY);
            String scheduleTime = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_SCHEDULE_TIME);
            String paymentUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.KBZPAY_PAYMENT_URL);
            String savbillDomainUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.KBZPAY.SAVBILL_DOMAIN_URL);
            /** Set payload for KBZ PAY  */
            KbzPayPayload kbzPayPayload = new KbzPayPayload();
            kbzPayPayload.setTimestamp(String.valueOf(Instant.now().getEpochSecond()));
            kbzPayPayload.setNotifyUrl(webHookUrl);
            kbzPayPayload.setMethod(URLConstants.KbzPay.CREATE_ORDER_METHOD);
            kbzPayPayload.setNonceStr(generateNonceStr());
            kbzPayPayload.setSignType(URLConstants.KbzPay.SHA_256);
            kbzPayPayload.setSign(URLConstants.KbzPay.WAIT_TO_GENERATE);
            kbzPayPayload.setVersion(URLConstants.KbzPay.CREATE_ORDER_VER);
            KbzPayPayload.BizContent bizContent = new KbzPayPayload.BizContent();
            bizContent.setMerchOrderId(customerPaymentDTO.getOrderId());
//            bizContent.setMerchOrderId(integrationGenericMethods.generateId(customerPaymentDTO.getCustomerId().longValue()));

            bizContent.setMerchantCode(merchantId);
            bizContent.setAppId(secretKey);
            if(customerPaymentDTO.getOrderType().equals(URLConstants.KbzPay.TRADE_TYPE_QR)){
                bizContent.setTradeType(URLConstants.KbzPay.TRADE_TYPE_QR);
            }else {
                bizContent.setTradeType(URLConstants.KbzPay.TRADE_TYPE);
            }
            bizContent.setTotalAmount(customerPaymentDTO.getAmount());
            bizContent.setTransCurrency(currency);
            kbzPayPayload.setBizContent(bizContent);
            String sign = generateSHA256(kbzPayPayload, appKey);
            kbzPayPayload.setSign(sign);
//            String jsonPayload = mapper.writeValueAsString(kbzPayPayload);
            map.put("secretKey", secretKey);
            map.put("gatewayUrl", gatewayUrl);
            map.put("webHookUrl", webHookUrl);
//            map.put("payload", jsonPayload);
            map.put("appKey", appKey);
            map.put("scheduleTime", scheduleTime);
            map.put("paymentUrl",paymentUrl);
            map.put("savbillDomainUrl",savbillDomainUrl);
            map.put("kbzPayObject", kbzPayPayload);
        } catch (Exception e) {
            logger.error("::::::::::::::::::::::Failed to Extract Params and Payload of KBZ Pay Payment for mvnoId::::::::::::::::::::::::" + customerPaymentDTO.getMvnoId());
            throw new RuntimeException(e);
        }
        return map;
    }

    private void scheduleForFetchingPaymentStatus(KbzPayRequest kbzPayRequest, Integer mvnoId,CustomerPayment customerPayment) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call queryOrder API
            try {
                queryOrder(kbzPayRequest, mvnoId,customerPayment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Long.valueOf(kbzPayRequest.getScheduleTime()), TimeUnit.MINUTES);
    }


    public void queryOrder(KbzPayRequest kbzPayRequest, Integer mvnoId,CustomerPayment customerPayment) throws Exception {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        ObjectMapper mapper = new ObjectMapper();
        Long responseTime = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost();
        String responseBody = null;
        String errorMessage = null;
        Integer response_code = 0;
        HashMap<String, Object> map = new HashMap<>();
        Map<String, Object> bizContent = new HashMap<>();
        try {
            String nonceStr = generateNonceStr();
            String timeStamp = String.valueOf(Instant.now().getEpochSecond());
//            Set Payload for Query Order.
            bizContent.put("appid", kbzPayRequest.getKbzPayPayload().getBizContent().getAppId());
            bizContent.put("merch_code", kbzPayRequest.getKbzPayPayload().getBizContent().getMerchantCode());
            bizContent.put("merch_order_id", kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
            map.put("timestamp", timeStamp);
            map.put("nonce_str", nonceStr);
            map.put("method", URLConstants.KbzPay.QUERY_ORDER_METHOD);
            map.put("sign_type", URLConstants.KbzPay.SHA_256);
            map.put("version", URLConstants.KbzPay.QUERY_ORDER_VER);
            map.put("biz_content", bizContent);
            String sign = generateSHA256ForQueryOrder(kbzPayRequest.getKbzPayPayload(), kbzPayRequest.getAppKey(), nonceStr, timeStamp);
            map.put("sign", sign);
            Map<String, Object> finalRequest = new HashMap<>();
            finalRequest.put("Request", map);
//            kbzPayRequest.getKbzPayPayload().setSign(sign);
            String jsonPayload = mapper.writeValueAsString(finalRequest);
            httpPost = new HttpPost(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.QUERY_ORDER);
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            response = httpClient.execute(httpPost);
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.info(":::::::::Receive Response from KBZ Pay: " + responseBody);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            JsonNode rootNode = mapper.readTree(responseBody);
            JsonNode responseNode = rootNode.get("Response");
//            JsonObject responsePayload = JsonParser.parseString(responseBody).getAsJsonObject();
            if (response != null) {
                response_code = response.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    logger.error("Error while Send Request for KBZ Pay Pay: ");
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.QUERY_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                } else if (response_code >= 400) {
                    errorMessage = responseNode.get("msg").asText();
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.QUERY_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                    logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for KBZ Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    // Map the response to a HashMap
                    logger.info("::::::::::::::Generated Response For KBZ Pay Fetched Successfully::::::::::{}", response);
                    System.out.println(response);
                    String status = responseNode.get("result").asText();
                    String orderId = responseNode.get("merch_order_id").asText();
                    String tradeStatus = responseNode.get("trade_status").asText();
                    String financialTransactionId = null;
                    if (tradeStatus.equalsIgnoreCase("PAY_SUCCESS")) {
                        status = "Successful";
                        financialTransactionId = responseNode.get("mm_order_id").asText();
                    } else {
                        status = tradeStatus;
                    }
                    logger.info("Call Update Status and Send to CMS during Scheduler Executed of KBZ Pay for orderId: " + orderId);
                    paymentIntegrationService.updateStatusAndSendToCMS(orderId, financialTransactionId, status, null);
                    logger.info("KBZ Pay Scheduler Status: " + tradeStatus + " for OrderId: " + orderId + " and Result: " + status);
                    apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.QUERY_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(kbzPayRequest.getGatewayUrl() + URLConstants.KbzPay.QUERY_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.KBZ_PAY, kbzPayRequest.getKbzPayPayload().getBizContent().getMerchOrderId());
            ApplicationLogger.logger.error("Failed to Pass request for  KBZ Pay." + e.getMessage());
            e.printStackTrace();
        }
    }

    public String generateSHA256(KbzPayPayload kbzPayPayload, String appKey) {
        try {
            String stringA = "appid=" + kbzPayPayload.getBizContent().getAppId() +
                    "&merch_code=" + kbzPayPayload.getBizContent().getMerchantCode() +
                    "&merch_order_id=" + kbzPayPayload.getBizContent().getMerchOrderId() +
                    "&method=" + kbzPayPayload.getMethod() +
                    "&nonce_str=" + kbzPayPayload.getNonceStr() +
                    "&notify_url=" + kbzPayPayload.getNotifyUrl() +
                    "&timestamp=" + kbzPayPayload.getTimestamp() +
                    "&total_amount=" + kbzPayPayload.getBizContent().getTotalAmount() +
                    "&trade_type=" + kbzPayPayload.getBizContent().getTradeType() +
                    "&trans_currency=" + kbzPayPayload.getBizContent().getTransCurrency() +
                    "&version=" + kbzPayPayload.getVersion();
            String data = stringA + "&key=" + appKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');  // pad with leading zero if needed
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();  // Return as uppercase string
        } catch (Exception e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    public String generateSHA256ForQueryOrder(KbzPayPayload kbzPayPayload, String appKey, String nonceStr, String timeStamp) {
        try {
            String stringA = "appid=" + kbzPayPayload.getBizContent().getAppId() +
                    "&merch_code=" + kbzPayPayload.getBizContent().getMerchantCode() +
                    "&merch_order_id=" + kbzPayPayload.getBizContent().getMerchOrderId() +
                    "&method=" + URLConstants.KbzPay.QUERY_ORDER_METHOD +
                    "&nonce_str=" + nonceStr +
                    "&timestamp=" + timeStamp +
                    "&version=" + URLConstants.KbzPay.QUERY_ORDER_VER;
            String data = stringA + "&key=" + appKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');  // pad with leading zero if needed
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();  // Return as uppercase string
        } catch (Exception e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    public String newHashGenerate(KbzPayPayload kbzPayPayload, String noncStr, String prepayId, String key, String timeStamp) throws NoSuchAlgorithmException {
        try {
            String stringA = "appid=" + kbzPayPayload.getBizContent().getAppId() +
                    "&merch_code=" + kbzPayPayload.getBizContent().getMerchantCode() +
                    "&nonce_str=" + noncStr + "&prepay_id=" + prepayId +
                    "&timestamp=" + timeStamp + "&key=" + key;
            System.out.println("ready to sign: " + stringA);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(stringA.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');  // pad with leading zero if needed
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();  // Return as uppercase string
        } catch (Exception e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }


    public static String generateNonceStr() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int NONCE_LENGTH = 32;
        SecureRandom random = new SecureRandom();
        StringBuilder nonce = new StringBuilder(NONCE_LENGTH);
        for (int i = 0; i < NONCE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            nonce.append(CHARACTERS.charAt(index));
        }
        return nonce.toString().toUpperCase();

    }

}
