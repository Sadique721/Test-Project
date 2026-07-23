package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TransacteasePayDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TransactionStatusRequestDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.TransacteaseApiConstant;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.commonMethods.IntegrationGenericMethods;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TransacteasePaymentService {

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private IntegrationGenericMethods integrationGenericMethods;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String URI = "api/transaction/status";
    private static final String METHOD = "POST";
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Transactional
    public Object initiatePayment(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        CustomerPayment customerPayment = new CustomerPayment();
        try {
            Double walletAmount = 0.0;
            Double planPrice = 0.0;
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            TransacteasePayDTO transacteasePayDTO = new TransacteasePayDTO();
            walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(), request.getHeader("Authorization"));
            planPrice = Double.valueOf(customerPaymentDTO.getAmount());
            if (customerPaymentDTO.getPlanId() != null) {
                log.info("fetching wallet ammount from revenue by customerId : {},", customerPaymentDTO.getCustomerId());
                log.info("fetching plan price from CMS by planId : {},", customerPaymentDTO.getPlanId());
                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), request.getHeader("Authorization"));
            }
            customerPaymentDTO.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.TRANSACTEASE);
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            if (!customerPaymentDTO.getAmount().contains(".")) {
                customerPaymentDTO.setAmount(customerPaymentDTO.getAmount() + ".00");
            }
            transacteasePayDTO = fetchGatewayParameters(customerPaymentDTO.getMerchantName(), customerPaymentDTO.getMvnoId());
            log.info("gateway parameter fetched successfully");
            customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            customerPaymentDTO.setOrderId(customerPayment.getOrderId().toString());
            Object object = generatePaymentRequest(customerPaymentDTO, transacteasePayDTO, customerPayment);
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void validateRequestForInitiatePayment(CustomerPaymentDTO paymentDTO) {
        if (Objects.nonNull(paymentDTO)) {
            if (paymentDTO.getCustomerId() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "customer id cannot be null", null);
            }
            if (paymentDTO.getMvnoId() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Mvno id cannot be null", null);
            }
            if (paymentDTO.getMobileNumber() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Mobile Number cannot be null", null);
            }
            if (paymentDTO.getAmount() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Amount cannot be null", null);
            }
            if (paymentDTO.getEmail() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Email cannot be null", null);
            }
            if (paymentDTO.getBillAddressLine1() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address Line1 cannot be null", null);
            }
            if (paymentDTO.getBillAddressLine2() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address Line2 cannot be null", null);
            }
            if (paymentDTO.getBillToAddressCity() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address City cannot be null", null);
            }
            if (paymentDTO.getBillToAddressState() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address State cannot be null", null);
            }
            if (paymentDTO.getBillToAddressZip() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address Zipcode cannot be null", null);
            }
//            if (paymentDTO.getBillToAddressCountry() == null) {
//                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Bill Address Country cannot be null", null);
//            }
        }
    }

    public TransacteasePayDTO fetchGatewayParameters(String paymentGatewayName, Integer mvnoId) {
        try {
            HashMap<String, String> params = paymentConfigService.getPaymentGatewayParameter(paymentGatewayName, mvnoId);
            String accessKey = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_ACCESS_KEY);
            String secretKey = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_SECRET_KEY);
            String allowedPaymentMethods = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_ALLOWED_PAYMENT_METHOD);
            String transacteaseChannel = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_CHANNEL);
            String merchantId = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_MERCHANT_ID);
//            String clientCredentials = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_CLIENT_CREDENTIALS);
            String requestUrl = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_REQUEST_URL);
            String expireTimeInSeconds = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_EXPIRED_TIME_IN_SECONDS);
            String callbackUrl = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_CALLBACK_URL) + URLConstants.Transactease.CALLBACK;
            String insId = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_INS_ID);
            String scheduleTime = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_SCHEDULE_TIME);
            String redirectTimeInSeconds = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.REDIRECT_TIME_IN_SECONDS);
            String redirectUrl = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_REDIRECT_URL);
            String currency = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_CURRENCY);
            String clientSecret = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_CLIENTSECRET);
            String country = params.get(PaymentGatewayConfigurationConstant.TRANSACTEASEPAY.TRANSACTEASE_COUNTRY);
            return new TransacteasePayDTO(accessKey, allowedPaymentMethods, secretKey, transacteaseChannel,
                    expireTimeInSeconds, insId, merchantId, requestUrl, redirectTimeInSeconds, callbackUrl, redirectUrl, scheduleTime, currency, clientSecret, country);
        } catch (Exception e) {
            log.error("error while fetching gateway parameters; Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Object generatePaymentRequest(CustomerPaymentDTO customerPaymentDTO, TransacteasePayDTO transacteasePayDTO, CustomerPayment customerPayment) {
        Map<String, String> data = new LinkedHashMap<>();
        StringBuilder htmlForm = new StringBuilder();
        try {
            data.put("MerchantUserID", transacteasePayDTO.getMerchantId());
            data.put("AccessKey", transacteasePayDTO.getAccessKey());
            data.put("Channel", transacteasePayDTO.getChannel());
            data.put("RequestID", customerPaymentDTO.getOrderId());
            data.put("PaymentMethod", transacteasePayDTO.getAllowedPaymentMethods());
            data.put("Amount", formatAmount(customerPaymentDTO.getAmount()));
            data.put("Currency", transacteasePayDTO.getCurrency());
            data.put("InvoiceNo", integrationGenericMethods.getLatestId().toString());
            data.put("BillToAddressLine1", customerPaymentDTO.getBillAddressLine1() != null ? trimStringTo50Chars(customerPaymentDTO.getBillAddressLine1()) : "");
            data.put("BillToAddressLine2", customerPaymentDTO.getBillAddressLine2() != null ? trimStringTo50Chars(customerPaymentDTO.getBillAddressLine2()) : "");
            data.put("BillToAddressCity", customerPaymentDTO.getBillToAddressCity() != null ? customerPaymentDTO.getBillToAddressCity() : "");
            data.put("BillToAddressPostalCode", URLConstants.Transactease.POSTAL_CODE);
            data.put("BillToAddressState", customerPaymentDTO.getBillToAddressState() != null ? customerPaymentDTO.getBillToAddressState() : "");
            data.put("BillToAddressCountry", transacteasePayDTO.getCountry() != null ? transacteasePayDTO.getCountry() : "");
            data.put("BillToForename", customerPaymentDTO.getCustomerUserName().replaceAll("[^a-zA-Z0-9]", ""));
            data.put("BillToSurname", customerPaymentDTO.getCustomerUserName().replaceAll("[^a-zA-Z0-9]", ""));
            data.put("BillToPhone", customerPaymentDTO.getMobileNumber());
            data.put("BillToEmail", customerPaymentDTO.getEmail());
            data.put("ExpiredInSeconds", transacteasePayDTO.getExpiredTimeInSeconds() != null ? transacteasePayDTO.getExpiredTimeInSeconds() : "300");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String timestamp = now.format(formatter);
            data.put("SignedDateTime", timestamp);
            String signedFields = "MerchantUserID,AccessKey,Channel,RequestID,PaymentMethod," +
                    "Amount,Currency,InvoiceNo,BillToAddressLine1,BillToAddressLine2," +
                    "BillToAddressCity,BillToAddressPostalCode,BillToAddressState," +
                    "BillToAddressCountry,BillToForename,BillToSurname,BillToPhone," +
                    "BillToEmail,ExpiredInSeconds,SignedDateTime";
            data.put("SignedFields", signedFields);
            String signature = generateSignatureForHostPayment(data, METHOD, "Payments/Request", timestamp, data.get("RequestID").toString(), transacteasePayDTO.getSecretKey());
            data.put("Signature", signature);

            htmlForm.append("<!DOCTYPE html>\n");
            htmlForm.append("<html>\n");
            htmlForm.append("<head>\n");
            htmlForm.append("  <title>Redirecting to Payment Gateway</title>\n");
            htmlForm.append("</head>\n");
            htmlForm.append("<body>\n");
            htmlForm.append("  <form id=\"paymentForm\" method=\"POST\" action=\"").append(transacteasePayDTO.getRequestUrl()).append("/Payments/Request").append("\">\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                htmlForm.append("    <input type=\"hidden\" name=\"").append(entry.getKey())
                        .append("\" value=\"").append(entry.getValue()).append("\">\n");
            }
            htmlForm.append("  </form>\n");
            htmlForm.append("  <script>document.getElementById('paymentForm').submit();</script>\n");
            htmlForm.append("</body>\n");
            htmlForm.append("</html>");
            System.out.println("printing html form \n\n");
            System.out.println(htmlForm.toString());
            scheduleForFetchingPaymentStatus(customerPaymentDTO, transacteasePayDTO, data.get("RequestID"), customerPayment);
        } catch (Exception e) {
            log.error("::::::Error while generating Payment Request for Transactease Payment:::::" + e.getMessage());
            e.printStackTrace();
        }
        return htmlForm.toString();
    }


    public void scheduleForFetchingPaymentStatus(CustomerPaymentDTO customerPaymentDTO, TransacteasePayDTO
            transacteasePayDTO, String requestId, CustomerPayment customerPayment) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call orderStatus API
            try {
                callLoginApi(customerPaymentDTO, transacteasePayDTO, requestId, customerPayment);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, Long.valueOf(transacteasePayDTO.getScheduleTime()), TimeUnit.MINUTES);
    }

    public void callLoginApi(CustomerPaymentDTO customerPaymentDTO, TransacteasePayDTO transacteasePayDTO, String requestId, CustomerPayment customerPayment) throws
            Exception {
        LinkedHashMap<String, Object> loginPayload = generateLoginPayload(transacteasePayDTO);
        String jsonLoginPayload = objectMapper.writeValueAsString(loginPayload);
        HashMap<String, Object> loginResponse = loginToTransactease(jsonLoginPayload, transacteasePayDTO, customerPaymentDTO);
        System.out.println("login response: " + loginResponse.toString());
        Map<String, Object> msgData = (Map<String, Object>) loginResponse.get("MsgData");
        String accessToken = msgData.get("AccessToken").toString();
        try {
            sendStatusRequestToTransactease(accessToken, customerPaymentDTO, transacteasePayDTO, customerPayment);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error while updating uabx Transactease Payment Status");
        }
    }

    private void sendStatusRequestToTransactease(String accessToken, CustomerPaymentDTO customerPaymentDTO, TransacteasePayDTO payDTO, CustomerPayment customerPayment) throws Exception {
        String trnxStatus = null;
        CustomerPayment byOrderId = customerPaymentRepository.findByOrderId(Long.parseLong(customerPaymentDTO.getOrderId()));
        if(byOrderId != null) {
            if(byOrderId.getStatus().equalsIgnoreCase("Successful") || byOrderId.getStatus().equalsIgnoreCase("SUCCESS")){
                trnxStatus = byOrderId.getStatus();
            }
        }
        int serialNumber = (int) (Math.random() * 900000) + 100000;
        String timestamp = LocalDateTime.now().format(formatter);
        String msgId = "M" + payDTO.getInsId().toString() + timestamp + serialNumber;

        TransactionStatusRequestDTO.MsgInfo msgInfo = new
                TransactionStatusRequestDTO.MsgInfo(URLConstants.Transactease.VERSION_NO,
                msgId, timestamp, URLConstants.Transactease.GET_TRANSACTION_STATUS,
                payDTO.getInsId());

        TransactionStatusRequestDTO.MsgData msgData =
                new TransactionStatusRequestDTO.MsgData(customerPaymentDTO.getOrderId(), payDTO.getMerchantId());

        TransactionStatusRequestDTO statusRequestDTO = new TransactionStatusRequestDTO(msgInfo, msgData);
        String payload = objectMapper.writeValueAsString(statusRequestDTO);
        String isoTimestamp = getIsoTimestamp();
        String rawString = generateRawStringForStatusSign(isoTimestamp, msgInfo, payload);
        String signature = generateHmacSHA256Base64(rawString, payDTO.getSecretKey());
        HttpPost post = new HttpPost(payDTO.getRequestUrl() + URLConstants.Transactease.CHECK_STATUS);
        StringEntity entity = new StringEntity(payload);
        post.setEntity(entity);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.addHeader("Authorization", "Bearer " + accessToken);
        post.addHeader("X-Auth-AccessKey", payDTO.getAccessKey());
        post.addHeader("X-Auth-Timestamp", isoTimestamp);
        post.addHeader("X-Auth-Nonce", msgInfo.getMsgID());
        post.addHeader("X-Auth-Signature", signature);

        LocalDateTime requestInitiationTime = LocalDateTime.now();
        Long responseTime = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            CloseableHttpResponse response = client.execute(post);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity httpEntity = response.getEntity();
            String responseText = EntityUtils.toString(httpEntity, "UTF-8");
            System.out.println("Response: " + responseText);
            JsonNode rootNode = objectMapper.readTree(responseText);
            if (rootNode.has("error")) {
                String error = rootNode.path("error").asText();
                String message = rootNode.path("message").asText();
                apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.CHECK_STATUS, null, response, post, null, responseTime, error, requestInitiationTime, responseText,customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Signature Verification Failed From Uabx Side: " + message, null);
            }
            String responseCode = rootNode.path("MsgResponse").path("ResponseCode").asText();
            String gatewayStatus = rootNode.path("MsgResponse").path("ResponseMsg").asText();

            if (responseCode.equals(TransacteaseApiConstant.STATUS_CODE.SUCCESS)) {
                apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.CHECK_STATUS, null, response, post, null, responseTime, null, requestInitiationTime, responseText, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                JsonNode responseData = rootNode.path("MsgData");
                String pgTransactionId = responseData.path("TransactionReferenceNumber").asText();
                String paymentStatus = responseData.path("Status").asText();
                if (paymentStatus.equalsIgnoreCase("EXPIRED")) {
                    gatewayStatus = paymentStatus;
                    paymentStatus = "Failed";
                    customerPayment.setFailureDescription("the transaction was not completed by user");
                } else if (paymentStatus.equalsIgnoreCase("CANCELED")) {
                    customerPayment.setFailureDescription("the transaction was cancelled");
                } else if (paymentStatus.equalsIgnoreCase("DECLINED")) {
                    customerPayment.setFailureDescription("the transaction was declined");
                }else if(paymentStatus.equalsIgnoreCase("SUCCESS")){
                    paymentStatus = "SUCCESSFUL";
                }
                if(trnxStatus == null){
                    trnxStatus = paymentStatus;
                }
                customerPayment.setStatus(trnxStatus);
                customerPayment.setPgTransactionId(pgTransactionId);
                customerPayment.setGatewayStatus(paymentStatus);
                customerPayment.setIsScheduled(true);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
            } else {
                apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.CHECK_STATUS, null, response, post, null, responseTime, gatewayStatus, requestInitiationTime, responseText, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                customerPayment.setIsScheduled(true);
                getStatusByResponseCodeAndDoOperationIt(responseCode, customerPayment, gatewayStatus, gatewayStatus);
            }
        }
    }

    private static String generateRawStringForStatusSign(String isoTimestamp, TransactionStatusRequestDTO.MsgInfo msgInfo, String payload) {
        String rawString = METHOD + "|" + URI + "|" + isoTimestamp + "|" + msgInfo.getMsgID() + "|" + payload;
        return rawString;
    }

    private static String getIsoTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.ofHoursMinutes(6, 30));
        String isoTimestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        return isoTimestamp;
    }

    private static String generateHmacSHA256Base64(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance(SignatureAlgorithm.HS256.getJcaName());
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        sha256Hmac.init(secretKey);
        byte[] hashBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private LinkedHashMap<String, Object> generateLoginPayload(TransacteasePayDTO transacteasePayDTO) {
        LinkedHashMap<String, Object> finalMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> MsgInfo = new LinkedHashMap<>();
        String timestamp = LocalDateTime.now().format(formatter);
        int serialNumber = (int) (Math.random() * 900000) + 100000;
        String msgId = "M" + transacteasePayDTO.getInsId().toString() + timestamp + serialNumber;
        MsgInfo.put("VersionNo", URLConstants.Transactease.VERSION_NO);
        MsgInfo.put("MsgID", msgId);
        MsgInfo.put("TimeStamp", timestamp);
        MsgInfo.put("MsgType", URLConstants.Transactease.LOGIN);
        MsgInfo.put("InsID", transacteasePayDTO.getInsId().toString());
        LinkedHashMap<String, Object> MsgData = new LinkedHashMap<>();
        MsgData.put("ClientID", transacteasePayDTO.getMerchantId());
        MsgData.put("ClientSecret", transacteasePayDTO.getClientSecret());
        MsgData.put("GrantType", URLConstants.Transactease.CLIENT_CREDENTIALS);
        finalMap.put("MsgInfo", MsgInfo);
        finalMap.put("MsgData", MsgData);
        return finalMap;
    }

    public HashMap<String, Object> loginToTransactease(String payload, TransacteasePayDTO payDTO, CustomerPaymentDTO customerPaymentDTO) {
        try {
            String errorMessage = null;
            Long responseTime = null;
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = new HttpPost(payDTO.getRequestUrl() + URLConstants.Transactease.LOGIN_API);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            StringEntity stringEntity = new StringEntity(payload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity responseEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            if (!responseString.isEmpty()) {
                HashMap<String, Object> objectHashMap = objectMapper.readValue(responseString, new TypeReference<HashMap<String, Object>>() {
                });
                LinkedHashMap<String, Object> msgResponse = (LinkedHashMap<String, Object>) objectHashMap.get("MsgResponse");
                if (msgResponse != null && objectHashMap != null) {
                    Integer response_code = Integer.parseInt((String) msgResponse.get("ResponseCode"));
                    if (response_code != 000) {
                        errorMessage = String.valueOf(msgResponse.get("ResponseMsg"));
                        System.out.println("Transection Error Message: " + errorMessage);
                        apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.LOGIN_API, null, httpResponse, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                    }
                    if (response_code >= 001) {
                        errorMessage = String.valueOf(msgResponse.get("ResponseMsg"));
                        System.out.println("Transection Error Message: " + errorMessage);
                        apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.LOGIN_API, null, httpResponse, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                    } else {
                        errorMessage = String.valueOf(msgResponse.get("ResponseMsg"));
                        apiAuditsService.extractDataAndSavePostApiAudits(payDTO.getRequestUrl() + URLConstants.Transactease.LOGIN_API, null, httpResponse, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName(): null, customerPaymentDTO.getMvnoId(), URLConstants.Transactease.TRANSACTEASE, customerPaymentDTO.getOrderId().toString());
                        return objectHashMap;
                    }
                }
            } else throw new RuntimeException("login response String is empty");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /* this signature is madte by the data fields provided */
    public static String generateSignatureForHostPayment(Map<String, String> formData, String method, String uri,
                                                         String signedDateTime, String requestId, String secretKey) {
        try {
            String[] signedFieldsArray = formData.get("SignedFields").toString().split(",");
            StringBuilder plainText = new StringBuilder();
            for (int i = 0; i < signedFieldsArray.length; i++) {
                String fieldName = signedFieldsArray[i];
                String fieldValue = (String) formData.getOrDefault(fieldName, "");

                plainText.append(fieldName).append("=").append(fieldValue);

                if (i < signedFieldsArray.length - 1) {
                    plainText.append(",");
                }
            }
            System.out.println("form data modified string: " + plainText.toString());
            // Build Signing String
            String signingString = method + "|" + uri + "|" + signedDateTime + "|" + requestId + "|" + plainText;
            System.out.println("Signing String: " + signingString);

            // HMAC SHA256 with Base64 encoding
            Mac mac = Mac.getInstance(SignatureAlgorithm.HS256.getJcaName());
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName()
            );
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(signingString.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    public static String formatAmount(String rawAmount) {
        if (rawAmount == null || rawAmount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be null or empty");
        }

        // Step 1: Remove commas
        String cleanedAmount = rawAmount.replace(",", "");

        try {
            // Step 2: Convert to BigDecimal and set scale to 2
            BigDecimal amount = new BigDecimal(cleanedAmount).setScale(2, BigDecimal.ROUND_DOWN);

            // Step 3: Format with 2 decimal places
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(amount);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + rawAmount, e);
        }
    }


    public void getStatusByResponseCodeAndDoOperationIt(String responseCode, CustomerPayment customerPayment, String message, String transection_status) {
        String status = TransacteaseApiConstant.getStatusAbbreviation(responseCode);
        switch (responseCode) {
            case (TransacteaseApiConstant.STATUS_CODE.FAILED):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Transaction Failed.", null);
            case (TransacteaseApiConstant.STATUS_CODE.CANCELED):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Transaction was canceled.", null);
            case (TransacteaseApiConstant.STATUS_CODE.DATA_ALREADY_EXISTS):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "payment status already updated.", null);
            case (TransacteaseApiConstant.STATUS_CODE.SESSION_TIMEOUT):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "payment link is may be expired for transaction.", null);
            case (TransacteaseApiConstant.STATUS_CODE.INVALID_FIELD_INFO):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid payment details", null);
            case (TransacteaseApiConstant.STATUS_CODE.INVALID_FORMAT):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid data", null);
            case (TransacteaseApiConstant.STATUS_CODE.SYSTEM_ERROR):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Some error occurred.", null);
            case (TransacteaseApiConstant.STATUS_CODE.INVALID_HASH):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid payment details provided for payment", null);
            case (TransacteaseApiConstant.STATUS_CODE.DATA_NOT_FOUND):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Data not found.", null);
            case (TransacteaseApiConstant.STATUS_CODE.ENCRYPTION_ERROR):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Encryption/Decryption failed.", null);
            case (TransacteaseApiConstant.STATUS_CODE.INVALID_AUTHORIZATION):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid authorization.", null);
            case (TransacteaseApiConstant.STATUS_CODE.INVALID_AUTHENTICATION):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid authentication.", null);
            default:
                customerPayment.setStatus("Failed");
                customerPayment.setGatewayStatus("Failed");
                customerPayment.setFailureDescription("payment failed");
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "payment failed", null);
        }
    }

    public static String generateNonce() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Random random = new Random();
        String randomDigits = String.format("%04d", random.nextInt(10000));
        return "M" + timestamp + randomDigits;
    }
    private String trimStringTo50Chars(String value) {
        if (value == null) return null;
        if (value.length() > 50) {
            return value.substring(0, 50);
        }
        return value;    }

}
