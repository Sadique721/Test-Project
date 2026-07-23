package com.savbill.integrationsystem.waveMoney;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WaveMoneyService {
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

    private static final Logger logger = LoggerFactory.getLogger(WaveMoneyService.class);

    private ObjectMapper mapper = new ObjectMapper();


    public GenericDataDTO waveMoneyPaymentInitiateService(CustomerPaymentDTO customerPaymentDTO, String authToken) throws Exception {
        logger.info("::::::::::Inside waveMoneyPaymentInitiateService Method:::::::::::::::");
        GenericDataDTO dataDTO = new GenericDataDTO();
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        String planName = null;
        try {
            planPrice = Double.valueOf(customerPaymentDTO.getAmount());
            walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(), authToken);
            if (customerPaymentDTO.getPlanId() != null) {
                logger.info("::::::::::Calling Revenue client:::::::::::::::");
//                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), authToken);
                logger.info("::::::::::Calling CMS client:::::::::::::::");
                planName = cmsClient.getPlanNameByPlanId(customerPaymentDTO.getPlanId(), authToken);
            }
//            else if(customerPaymentDTO.getInvoiceId()!=null){
//                planPrice = revenueClient.getPlanPriceByDebitDocId(customerPaymentDTO.getInvoiceId(), authToken);
//                planName = revenueClient.getPlanNameByDebitDocId(customerPaymentDTO.getInvoiceId(), authToken);
//            }
            else {
                logger.info(":::::::::::::::::::: Setting as ADVANCE PAYMENT :::::::::::::::::::::::::");
                planName = "Advance";
            }
            Map<String, Object> responseMap = cmsClient.getAccountNoByCustId(customerPaymentDTO.getCustomerId(), authToken);
            if (responseMap != null && responseMap.containsKey("accountNumber")) {
                String accountNumber = responseMap.get("accountNumber").toString();
                customerPaymentDTO.setAccountNumber(accountNumber);
            }
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            customerPaymentDTO.setPayerMobileNumber(customerPaymentDTO.getMobileNumber());

            CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            logger.info("::::::::::::Save Data for Wave Money in Customer Payment and Send to CMS:::::::");
            if (customerPayment != null) {
                WaveMoneyRequest waveMoneyRequest = fetchGatewayParameters(customerPaymentDTO, planName);
                dataDTO = requestToPay(waveMoneyRequest, waveMoneyRequest.getWaveMoneyObject(), customerPayment.getMvnoid(),customerPaymentDTO);
            } else {
                dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                dataDTO.setResponseMessage("Error while get customer payment details.");
                ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error in Initiate Request for WaveMoney : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + "Customer payment is not intiated" + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage("Exception while performing payment for Wave Money.");
            logger.error(":::::::::::::::::::::::::::Error in Initiate Request for Wave Money Payment:::::::::::::::::{}", e, e.getMessage());
            e.printStackTrace();
        }
        return dataDTO;
    }


    public WaveMoneyRequest fetchGatewayParameters(CustomerPaymentDTO customerPaymentDTO, String planName) throws JsonProcessingException {
        try {

            logger.info(":::::::::Fetch GatewayParameters for Wave Money:::::::::");
            HashMap<String, Object> gatewayParametersMap = extractParamsAndPayload(customerPaymentDTO, planName);
            String secretKey = gatewayParametersMap.get("secretKey").toString();
            String gatewayUrl = gatewayParametersMap.get("gatewayUrl").toString();
            String payload = gatewayParametersMap.get("payload").toString();
            WaveMoneyPayload waveMoneyObject = (WaveMoneyPayload) gatewayParametersMap.get("waveMoneyObject");
            return new WaveMoneyRequest(secretKey, gatewayUrl, payload, waveMoneyObject);
        } catch (Exception e) {
            logger.error("::::::::::::::::::::::::Error while fetch gateway parameters for Wave Money:::::::::", e, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Object> extractParamsAndPayload(CustomerPaymentDTO customerPaymentDTO, String planName) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        try {
//            Fetch gateway parameters
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.WAVE_PAY, customerPaymentDTO.getMvnoId());
            String merchantId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_MERCHANTID);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_SECRET_KEY);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_GATEWAY_URL);
            String webHookUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_CALLBACK_URL) + URLConstants.WaveMoney.CALLBACK_URL_ENDPOINT;
            String currency = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.WAVEMONEY.WAVEMONEY_CURRENCY);
            /** Set payload for Wave Money */
            WaveMoneyPayload waveMoneyPayload = setPayload(customerPaymentDTO, planName);
            waveMoneyPayload.setMerchantId(merchantId);
            waveMoneyPayload.setCurrency(currency);
            waveMoneyPayload.setBackendResultUrl(webHookUrl);
            waveMoneyPayload.setFrontendResultUrl(webHookUrl);
            String jsonPayload = mapper.writeValueAsString(waveMoneyPayload);
            map.put("secretKey", secretKey);
            map.put("gatewayUrl", gatewayUrl);
            map.put("payload", jsonPayload);
            map.put("waveMoneyObject", waveMoneyPayload);
        } catch (Exception e) {
            logger.error("::::::::::::::::::::::Failed to Extract Params and Payload of Wave Money Payment for mvnoId::::::::::::::::::::::::" + customerPaymentDTO.getMvnoId());
            throw new RuntimeException(e);
        }
        return map;
    }


    public WaveMoneyPayload setPayload(CustomerPaymentDTO customerPaymentDTO, String planName) throws JsonProcessingException {
        try {
            WaveMoneyPayload obj = new WaveMoneyPayload();
            obj.setOrderId(integrationGenericMethods.getLatestId().toString());
            obj.setMerchantReferenceId(integrationGenericMethods.generateId(customerPaymentDTO.getCustomerId().longValue()));
            obj.setTimeToLiveInSeconds(URLConstants.WaveMoney.timeToLiveInSeconds);
            obj.setPaymentDescription(URLConstants.WaveMoney.REMARKS);
            String rawAmount = customerPaymentDTO.getAmount().replace(",", "");
            double amountAsDouble = Double.parseDouble(rawAmount);
            int formattedAmount = (int) amountAsDouble;
            obj.setAmount(formattedAmount);
            obj.setMerchantName(customerPaymentDTO.getMerchantName());
            String itemList = setItemList(customerPaymentDTO.getPlanPrice(), planName);
            obj.setItems(itemList);
            return obj;
        } catch (Exception e) {
            logger.error(":::::::::::::Exception while set payload for WaveMoney:::::::::::::::::", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String setItemList(Double planPrice, String planName) throws JsonProcessingException {
        try {
            List<WaveMoneyPayload.Item> itemList = new ArrayList<>();
            WaveMoneyPayload.Item item = new WaveMoneyPayload.Item();
            item.setName(planName);
            double amountAsDouble = planPrice;
            int formattedAmount = (int) amountAsDouble;
            item.setAmount(formattedAmount);
            itemList.add(item);
            String jsonString = mapper.writeValueAsString(itemList);
            return jsonString;
        } catch (Exception e) {
            logger.error("::::::::::::::Exception while set payload for Wave Money:::::::::::", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public GenericDataDTO requestToPay(WaveMoneyRequest waveMoneyRequest, WaveMoneyPayload waveMoneyPayload, Integer mvnoId,CustomerPaymentDTO customerPaymentDTO) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        ObjectMapper mapper = new ObjectMapper();
        Long responseTime = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost();
        String responseBody = null;
        String errorMessage = null;
        Integer response_code = 0;
        try {

            String dataToHash = String.valueOf(waveMoneyPayload.getTimeToLiveInSeconds())
                    + waveMoneyPayload.getMerchantId()
                    + waveMoneyPayload.getOrderId()
                    + waveMoneyPayload.getAmount()
                    + waveMoneyPayload.getBackendResultUrl()
                    + waveMoneyPayload.getMerchantReferenceId();
            String hash = hmacSha256(dataToHash, waveMoneyRequest.getSecretKey());
            waveMoneyPayload.setHash(hash);
            String jsonBody = mapper.writeValueAsString(waveMoneyPayload);
            post = new HttpPost(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.PAYMENT);
            post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-Type", "application/json");
            response = httpClient.execute(post);
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.info(":::::::::Receive Response from Wave Money: " + responseBody);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            JsonObject responsePayload = JsonParser.parseString(responseBody).getAsJsonObject();
            if (response != null) {
                response_code = response.getStatusLine().getStatusCode();
                if (response_code == 422) {
                    errorMessage = manageResponse(responsePayload, response_code);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                    logger.error("Error while Send Request for Wave Money Pay: ");
                    apiAuditsService.extractDataAndSavePostApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.PAYMENT, null, response, post, null, responseTime, errorMessage, requestInitiationTime, responseBody,  customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.WAVE_PAY, waveMoneyPayload.getMerchantReferenceId());
                } else if (response_code >= 400) {
                    errorMessage = responsePayload.get("message").getAsString();
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage(errorMessage);
                    logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Wave Money : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    // Map the response to a HashMap
                    logger.info("::::::::::::::Generated Response For Wave Money Fetched Successfully::::::::::{}", response);
                    String transactionId = responsePayload.get("transaction_id").getAsString();
                    String responseUrl = waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.AUTHENTICATE + transactionId;
                    genericDataDTO.setData(responseUrl);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage("Payment Response Generated Successfully for WaveMoney.");
                    logger.info("Payment request Executed Successfully of WaveMoney for user : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.PAYMENT, null, response, post, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.WAVE_PAY, waveMoneyPayload.getMerchantReferenceId());
//                    scheduleForFetchingPaymentStatus(twoCTwoPRequest, mvnoId, orderId);
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.PAYMENT, null, response, post, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, mvnoId, PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.WAVE_PAY, waveMoneyPayload.getMerchantReferenceId());
            ApplicationLogger.logger.error("Failed to Pass request for  WaveMoney." + e.getMessage());
            genericDataDTO.setResponseCode(response_code);
            genericDataDTO.setResponseMessage("Failed to perform payment for WaveMoney.");
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    public String manageResponse(JsonObject responsePayload, Integer responseCode) {
        JsonObject errorsObject = responsePayload.getAsJsonObject("errors");
        StringBuilder errorMessageBuilder = new StringBuilder();

        for (Map.Entry<String, JsonElement> entry : errorsObject.entrySet()) {
            String field = entry.getKey();
            JsonArray messages = entry.getValue().getAsJsonArray();
            for (JsonElement message : messages) {
                errorMessageBuilder
                        .append(field)
                        .append(": ")
                        .append(message.getAsString())
                        .append(" | ");
            }
        }

        String errorMessage = errorMessageBuilder.toString();
        if (errorMessage.endsWith(" | ")) {
            errorMessage = errorMessage.substring(0, errorMessage.length() - 3); // Remove last delimiter

        }
        return errorMessage;
    }

  /*  public String authenticateAPI(String transactionId, WaveMoneyRequest waveMoneyRequest,Integer mvnoId, String orderId) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        ObjectMapper mapper = new ObjectMapper();
        Long responseTime = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.AUTHENTICATE + transactionId);
        String responseBody = null;
        String errorMessage = null;
        String getResponse = null;
        try {
            response = httpClient.execute(httpGet);
            getResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            logger.info(":::::::::Receive Response from Wave Money: " + responseBody);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            JsonObject responsePayload = JsonParser.parseString(getResponse).getAsJsonObject();
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    errorMessage = responsePayload.get("message").getAsString();
                    logger.error("Error while Send Request for Wave Money Pay: ");
                    apiAuditsService.extractDataAndSaveGetApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.AUTHENTICATE, null, response, httpGet,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,orderId);
                    return errorMessage;
                }
                if (response_code >= 400) {
                    logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                } else {
                    // Map the response to a HashMap
                    logger.info("::::::::::::::Generated Response For Wave Fetched Successfully::::::::::{}", getResponse);
                    logger.info("Order Create Executed Successfully of Selcom Pay for user : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSaveGetApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.AUTHENTICATE, null, response, httpGet,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,orderId);
                    return getResponse;
//                    scheduleForFetchingPaymentStatus(twoCTwoPRequest, mvnoId, orderId);
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSaveGetApiAudits(waveMoneyRequest.getGatewayUrl() + URLConstants.WaveMoney.AUTHENTICATE, null, response, httpGet,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,orderId);
            throw new RuntimeException(e);
        }
        return getResponse;
    }*/


    private static String hmacSha256(String data, String key) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        mac.init(secretKeySpec);

        byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }


}
