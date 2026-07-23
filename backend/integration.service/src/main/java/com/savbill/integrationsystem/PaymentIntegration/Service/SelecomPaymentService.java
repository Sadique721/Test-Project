package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.SelcomPayDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.QCustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.SelcomGeneralDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.SelcomPayPayment;
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
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SelecomPaymentService {

    @Autowired
    PaymentConfigService paymentConfigService;

    @Autowired
    CustomerPaymentService customerPaymentService;

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private CMSClient cmsClient;


    private final ObjectMapper objectMapper = new ObjectMapper();


    public GenericDataDTO selecomPaymentInitiateService(SelcomGeneralDTO selcomGeneralDTO, String authToken) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            Double walletAmount = 0.0;
            Double planPrice = 0.0;
            if(selcomGeneralDTO.getCustomerPaymentDTO().getPlanId() != null) {
                walletAmount = revenueClient.getWalletBalanceByCustId(selcomGeneralDTO.getCustomerPaymentDTO().getCustomerId(),authToken);
                planPrice =  cmsClient.getplanPriceByPlanId(selcomGeneralDTO.getCustomerPaymentDTO().getPlanId(),authToken);
            }
            Map<String, Object> responseMap = cmsClient.getAccountNoByCustId(selcomGeneralDTO.getCustomerPaymentDTO().getCustomerId(), authToken);
            if (responseMap != null && responseMap.containsKey("accountNumber")) {
                String accountNumber = responseMap.get("accountNumber").toString();
                selcomGeneralDTO.getCustomerPaymentDTO().setAccountNumber(accountNumber);
            }
            selcomGeneralDTO.getCustomerPaymentDTO().setWalletAmount(walletAmount);
            selcomGeneralDTO.getCustomerPaymentDTO().setPlanPrice(planPrice);
            selcomGeneralDTO.getCustomerPaymentDTO().setPayerMobileNumber(selcomGeneralDTO.getCustomerPaymentDTO().getMobileNumber());

            CustomerPayment customerPayment = sendAndSaveSelcomPayDataForPayment(selcomGeneralDTO.getCustomerPaymentDTO(), "Initiate");
            if(customerPayment != null){
                SelcomPayDTO selcomPayDTO = fetchGatewayParameters(selcomGeneralDTO);
                dataDTO = createOrder(selcomPayDTO,selcomGeneralDTO.getCustomerPaymentDTO().getMvnoId(),customerPayment);
            }
            else{
                ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error in Initiate Request for Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + "Customer payment is not intiated" + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error in Initiate Request for Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.printStackTrace();
        }
        return dataDTO;
    }

    public GenericDataDTO createOrder(SelcomPayDTO selcomPayDTO, Integer mvnoId,CustomerPayment customerPayment) throws Exception {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CloseableHttpResponse response = null;
        JsonObject jsonObject = new JsonObject();
        String responseBody = null;
        Long responseTime = null;
        HttpPost httpPost = new HttpPost();
        HashMap<String, Object> responseMap = new HashMap<>();
        try {
            /** We convert our jsonPayload into Map so further it will add in JsoObject */
            Map<String, Object> dataMap = objectMapper.readValue(selcomPayDTO.getJsonPayload(), Map.class);
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                jsonObject.addProperty(entry.getKey(), entry.getValue().toString());
            }
            /** Set Headers required for Create Order */
            Map<String, Object> headers = computeHeaders(jsonObject, selcomPayDTO.getApiKey(), selcomPayDTO.getSecretKey());
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            httpPost = new HttpPost(selcomPayDTO.getGatewayUrl() + URLConstants.SelcomPay.CREATE_ORDER);
            /** Add headers that calculate in computeHeaders method */
            for (Object key : headers.keySet()) {
                httpPost.addHeader(key.toString(), headers.get(key).toString());
            }
            /** set json payload required for request*/
            StringEntity entity = new StringEntity(selcomPayDTO.getJsonPayload());
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Request Executed for Order Create of Selcom Pay  : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String errorMessage = null;
            if (response != null) {
                Integer response_code = response.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    errorMessage = rootNode.path("message").asText();
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Order Create of Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(selcomPayDTO.getGatewayUrl() + URLConstants.SelcomPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM,customerPayment.getOrderId().toString());
                }
                if (response_code >= 400) {
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(selcomPayDTO.getGatewayUrl() + URLConstants.SelcomPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM,customerPayment.getOrderId().toString());
                } else {
                    // Map the response to a HashMap
                    responseMap.put("code", response_code);
                    responseMap.put("message", "Order Create Executed Successfully of Selcom Pay.");
                    JsonNode dataArray = rootNode.get("data");
                    if (dataArray.isArray() && dataArray.size() > 0) {
                        JsonNode firstDataItem = dataArray.get(0);
                        String paymentGatewayUrl = firstDataItem.get("payment_gateway_url").asText();
                        /** decode payment url using Base64 decoder generated in response  */
                        byte[] decodedBytes = Base64.getDecoder().decode(paymentGatewayUrl);
                        String decodedUrl = new String(decodedBytes);
                        responseMap.put("data", decodedUrl);
                    }
                    genericDataDTO.setData(responseMap);
                    genericDataDTO.setResponseCode(HttpStatus.SC_OK);
                    genericDataDTO.setResponseMessage("success");
                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Order Create Executed Successfully of Selcom Pay for user : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(selcomPayDTO.getGatewayUrl() + URLConstants.SelcomPay.CREATE_ORDER, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM,customerPayment.getOrderId().toString());
                    scheduleForFetchingPaymentStatus(selcomPayDTO);
                }
            }
        } catch (Exception e) {
            apiAuditsService.extractDataAndSavePostApiAudits(selcomPayDTO.getGatewayUrl() + URLConstants.SelcomPay.CREATE_ORDER, null, response, httpPost, null, responseTime, e.getMessage(), requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId , PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM,customerPayment.getOrderId().toString());
            ApplicationLogger.logger.error("Failed to Create Order of Selcom Pay." + e.getMessage());
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    /**
     * This Method Basically Set Headers required for Selcom Pay used in every api-request.
     */
    private static Map<String, Object> computeHeaders(JsonObject data, String apiKey, String apiSecret) throws Exception {
        Map<String, Object> header = new HashMap<>();
        List<String> keys = new ArrayList<String>();
        List<String> serializedJson = new ArrayList<>();
        String message = "", signed_fields = "";
        Date now = new Date();
        try {
            String encodekey = Base64.getEncoder().encodeToString((apiKey).getBytes());
            String authToken = URLConstants.SelcomPay.SELCOM + encodekey;
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-M-d'T'H:m:sXXX");

            String timestamp = sdfDate.format(now);
            serializedJson.add("timestamp=" + timestamp);
            for (Object key : data.keySet()) {
                String keyStr = (String) key;
                String keyvalue = data.get(keyStr).getAsString();
                serializedJson.add(keyStr + "=" + keyvalue);
                keys.add(keyStr);
            }

            message = String.join("&", serializedJson);
            signed_fields = String.join(",", keys);


            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String digest = new String(Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes())));
            /** Add required Headers For Request */
            header.put("Content-type", "application/json");
            header.put("Authorization", authToken);
            header.put("Digest-Method", "HS256");
            header.put("Digest", digest);
            header.put("Timestamp", timestamp);
            header.put("Signed-Fields", signed_fields);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Failed to Calculate headers required for Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);

        }
        return header;

    }

    private void scheduleForFetchingPaymentStatus(SelcomPayDTO dto) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call orderStatus API
            try {
                orderStatus(dto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, Long.valueOf(dto.getScheduleTime()), TimeUnit.MINUTES);
    }

    public void orderStatus(SelcomPayDTO dto) throws JsonProcessingException {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        CloseableHttpResponse response = null;
        HttpGet request = new HttpGet();
        Long responseTime = null;
        String responseBody = null;
        JsonObject jsonObject = new JsonObject();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(dto.getJsonPayload());
        String orderId = jsonNode.get("order_id").asText();
        try {
//            List<CustomerPayment> paymentList = customerPaymentRepository.findAllByStatusAndMechantNameAndIsScheduled("Successful","SELCOM",false);
//            if (!paymentList.isEmpty()) {
//                paymentList.sort(Comparator.comparing(CustomerPayment::getPaymentDate).reversed());
//                LocalDateTime mostRecentDate = paymentList.get(0).getPaymentDate();
//                long exclusionMarginMinutes = 5; // Exclude all within 5 minute
//                LocalDateTime thresholdDate = mostRecentDate.minusMinutes(exclusionMarginMinutes);
//                paymentList = paymentList.stream()
//                        .filter(payment -> payment.getPaymentDate().isBefore(thresholdDate))
//                        .collect(Collectors.toList());
//            }

//            String customerUUID = jsonNode.get("customer_uuid").asText();

                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                jsonObject.addProperty("order_id", orderId);
                // Prepare headers
                Map<String, Object> headers = computeHeaders(jsonObject, dto.getApiKey(), dto.getSecretKey());
                request = new HttpGet(dto.getGatewayUrl() + URLConstants.SelcomPay.ORDER_STATUS + orderId);
                // Add headers
                for (Object key : headers.keySet()) {
                    request.addHeader(key.toString(), headers.get(key).toString());
                }
                response = httpClient.execute(request);
                ApplicationLogger.logger.info("=======Response==="+response);
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                ApplicationLogger.logger.info("Request execute for fetching order status of Selcom for referenceId: " + orderId);
                HttpEntity responseEntity = response.getEntity();
                ApplicationLogger.logger.info("=======ResponseEntity==="+responseEntity);
                responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                ApplicationLogger.logger.info("=== SELCOM RAW RESPONSE START ===");
                ApplicationLogger.logger.info(responseBody);
                ApplicationLogger.logger.info("=== SELCOM RAW RESPONSE END ===");
                ApplicationLogger.logger.info("Response length: " + (responseBody != null ? responseBody.length() : 0));
                JsonNode rootNode = objectMapper.readTree(responseBody);
                // Navigate to the 'data' array
                JsonNode dataArray = rootNode.path("data");
                ApplicationLogger.logger.info("rootNode.has('data'): " + rootNode.has("data"));
                ApplicationLogger.logger.info("data node type: " + rootNode.path("data").getNodeType());
                ApplicationLogger.logger.info("data isArray: " + rootNode.path("data").isArray());
                ApplicationLogger.logger.info("data size: " + rootNode.path("data").size());
                ApplicationLogger.logger.info("resultcode: " + rootNode.path("resultcode").asText());

                ApplicationLogger.logger.info("About to check dataArray condition");
                if (dataArray.isArray() && dataArray.size() > 0) {
                    ApplicationLogger.logger.info("Entered dataArray block");
                    // Extract first object in the 'data' array
                    JsonNode firstDataNode = dataArray.get(0);
                    String errorMessage = null;
                    if (response != null) {
                        Integer response_code = response.getStatusLine().getStatusCode();
                        ApplicationLogger.logger.info("HTTP Response Code: " + response_code);
                        if (response_code != 200) {
                            errorMessage = rootNode.path("message").asText();
                            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Fetching Order status In Selcom Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                            apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.SelcomPay.ORDER_STATUS, null, response, request,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,orderId.toString());
                        }
                        if (response_code >= 400) {
                            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Fetching Order status of Selcom  : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        } else {
                            String financialTransactionId = firstDataNode.path("transid").asText();
                            String customerUuid = rootNode.path("reference").asText();
                            String status = firstDataNode.path("payment_status").asText();
                            if(status.equalsIgnoreCase("COMPLETED")){
                                status = "Successful";
                            }
                            ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Fetch Order Status Successfully of Selcom Pay for orderId: "+ orderId  + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                            ApplicationLogger.logger.info(">>> CALLING updateStatusAndSendToCMS <<<");
                            paymentIntegrationService.updateStatusAndSendToCMS(orderId, financialTransactionId, status,null);
                            apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.SelcomPay.ORDER_STATUS, null, response, request,  responseTime, errorMessage, requestInitiationTime, responseBody, null, null,orderId.toString());
                        }
                    }
                }


        } catch (Exception e) {
            apiAuditsService.extractDataAndSaveGetApiAudits(dto.getGatewayUrl() + URLConstants.SelcomPay.ORDER_STATUS, null, response, request,  responseTime, e.getMessage(), requestInitiationTime, responseBody, null, null,orderId.toString());
            ApplicationLogger.logger.error("Failed to fetch Selcom Pay Order status " + e.getMessage());
        }
    }

    public SelcomPayDTO fetchGatewayParameters(SelcomGeneralDTO selcomGeneralDTO) throws JsonProcessingException {
        HashMap<String, Object> gatewayParametersMap = extractParamsAndPayload(selcomGeneralDTO);
        String apiKey = gatewayParametersMap.get("apiKey").toString();
        String secretKey = gatewayParametersMap.get("secretKey").toString();
        String gatewayUrl = gatewayParametersMap.get("gatewayUrl").toString();
        String jsonPayload = gatewayParametersMap.get("jsonPayload").toString();
        String scheduleTime = gatewayParametersMap.get("scheduleTime").toString();
        ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Fetch Gateway Params and Payload of Selcom Pay : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        return new SelcomPayDTO(apiKey, secretKey, gatewayUrl, jsonPayload, scheduleTime);
    }

    public HashMap<String, Object> extractParamsAndPayload(SelcomGeneralDTO selcomGeneralDTO) throws JsonProcessingException {
        HashMap<String, Object> map = new HashMap<>();
        try {
//            Fetch gateway parameters
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.SELCOM, selcomGeneralDTO.getCustomerPaymentDTO().getMvnoId());
            String apiKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_API_KEY);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_SECRET_KEY);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_GATEWAY_URL);
            String webHookUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_WEBHOOK_URL)+URLConstants.SelcomPay.WEBHOOK_URL_ENDPOINT;
            String paymentMethods = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_PAYMENT_METHODS);
            String vendor = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_VENDOR);
            String currency = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_CURRENCY);
            String scheduleTime = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.SELCOMPAY.SELCOM_SCHEDULE_TIME);
            // Set Body or Json Payload required for create Order.
            SelcomPayPayment selcomPayPayment = setPayload(selcomGeneralDTO);
            selcomPayPayment.setVendor(vendor);
            selcomPayPayment.setCurrency(currency);
            selcomPayPayment.setPaymentMethods(paymentMethods);
            selcomPayPayment.setWebHook(Base64.getEncoder().encodeToString(webHookUrl.getBytes()));
            String jsonPayload = objectMapper.writeValueAsString(selcomPayPayment);
            map.put("jsonPayload", jsonPayload);
            map.put("scheduleTime", scheduleTime);
            map.put("gatewayUrl", gatewayUrl);
            map.put("apiKey", apiKey);
            map.put("secretKey", secretKey);
        } catch (Exception e) {
            ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Failed to Extract Params and Payload of Selcom Pay for referenceId : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        }
        return map;
    }

    public static String generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return String.valueOf(id);
    }

//    public Long getLatestId() {
//        Long latestId = 0L;
//        latestId = customerPaymentRepository.getLatestId();
//        if (Objects.isNull(latestId)) {
//            latestId = 1L;
//        } else {
//            latestId = latestId + 1L;
//        }
//        return latestId;
//    }

    public SelcomPayPayment setPayload(SelcomGeneralDTO selcomGeneralDTO) {
        SelcomPayPayment obj = new SelcomPayPayment();
        obj.setOrderId(selcomGeneralDTO.getCustomerPaymentDTO().getOrderId());
        obj.setBuyerEmail(selcomGeneralDTO.getSelcomPayPayment().getBuyerEmail());
        obj.setBuyerName(selcomGeneralDTO.getSelcomPayPayment().getBuyerName());
        obj.setBuyerPhone(selcomGeneralDTO.getSelcomPayPayment().getBuyerPhone());
        obj.setGatewayBuyerUuid(selcomGeneralDTO.getSelcomPayPayment().getGatewayBuyerUuid());
        double amountAsDouble = Double.parseDouble(selcomGeneralDTO.getSelcomPayPayment().getAmount().replace(",",""));
        String formattedAmount = String.format("%.2f", amountAsDouble);
        obj.setAmount(formattedAmount);
        obj.setBillingFirstName(selcomGeneralDTO.getSelcomPayPayment().getBillingFirstName());
        obj.setBillingLastName(selcomGeneralDTO.getSelcomPayPayment().getBillingLastName());
        obj.setBillingAddress1(selcomGeneralDTO.getSelcomPayPayment().getBillingAddress1());
        obj.setBillingCity(selcomGeneralDTO.getSelcomPayPayment().getBillingCity());
        obj.setBillingStateOrRegion(selcomGeneralDTO.getSelcomPayPayment().getBillingStateOrRegion());
        obj.setBillingCountry(selcomGeneralDTO.getSelcomPayPayment().getBillingCountry());
        obj.setBillingPhone(selcomGeneralDTO.getSelcomPayPayment().getBillingPhone());
        obj.setNoOfItems(selcomGeneralDTO.getSelcomPayPayment().getNoOfItems());
        return obj;
    }


    public CustomerPayment sendAndSaveSelcomPayDataForPayment(CustomerPaymentDTO customerPaymentDTO, String status) {
        try {
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            Long orderId = Long.valueOf(generateId(customerPaymentDTO.getCustomerId().longValue()));
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
            if (customerPaymentDTO.getCustServiceMappingId() != null) {
                custPayDTOMessage.setCustServiceMappingId(customerPaymentDTO.getCustServiceMappingId());
            }
            customerPaymentDTO.setOrderId(orderId.toString());
            if (customerPaymentDTO.getPartnerId() != null) {
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            }
            custPayDTOMessage.setCustomerUUID(customerPaymentDTO.getCustomerUUID());
            CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(getLatestId());
            if (customerPaymentDTO.getInvoiceId() != null) {
                customerPayment.setInvoiceId(customerPaymentDTO.getInvoiceId());
            }
            if(customerPaymentDTO.getIsAdvancePayment()!=null) {
                customerPayment.setIsAdvancePayment(customerPaymentDTO.getIsAdvancePayment());
            }
            if(customerPaymentDTO.getPayerMobileNumber() != null) {
                customerPayment.setPayerMobileNumber(customerPaymentDTO.getPayerMobileNumber());
            }
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
            ApplicationLogger.logger.info("Send Initiated Request of SELCOM Data to CMS for referenceId: " + customerPayment.getCustomerUUID());
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
           customerPayment =  customerPaymentRepository.save(customerPayment);
           return customerPayment;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Sending Data of SELCOM to CMS Through Kafka. ", e.getMessage());
            return null;
        }
    }

}