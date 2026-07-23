package com.savbill.integrationsystem.PaymentIntegration.Service;


import com.savbill.integrationsystem.CRDB.Constants.CRDBConstant;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.repository.PaymentConfigRepository;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.OnlineInvoicePaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.PhonePeBase64DTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.PhonePePayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.QCustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.TradelanceIntigration.TradelanceService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaConstant;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
//import com.savbill.integrationsystem.rabbitmq.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PaymentIntegrationService {


    @Autowired
    PaymentConfigRepository paymentConfigRepository;

    @Autowired
    PaymentConfigService paymentConfigService;

    @Autowired
    ApiAuditsService apiAuditsService;

    @Autowired
    TradelanceService tradelanceService;

    @Autowired
    CMSClient cmsClient;

    @Autowired
    private JwtUtil jwtUtil;

//    @Autowired
//    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CustomerPaymentService customerPaymentService;


    @Autowired
    CustomerPaymentRepository customerPaymentRepository;



    public GenericDataDTO phonePePaymentInitiateService(CustomerPaymentDTO customerPaymentDTO) {
        CloseableHttpResponse response = null;
        PhonePePayment phonePePayment = new PhonePePayment();
        String url = null;
        LocalDateTime requestInitiationTime = null;
        PhonePeBase64DTO phonePeBase64DTO = new PhonePeBase64DTO();
        HashMap<String, Object> responseMap = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            //Business Logic

            HashMap<String, Object> payloadMap = genrateAPIPayload(customerPaymentDTO);

            String jsonPayload = payloadMap.get("jsonPayload").toString();
            String saltIndex = payloadMap.get("saltIndex").toString();
            String saltKey = payloadMap.get("saltKey").toString();
            String gatewayUrl = payloadMap.get("gatewayUrl").toString();
            String profileName = PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PHONEPE;
            PhonePePayment phonePePaymentObj = (PhonePePayment) payloadMap.get("phonePePayment");
            String merchantId = payloadMap.get("merchantId").toString();

            // base64Payload  and checksum generation.
            String base64Payload = convertJsonToBase64(jsonPayload);
            System.out.println("base64Payload : \n\n" + base64Payload + "\n\n");
            String x_verifyChecksum = generateSignature(base64Payload, saltKey, saltIndex);
            System.out.println("x_verifyChecksum : \n\n" + x_verifyChecksum + "\n\n");


            CloseableHttpClient closeableHttpClient = createHttpClient();
            url = gatewayUrl;
            Integer response_code = HttpStatus.SC_EXPECTATION_FAILED;
            requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = createHttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("X-VERIFY", x_verifyChecksum);
            phonePeBase64DTO.setRequest(base64Payload);
            ObjectMapper objectMapper = new ObjectMapper();
            String phonepeBase64Payload = objectMapper.writeValueAsString(phonePeBase64DTO);
            StringEntity requestEntity = new StringEntity(phonepeBase64Payload);
            requestEntity.setContentType("application/json");
            httpPost.setEntity(requestEntity);


            try {
                CloseableHttpClient httpClients = HttpClients.createDefault();
                response = closeableHttpClient.execute(httpPost);
                System.out.println("PhonePe Payment Initiate Response : \n\n" + response + "\n\n");
                LocalDateTime requestCompletionTime = LocalDateTime.now();
                Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                String errorMessage = null;
                if (response != null) {
                    response_code = response.getStatusLine().getStatusCode();
                    if (response_code != 200) {
                        JSONObject responseObject = new JSONObject(responseBody.toString());
                        if (responseObject.has("errors")) {
                            JSONArray errorsArray = responseObject.getJSONArray("errors");
                            JSONObject errorObject = errorsArray.getJSONObject(0);
                            errorMessage = errorObject.getString("message");
                            genericDataDTO.setData(errorMessage);
                            genericDataDTO.setResponseCode(response_code);
                            genericDataDTO.setResponseMessage("error");
                        }
                    }
                }
                // Extract the message

                // Handle different response codes if needed
                if (response_code >= 400) {
                    genericDataDTO.setData(errorMessage);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("error");
                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Initiating Phonpe Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                    apiAuditsService.extractDataAndSavePostApiAudits(url, null, response, httpPost, null, responseTime, null, requestInitiationTime, responseBody, String.valueOf(customerPaymentDTO.getCustomerId()), customerPaymentDTO.getMvnoId(), profileName,null);
                } else {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject nestedDataObject = new JSONObject();
                    JSONObject instrumentResponseObject = new JSONObject();
                    JSONObject redirectInfoObject = new JSONObject();
                    // Map the response to a HashMap

                    responseMap.put("code", jsonObject.optString("code"));
                    responseMap.put("data", jsonObject.optJSONObject("data").toString());
                    responseMap.put("success", jsonObject.optBoolean("success"));
                    responseMap.put("message", jsonObject.optString("message"));

                    genericDataDTO.setData(responseMap);
                    genericDataDTO.setResponseCode(response_code);
                    genericDataDTO.setResponseMessage("success");

                    // Extract Payment Link URL
                    // Get the 'data' object
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    // Get the 'instrumentResponse' object
                    instrumentResponseObject = dataObject.getJSONObject("instrumentResponse");
                    // Get the 'redirectInfo' object
                    redirectInfoObject = instrumentResponseObject.getJSONObject("redirectInfo");

                    String paymentLink = redirectInfoObject.getString("url");

                    //save data into tbltpayment table and send it to the cms
                    sendAndSaveDataForPayment(customerPaymentDTO, phonePePaymentObj, "Initiate", merchantId, paymentLink, x_verifyChecksum);

                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Payment Initiatied Successfully" + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                    apiAuditsService.extractDataAndSavePostApiAudits(url, null, response, httpPost, null, responseTime, null, requestInitiationTime, responseBody, String.valueOf(customerPaymentDTO.getCustomerId()), customerPaymentDTO.getMvnoId(), profileName,null);
                }
            } catch (Exception e) {
                ApplicationLogger.logger.error("Failed to initiate phoenpe payment " + e.getMessage());
            }
            return genericDataDTO;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Failed to initiate phoenpe payment " + e.getMessage());
        }
        return genericDataDTO;
    }


    public static String generateSignature(String base64Payload, String saltKey, String saltIndex) throws NoSuchAlgorithmException {
        // Concatenate the base64 encoded payload, endpoint, and salt key
        String dataToHash = base64Payload + "/pg/v1/pay" + saltKey;

        // Compute SHA-256 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

        // Convert hash to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Append salt index with separator
        return hexString.toString() + "###" + saltIndex;
    }


    public static String convertJsonToBase64(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        String base64Encoded = null;
        try {
            objectMapper.writeValueAsString(jsonString);
            base64Encoded = Base64.getEncoder().encodeToString(jsonString.getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return base64Encoded;
    }

    public HashMap<String, Object> genrateAPIPayload(CustomerPaymentDTO customerPaymentDTO) {
        String payload = null;
        HashMap<String, Object> map = new HashMap<>();
        try {
            String callBackUrl = null;
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PHONEPE, customerPaymentDTO.getMvnoId());

            String merchantId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_MERCHANT_ID);
            String merchantUserId = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_MERCHANT_USERID);
            String saltIndex = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_SALT_INDEX);
            String saltKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_SALT_KEY);
            String redirectUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_CALLBACK_URL);
            if(customerPaymentDTO.getCustomerId()!=null) {
                 callBackUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONEPE_REDIRECT_URL);
            }
            else if(customerPaymentDTO.getPartnerId()!=null){
                 callBackUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PARTNER_REDIRECT_URL);
            }
            String redirectMode = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONPE_REDIRECT_MODE);
            String paymentInstrumentType = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONPE_PAYMENT_INSTRUMENT_TYPE);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PHONEPE.PHONPE_GATEWAY_URL);

            PhonePePayment phonePePayment = new PhonePePayment();

            PhonePePayment.PaymentInstrument paymentInstrument = new PhonePePayment.PaymentInstrument();
            if (merchantId != null) {
                phonePePayment.setMerchantId(merchantId);
            }
            if (merchantUserId != null) {
                phonePePayment.setMerchantUserId(merchantUserId);
            }
            if (callBackUrl != null) {
                phonePePayment.setCallbackUrl(callBackUrl);
            }
            if (redirectMode != null) {
                phonePePayment.setRedirectMode(redirectMode);
            }
            if (redirectUrl != null) {
                phonePePayment.setRedirectUrl(redirectUrl);
            }
            if (paymentInstrumentType != null) {
                paymentInstrument.setType(paymentInstrumentType);
                phonePePayment.setPaymentInstrument(paymentInstrument);
            }
            if (customerPaymentDTO.getAmount() != null) {
                phonePePayment.setAmount(Long.valueOf(customerPaymentDTO.getAmount()));
            }
            if (customerPaymentDTO.getMobileNumber() != null) {
                phonePePayment.setMobileNumber(customerPaymentDTO.getMobileNumber());
            }
            phonePePayment.setMerchantTransactionId(String.valueOf(generateUUID()));

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(phonePePayment);

            map.put("jsonPayload", jsonPayload);
            map.put("saltIndex", saltIndex);
            map.put("saltKey", saltKey);
            map.put("gatewayUrl", gatewayUrl);
            map.put("phonePePayment", phonePePayment);
            map.put("merchantId", merchantId);

            return map;

        } catch (Exception exception) {
            ApplicationLogger.logger.error("Error encounter while generating phonepe payload : " + exception.getMessage());
        }
        return map;
    }

    private static CloseableHttpClient createHttpClient() throws Exception {
        return HttpClients.custom()
                .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    private static HttpPost createHttpPost(String url) {
        return new HttpPost(URI.create(url));
    }

    public static Long generateUUID() {
        return Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }


    public void sendAndSaveDataForPayment(CustomerPaymentDTO customerPaymentDTO, PhonePePayment phonePePayment, String status, String merchantId, String paymentLink, String x_verifyChecksum) {
        try {
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            custPayDTOMessage.setOrderId(Long.valueOf(phonePePayment.getMerchantTransactionId()));
            if(customerPaymentDTO.getCustomerId()!=null)
                custPayDTOMessage.setCustId(customerPaymentDTO.getCustomerId());
            if(customerPaymentDTO.getPartnerId()!=null)
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            if(customerPaymentDTO.getAccountNumber()!=null)
                custPayDTOMessage.setAccountNumber(customerPaymentDTO.getAccountNumber());
            custPayDTOMessage.setPayment(Double.valueOf(customerPaymentDTO.getAmount())/100);
            custPayDTOMessage.setStatus(status);
            custPayDTOMessage.setPgTransactionId(phonePePayment.getMerchantTransactionId());
            custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
            custPayDTOMessage.setPlanId(customerPaymentDTO.getPlanId());
            custPayDTOMessage.setIsFromCaptive(customerPaymentDTO.getIsFromCaptive());
            custPayDTOMessage.setMerchantName(merchantId);
            custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
            custPayDTOMessage.setCustomerUsername(customerPaymentDTO.getCustomerUserName());
            custPayDTOMessage.setMvnoid(customerPaymentDTO.getMvnoId());
            custPayDTOMessage.setBuid(customerPaymentDTO.getBuid());
            custPayDTOMessage.setPaymentLink(paymentLink);
            custPayDTOMessage.setChecksum(x_verifyChecksum);
            if(customerPaymentDTO.getPartnerPaymentId()!=null)
                custPayDTOMessage.setPartnerPaymentId(customerPaymentDTO.getPartnerPaymentId());

//            messageSender.send(custPayDTOMessage, RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName()));

        } catch (Exception exception) {
            ApplicationLogger.logger.error(exception.getMessage());
        }
    }


    public String updatePaymentStatus(String code, String merchantId, String transactionId, String amount, String providerReferenceId, String checksum) {
        CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(transactionId));
        if (customerPayment != null) {
            if (code.equalsIgnoreCase("PAYMENT_SUCCESS")) {
                customerPayment.setStatus("Success");
                customerPayment.setPaymentDate(LocalDateTime.now());
                customerPayment.setTransactionDate(LocalDateTime.now());
                customerPayment.setPgTransactionId(providerReferenceId);
               // CustomerPayment savedCustomerPayment = customerPaymentRepository.save(customerPayment);
                CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
//                messageSender.send(custPayDTOMessage, RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName()));
            } else {
                customerPayment.setStatus("Failed");
                customerPayment.setPaymentDate(LocalDateTime.now());
                customerPayment.setTransactionDate(LocalDateTime.now());
                customerPayment.setPgTransactionId(providerReferenceId);
                //CustomerPayment savedCustomerPayment = customerPaymentRepository.save(customerPayment);
                CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
//                messageSender.send(custPayDTOMessage, RabbitMqConstants.QUEUE_SEND_PAYMENT_AUDIT_TO_CMS);
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName()));
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Payment Failed,Please Try again", null);
            }

        }
        return code;
    }
    public void updateIntegrationId(String orderId,String pgTransactionId ){
        CustomerPayment customerPayment = null;
        if(orderId!=null) {
            customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(orderId));
        }else{
            customerPayment = customerPaymentRepository.findByPgTransactionId(pgTransactionId);
        }
        customerPayment.setPgTransactionId(pgTransactionId);
        customerPaymentRepository.save(customerPayment);
        CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));

    }
    public void updateStatusAndSendToCMS(String orderId,String pgTransactionId ,String status ,String failuerReason){
        ApplicationLogger.logger.info("Entering updateStatusAndSendToCMS for orderId: " + orderId + " with status: " + status);

        CustomerPayment customerPayment = null;
        if(orderId!=null) {
//            customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(orderId));
            customerPayment = customerPaymentRepository.findByOrderIdAndGatewayStatusNot(Long.valueOf(orderId), "Successful");
            ApplicationLogger.logger.info("Fetched payment row: " + customerPayment);
        }else{
            customerPayment = customerPaymentRepository.findByPgTransactionIdAndGatewayStatusNot(pgTransactionId,"Successful");
            ApplicationLogger.logger.info("Fetched payment row: " + customerPayment);
        }
        if(customerPayment != null) {
            if(!customerPayment.getStatus().equalsIgnoreCase("SUCCESSFUL")){
                if (status.equalsIgnoreCase("SUCCESSFUL")) {
                    customerPayment.setStatus("Successful");
                    customerPayment.setGatewayStatus("Successful");
                    ApplicationLogger.logger.info("*********** SUCCESSFUL payment for orderId : " + customerPayment.getOrderId() + "***********");
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    customerPayment.setPaymentDate(LocalDateTime.now());
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    custPayDTOMessage.setPaymentGatewayName(customerPayment.getMerchantName());

                    if (custPayDTOMessage.getPlanId() != null && custPayDTOMessage.getInvoiceId() == null) {
                        ApplicationLogger.logger.info("*********** Invoice id is null so payment is for buy plan for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName(), "BUY_PLAN"));
                    } else {
                        ApplicationLogger.logger.info("*********** add amount to wallet for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName() , KafkaConstant.ADD_WALLET));
                    }

                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                } else {
                    ApplicationLogger.logger.info("*********** " + status + " payment for orderId : " + customerPayment.getOrderId() + "***********");
                    customerPayment.setGatewayStatus(status);
                    if(!customerPayment.getStatus().equalsIgnoreCase("SUCCESSFUL")) {
                        customerPayment.setStatus(status);
                    }
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    if(failuerReason != null){
                        customerPayment.setFailureDescription(failuerReason);
                    }
                    customerPayment.setTransactionDate(LocalDateTime.now());
                    ApplicationLogger.logger.info("Updating to SUCCESSFUL");
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                }
            } else {
                ApplicationLogger.logger.info("*********** Already SUCCESSFUL using manually " + status + " payment for orderId : " + customerPayment.getOrderId() + "***********");
                customerPayment.setGatewayStatus(status);
                if(!customerPayment.getStatus().equalsIgnoreCase("SUCCESSFUL")) {
                    customerPayment.setStatus(status);
                }
                if (pgTransactionId != null) {
                    customerPayment.setPgTransactionId(pgTransactionId);
                }
                if(failuerReason != null){
                    customerPayment.setFailureDescription(failuerReason);
                }
                customerPayment.setTransactionDate(LocalDateTime.now());
                ApplicationLogger.logger.info("About to save payment with status: " + customerPayment.getStatus());
                customerPaymentRepository.save(customerPayment);
                ApplicationLogger.logger.info("Payment saved successfully for orderId: " + customerPayment.getOrderId());
                CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                    tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                }
                if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                    tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                }
            }
        }
    }

    public void updateStatusAndSendToCMSForCRDB(String orderId,String pgTransactionId ,String status){
        ApplicationLogger.logger.info("Entering updateStatusAndSendToCMS for orderId: " + orderId + " with status: " + status);

        CustomerPayment customerPayment = null;
        if(orderId!=null) {
//            customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(orderId));
            customerPayment = customerPaymentRepository.findByOrderId(Long.valueOf(orderId));
            ApplicationLogger.logger.info("Fetched payment row: " + customerPayment);
        }else{
            customerPayment = customerPaymentRepository.findByPgTransaction(pgTransactionId).orElse(null);
            ApplicationLogger.logger.info("Fetched payment row: " + customerPayment);
        }
        if(customerPayment != null) {
            try {
                if (status.equalsIgnoreCase("Successful") || status.equalsIgnoreCase("SUCCESSFUL")) {
                    customerPayment.setStatus(CRDBConstant.SUCCESSFUL);
                    customerPayment.setGatewayStatus(CRDBConstant.SUCCESSFUL);
                    ApplicationLogger.logger.info("*********** APPROVED payment for orderId : " + customerPayment.getOrderId() + "***********");
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    customerPayment.setPaymentDate(LocalDateTime.now());
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    custPayDTOMessage.setPaymentGatewayName(customerPayment.getMerchantName());

                    if (custPayDTOMessage.getPlanId() != null && custPayDTOMessage.getInvoiceId() == null) {
                        ApplicationLogger.logger.info("*********** Invoice id is null so payment is for buy plan for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName(), "BUY_PLAN"));
                    } else {
                        ApplicationLogger.logger.info("*********** add amount to wallet for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName() , KafkaConstant.ADD_WALLET));
                    }
                } else {
                    ApplicationLogger.logger.info("*********** " + status + " payment for orderId : " + customerPayment.getOrderId() + "***********");
                    customerPayment.setGatewayStatus(status);
                    if(!customerPayment.getStatus().equalsIgnoreCase("approved")) {
                        customerPayment.setStatus(status);
                    }
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    customerPayment.setTransactionDate(LocalDateTime.now());
                    ApplicationLogger.logger.info("Updating to status: " + status);
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                }
            } catch (Exception e) {
                ApplicationLogger.logger.error("Failed to send Kafka message for orderId: " + customerPayment.getOrderId() + " Error: " + e.getMessage());
            }
        }
    }


    public void changeCustomerPaymentStatus(CustomerPayment customerPayment){
        customerPayment.setTransactionDate(LocalDateTime.now());
        customerPaymentRepository.save(customerPayment);
        CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName()));
    }

    public void updateTransactionStatusAndSendToCMS(String customerUuid ,String pgTransactionId ,String status, String failuerReason){
        CustomerPayment customerPayment = customerPaymentRepository.findByCustomerUUID(customerUuid);
        if(customerPayment != null) {
            if(!customerPayment.getStatus().equalsIgnoreCase("SUCCESSFUL")){
                if (status.equalsIgnoreCase("SUCCESSFUL")) {
                    customerPayment.setStatus("Successful");
                    customerPayment.setGatewayStatus("Successful");
                    ApplicationLogger.logger.info("*********** SUCCESSFUL payment for orderId : " + customerPayment.getOrderId() + "***********");
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    customerPayment.setPaymentDate(LocalDateTime.now());
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    custPayDTOMessage.setPaymentGatewayName(customerPayment.getMerchantName());

                    if (custPayDTOMessage.getPlanId() != null && custPayDTOMessage.getInvoiceId() == null) {
                        ApplicationLogger.logger.info("*********** Invoice id is null so payment is for buy plan for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName(), "BUY_PLAN"));
                    } else {
                        ApplicationLogger.logger.info("*********** add amount to wallet for orderId : " + customerPayment.getOrderId() + "***********");
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName() , KafkaConstant.ADD_WALLET));
                    }

                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                } else {
                    ApplicationLogger.logger.info("*********** " + status + " payment for orderId : " + customerPayment.getOrderId() + "***********");
                    customerPayment.setGatewayStatus(status);
                    if (pgTransactionId != null) {
                        customerPayment.setPgTransactionId(pgTransactionId);
                    }
                    if(failuerReason != null){
                        customerPayment.setFailureDescription(failuerReason);
                    }
                    customerPayment.setTransactionDate(LocalDateTime.now());
                    customerPaymentRepository.save(customerPayment);
                    CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                    kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                    if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                        tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                    }
                }
            } else {
                ApplicationLogger.logger.info("*********** Already SUCCESSFUL using manually " + status + " payment for orderId : " + customerPayment.getOrderId() + "***********");
                customerPayment.setGatewayStatus(status);
                if (pgTransactionId != null) {
                    customerPayment.setPgTransactionId(pgTransactionId);
                }
                if(failuerReason != null){
                    customerPayment.setFailureDescription(failuerReason);
                }
                customerPayment.setTransactionDate(LocalDateTime.now());
                customerPaymentRepository.save(customerPayment);
                CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY_USSD_PUSH)){
                    tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                }
                if(customerPayment.getMerchantName().equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL_USSD_PUSH)){
                    tradelanceService.sendPaymentStatus(customerPayment.getStatus(), customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), customerPayment.getPayment(), customerPayment.getAccountNumber(), customerPayment.getMvnoid());
                }
            }
        }
    }

    public Boolean IsTransactionStatusSuccess(String transactionId , String paymentStatus){
        Boolean status = false;
        Long orderId = Long.parseLong(transactionId);
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
        if(Objects.nonNull(customerPayment)){
            if(customerPayment.getStatus().equalsIgnoreCase(paymentStatus)){
                status = true;
            }
        }
        return status;
    }

    public OnlineInvoicePaymentDTO convertCustomerPaymentToOnlinePaymentDTO(CustomerPayment customerPayment){
        OnlineInvoicePaymentDTO onlineInvoicePaymentDTO = new OnlineInvoicePaymentDTO();
        onlineInvoicePaymentDTO.setPaymentGatewayName(customerPayment.getMerchantName());
        onlineInvoicePaymentDTO.setCustId(customerPayment.getCustId());
        onlineInvoicePaymentDTO.setAmount(customerPayment.getPayment());
        onlineInvoicePaymentDTO.setIsLco(false);
        onlineInvoicePaymentDTO.setMvnoId(customerPayment.getMvnoid());
        if(customerPayment.getBuid() != null) {
            onlineInvoicePaymentDTO.setBuId(Collections.singletonList(customerPayment.getBuid().longValue()));
        }
        onlineInvoicePaymentDTO.setCreatedById(customerPayment.getCreatedById());
        onlineInvoicePaymentDTO.setCreatedByName(customerPayment.getCreatedByName());
        onlineInvoicePaymentDTO.setInvoiceId(customerPayment.getInvoiceId());
        onlineInvoicePaymentDTO.setPartnerId(customerPayment.getPartnerId());
        onlineInvoicePaymentDTO.setTransactionNumber(customerPayment.getOrderId());
        return onlineInvoicePaymentDTO;

    }
    @Async
    public void updateAndsendFailedPaymentsToCMS(List<CustomerPayment> pendingPayments) {
        try{
            for(CustomerPayment payment : pendingPayments) {
                updateStatusAndSendToCMS(payment.getOrderId().toString(),payment.getPgTransactionId(),payment.getStatus(),null);
            }
        } catch (Exception e) {
            log.error("error while verifying and sent status to CMS for payment", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
