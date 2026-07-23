package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.PaystackPaymentMessage;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Model.PayStackPojo;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.commonMethods.IntegrationGenericMethods;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PayStackPaymentService {


    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    PaymentConfigService paymentConfigService;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;
    @Autowired
    private IntegrationGenericMethods integrationGenericMethods;
    public static final String VERIFY_URL = "https://api.paystack.co";
    public static final String MERCHANT_NAME = "PAYSTACK";

    public GenericDataDTO initiatePaymentService(CustomerPaymentDTO customerPaymentDTO,String authToken) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            Double walletAmount = 0.0;
            Double planPrice = 0.0;
            PayStackPojo payStackPojo = new PayStackPojo();
            if(customerPaymentDTO.getPlanId() != null){
                log.info("fetching wallet ammount from revenue by customerId : {},",customerPaymentDTO.getCustomerId());
                walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(), authToken);
                log.info("fetching plan price from CMS by planId : {},",customerPaymentDTO.getPlanId());
                planPrice =  cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(),authToken);
            }
            payStackPojo.setReference(customerPaymentDTO.getOrderId());
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            customerPaymentDTO.setPayerMobileNumber(customerPaymentDTO.getMobileNumber());
            log.info("fetching payment gateway details");
            payStackPojo = fetchPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYSTACK, customerPaymentDTO.getMvnoId());
            log.info("generating reference id for payment initiate");
            if(customerPaymentDTO.getMerchantName() == null || customerPaymentDTO.getMerchantName().isEmpty()){
                customerPaymentDTO.setMerchantName(MERCHANT_NAME);
            }
            CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            payStackPojo.setReference(customerPayment.getOrderId().toString());
            payStackPojo.setAmount(convertAmountToSubUnitAmount(customerPaymentDTO.getAmount()));
            payStackPojo.setEmail(customerPaymentDTO.getEmail());
            payStackPojo.setCustomerPaymentDTO(customerPaymentDTO);
            log.info("generating authorization url payment initiate for customerId : {},",customerPaymentDTO.getCustomerId());
            Map<String,Object> response= generateAuthorizationUrl(payStackPojo,customerPaymentDTO);
            return generateResponseFromPayStack(response); // generation genric response for authorization url
        } catch (FeignException e) {
            e.printStackTrace();
            if(e instanceof FeignException.Unauthorized){
                dataDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
                dataDTO.setResponseMessage(e.getMessage());
            }else if(e instanceof FeignException.NotFound) {
                dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                dataDTO.setResponseMessage(e.getMessage());
            } else if (e instanceof RetryableException) {
                dataDTO.setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
                dataDTO.setResponseMessage("CMS OR REVENUE instance not found for feign request");
            }else {
                dataDTO.setResponseMessage(e.getMessage());
                dataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return dataDTO;
        }
        return dataDTO;
    }


    public PayStackPojo fetchPaymentGatewayParameter(String paymentGatewayName, Integer mvnoId){
        try {
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(paymentGatewayName, mvnoId);
            String gatewayUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_REQUEST_URL);
            String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_SECRET_KEY);
            String publicKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_PUBLIC_KEY);
            String callBackUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_CALLBACK_URL);
            String payStackVerifyUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_VERIFY_URL);
            String scheduleTime = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYSTACK.PAYSTACK_SCHEDULE_TIME);

            return new PayStackPojo(gatewayUrl,callBackUrl,payStackVerifyUrl,scheduleTime,secretKey,publicKey);
        } catch (RuntimeException e) {
            throw new CustomValidationException(APIConstants.NOT_FOUND.intValue(),"No Payment Gateway Configuration found with name and mvnoid",null);
        }
    }

    String generateReferenceId(Integer customerId) {
        Random random = new Random();
        int randomDigit = random.nextInt(9) + 1;
        String id =   randomDigit + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return id;
    }

    private Map<String, Object> generateAuthorizationUrl(PayStackPojo payStackPojo,CustomerPaymentDTO customerPaymentDTO) {
        String responseBody = null;
        Map<String, Object> fullResponseMap = null;
        CloseableHttpResponse closeableHttpResponse = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(payStackPojo.getGatewayUrl()+URLConstants.PayStackPay.TRANSACTION_INITIALIZE);
            request.setHeader("Authorization", "Bearer " + payStackPojo.getSecretKey());
            request.setHeader("Content-Type", "application/json");
            HashMap<String,Object> requestParams = new HashMap<>();
            requestParams.put("email", payStackPojo.getEmail());
            requestParams.put("amount",payStackPojo.getAmount());
            requestParams.put("reference",payStackPojo.getReference());
            requestParams.put("callback_url",payStackPojo.getCallBackUrl());
            if(payStackPojo.getCurrency() != null){
                requestParams.put("currency",payStackPojo.getCurrency());
            }
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestParams));
            request.setEntity(entity);
            log.info("Sending request to gateway URL : {}",payStackPojo.getGatewayUrl());
            closeableHttpResponse = client.execute(request);
             LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            log.info("received paystack response from gateway URL : {}",closeableHttpResponse.getStatusLine().getReasonPhrase());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            responseBody = EntityUtils.toString(responseEntity, "UTF-8");
            fullResponseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            String errorMessage = null;
            if (closeableHttpResponse != null) {
                Integer response_code = closeableHttpResponse.getStatusLine().getStatusCode();
                if (response_code != 200) {
                    errorMessage = fullResponseMap.get("message").toString();
                    apiAuditsService.extractDataAndSavePostApiAudits(payStackPojo.getGatewayUrl() , null, closeableHttpResponse, request, null, responseTime, errorMessage, requestInitiationTime, responseBody,customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null,payStackPojo.getCustomerPaymentDTO().getMvnoId(),APIConstants.PAYSTACK_PAY,payStackPojo.getReference());
                }
                if (response_code == 400) {
                    errorMessage = fullResponseMap.get("message").toString();
                    apiAuditsService.extractDataAndSavePostApiAudits(payStackPojo.getGatewayUrl() , null, closeableHttpResponse, request, null, responseTime, errorMessage, requestInitiationTime, responseBody,customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null,payStackPojo.getCustomerPaymentDTO().getMvnoId(),APIConstants.PAYSTACK_PAY,payStackPojo.getReference());
                    log.error("paystack response code : {}",response_code);
                }
                if(fullResponseMap.get("status").equals(true) && response_code == 200) {
                    apiAuditsService.extractDataAndSavePostApiAudits(payStackPojo.getGatewayUrl() , null, closeableHttpResponse, request, null, responseTime, null, requestInitiationTime, responseBody, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null, payStackPojo.getCustomerPaymentDTO().getMvnoId() , APIConstants.PAYSTACK_PAY,payStackPojo.getReference());
                    scheduleForFetchingPaymentStatus(payStackPojo);
                }
            }
        } catch (Exception e) {
            log.error("error while generating paystack payment authorization url for customerId,Message: {}",payStackPojo.getCustomerPaymentDTO().getCustomerId(),e.getMessage(),e);
            e.printStackTrace();
        }
        return fullResponseMap;
    }


    private GenericDataDTO generateResponseFromPayStack(Map<String,Object> response) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        if (response != null) {
            if(response.get("status").equals(true)){
                HashMap<String,Object> data = (HashMap<String,Object>)response.get("data");
                genericDataDTO.setData(data);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage((String) response.get("message"));
            }else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage((String) response.get("message"));
            }
        }else {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("error while generation authorization url");
        }
        log.info("returning response for paystack payment request initiate");
        return genericDataDTO;
    }

    public Map<String,Object>  handleCallBackFromPayStack(String reference, HttpServletRequest req) {
        CloseableHttpResponse response = null;
        String responseBody = null;
        String transactionStatus = null;
        Map<String, Object> stringObjectMap = new HashMap<>();
        PayStackPojo payStackPojo = new PayStackPojo();
        CustomerPaymentDTO customerPaymentDTO = new CustomerPaymentDTO();
        CustomerPayment customerPaymentByOrder = new CustomerPayment();
        payStackPojo.setReference(reference);
        try {
            customerPaymentByOrder = customerPaymentRepository.findByOrderId(Long.parseLong(reference));
            customerPaymentDTO.setMvnoId(customerPaymentByOrder.getMvnoid());
            if(customerPaymentByOrder.getMvnoid() == null){
                stringObjectMap.put("message","mvnoid not found for reference"+ reference);
                stringObjectMap.put("status",false);
                return stringObjectMap;
            }
            payStackPojo = fetchPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYSTACK, customerPaymentDTO.getMvnoId());
        } catch (Exception e) {
            log.error("Error while fetching paystack gateway parameters and mvnoId");
           e.printStackTrace();
        }
        try {
           CloseableHttpClient client = HttpClients.createDefault();
            if(payStackPojo.getVerifyUrl() != null && payStackPojo.getVerifyUrl().endsWith("/")){
                payStackPojo.setVerifyUrl(payStackPojo.getVerifyUrl().replace("/",""));
            }else {
                payStackPojo.setVerifyUrl(VERIFY_URL);
            }
            HttpGet request = new HttpGet(payStackPojo.getVerifyUrl()+URLConstants.PayStackPay.TRANSACTION_VERIFY+reference);
            request.setHeader("Authorization", "Bearer " + payStackPojo.getSecretKey());
            response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            responseBody = EntityUtils.toString(responseEntity, "UTF-8");

               stringObjectMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
               if(response.getStatusLine().getStatusCode() == 200) {
                   if(stringObjectMap != null){
                       boolean responseStatus = (boolean)stringObjectMap.get("status");
                       if(responseStatus){
                           HashMap<String,Object> data = (HashMap<String,Object>)stringObjectMap.get("data");
                           transactionStatus = (String)data.get("status");
                           if(transactionStatus.equalsIgnoreCase("success")){
                               paymentIntegrationService.updateStatusAndSendToCMS((String) data.get("reference"),data.get("id").toString(),transactionStatus,null);
                               PaystackPaymentMessage paymentMessage= new PaystackPaymentMessage(customerPaymentByOrder.getCustId(),transactionStatus,reference,null);
                               kafkaMessageSender.send(new KafkaMessageData(paymentMessage,paymentMessage.getClass().getSimpleName()));
                               log.info("*********PaystackPaymentMessage kafka message sent to revenue*********");
                           }else {
                               paymentIntegrationService.updateStatusAndSendToCMS((String) data.get("reference"),data.get("id").toString(),transactionStatus,data.get("gateway_response").toString());
                           }
                           if(data.get("message") == null){
                               data.put("message",transactionStatus);
                           }

                           HashMap<String, Object> objectHashMap = extractResponseFromCallBack(data);

                           stringObjectMap.put("data", objectHashMap);
                       }else {
                           stringObjectMap.put("message","error while handling payment gateway response");
                       }
                   }else {
                       stringObjectMap.put("message","error while fetching payment gateway response");
                   }
               }
               return  stringObjectMap;
        } catch (Exception e) {
            log.error("error while processing paystack callback; Message: {}",e.getMessage(),e);
            e.printStackTrace();
        }
        return stringObjectMap;
    }

    public HashMap<String,Object> extractResponseFromCallBack(HashMap<String, Object> data) {
        HashMap<String,Object> response = new HashMap<>();
        response.put("id",data.get("id"));
        response.put("reference",data.get("reference"));
        response.put("gateway_response",data.get("gateway_response"));
        response.put("amount",convertSubUnitToAmount(data.get("amount").toString()));
        response.put("message",data.get("message"));
        response.put("order_id",data.get("reference"));
        response.put("channel",data.get("channel"));
        response.put("currency",data.get("currency"));
        response.put("paid_at",data.get("paid_at"));
        response.put("transaction_date",data.get("transaction_date"));
        response.put("createdAt",data.get("createdAt"));
        response.put("status",data.get("status"));
        return response;
    }

    /*this method is commented because this method is created as generic for all payment gateways*/
//    private CustomerPayment sendAndSavePayStackDataForPayment(PayStackPojo payStackPojo,String status) {
//        CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
//        custPayDTOMessage.setOrderId(Long.parseLong(payStackPojo.getReference()));
//        if (payStackPojo.getCustId() != null)
//            custPayDTOMessage.setCustId(payStackPojo.getCustId());
//        if (payStackPojo.getPartnerId() != null)
//            custPayDTOMessage.setPartnerId(payStackPojo.getPartnerId());
//        if (payStackPojo.getAccountNumber() != null)
//            custPayDTOMessage.setAccountNumber(payStackPojo.getAccountNumber());
//
//        custPayDTOMessage.setPayment(Double.valueOf(payStackPojo.getAmount()));
//        custPayDTOMessage.setStatus(status);
//        custPayDTOMessage.setGatewayStatus(status);
//        custPayDTOMessage.setPlanId(payStackPojo.getPlanId());
//        custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
//        custPayDTOMessage.setMerchantName(payStackPojo.getMerchantName());
//        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
//        custPayDTOMessage.setCustomerUsername(payStackPojo.getCustomerUsername());
//        custPayDTOMessage.setMvnoid(payStackPojo.getMvnoid());
//        if(payStackPojo.getBuid() != null){
//            custPayDTOMessage.setBuid(payStackPojo.getBuid());
//        }
//        custPayDTOMessage.setIsAdvancePayment(payStackPojo.getIsAdvancePayment());
//
//        if(payStackPojo.getCustServiceMappingId() != null){
//            custPayDTOMessage.setCustServiceMappingId(payStackPojo.getCustServiceMappingId());
//        }
//        custPayDTOMessage.setCustomerUUID(payStackPojo.getCustomerUUID());
//        CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//        customerPayment.setId(getLatestId());
//        if(payStackPojo.getInvoiceId() != null){
//            customerPayment.setInvoiceId(payStackPojo.getInvoiceId());
//        }if(payStackPojo.getIsAdvancePayment()!=null) {
//            customerPayment.setIsAdvancePayment(payStackPojo.getIsAdvancePayment());
//        }if(payStackPojo.getWalletAmount() != null) {
//            customerPayment.setWalletAmount(payStackPojo.getWalletAmount());
//        }if(payStackPojo.getPlanPrice() != null) {
//            customerPayment.setPlanPrice(payStackPojo.getPlanPrice());
//        }if(payStackPojo.getPayerMobileNumber() != null) {
//            customerPayment.setPayerMobileNumber(payStackPojo.getPayerMobileNumber());
//        }
//        customerPayment.setCreatedById(getLoggedInUser().getUserId());
//        customerPayment.setCreatedByName(getLoggedInUser().getUsername());
//        log.info("Send Initiated Request of PayStack Data to CMS for  reference CustomerUUID: {}",customerPayment.getCustomerUUID());
//        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
//        return customerPaymentRepository.save(customerPayment);
//    }

    public void validateRequestForInitiate(CustomerPaymentDTO customerPaymentDTO) {
        if(customerPaymentDTO.getCustomerId() == null){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"customerId cannot be null",null);
        }
        if(customerPaymentDTO.getAmount() == null){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"amount cannot be null",null);
        }
        if (customerPaymentDTO.getMvnoId() == null){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"mvnoid cannot be null",null);
        }
        if(customerPaymentDTO.getEmail() == null){
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"email cannot be null",null);
        }
    }
    public LoggedInUser getLoggedInUser() {
        try {
            LoggedInUser loggedInUser = null;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            loggedInUser  = (LoggedInUser)securityContext.getAuthentication().getPrincipal();
            return loggedInUser;
        } catch (Exception e) {
            log.error("error while getting logged in user; {}",e.getMessage(),e);
        }
        return null;
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


    public GenericDataDTO verifyTransaction(String reference) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CloseableHttpResponse response = null;
        HashMap<String, Object> responseMap = new HashMap<>();
        Integer responseCode = null;
        String transactionStatus = null;
            PayStackPojo payStackPojo = new PayStackPojo();
        List<CustomerPayment> customerPaymentList = customerPaymentRepository.findAllByOrderId(Long.parseLong(reference));
        CustomerPaymentDTO paymentDTO = new CustomerPaymentDTO();
        if(customerPaymentList == null || customerPaymentList.isEmpty()) {
            genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
            genericDataDTO.setResponseMessage("payment with reference: " + reference + " is not found");
            return genericDataDTO;
        }
        paymentDTO.setCustomerId(customerPaymentList.get(0).getCustId());
        paymentDTO.setMvnoId(customerPaymentList.get(0).getMvnoid());
        payStackPojo = fetchPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYSTACK, customerPaymentList.get(0).getMvnoid());
        // setting customer Id and mvnoId to pojo
        payStackPojo.setCustomerPaymentDTO(paymentDTO);
        try{
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            if(payStackPojo.getVerifyUrl() != null && payStackPojo.getVerifyUrl().endsWith("/")){
                payStackPojo.setVerifyUrl(payStackPojo.getVerifyUrl().replace("/",""));
            }else{
                payStackPojo.setVerifyUrl(VERIFY_URL);
            }
            HttpGet request = new HttpGet(payStackPojo.getVerifyUrl()+URLConstants.PayStackPay.TRANSACTION_VERIFY+reference);
            payStackPojo.setVerifyUrl(payStackPojo.getVerifyUrl()+URLConstants.PayStackPay.TRANSACTION_VERIFY+reference);
            request.setHeader("Authorization", "Bearer " + payStackPojo.getSecretKey());
            HttpHeaders httpHeaders = Arrays.stream(request.getAllHeaders())
                    .collect(HttpHeaders::new,
                            (headers, header) -> headers.add(header.getName(), header.getValue()),
                            HttpHeaders::putAll);
            response = client.execute(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            responseMap = objectMapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), new TypeReference<HashMap<String, Object>>() {});
            responseCode = response.getStatusLine().getStatusCode();
            if(responseCode != 200){
                genericDataDTO.setResponseCode(responseCode);
                genericDataDTO.setResponseMessage(response.getStatusLine().getReasonPhrase());
                apiAuditsService.setAuditForCallback(payStackPojo.getVerifyUrl().toString(),"reference:"+reference, ResponseEntity.status(HttpStatus.OK).body(responseMap) , httpHeaders , responseTime , requestInitiationTime , null , 1, "GET" , response.getStatusLine().getReasonPhrase(),"PAYSTACK",payStackPojo.getReference());
            }else if(responseCode == 200){
                if(!(boolean)responseMap.get("status")){
                    genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                    genericDataDTO.setResponseMessage(responseMap.get("message").toString());
                }else if((boolean)responseMap.get("status")){
                    HashMap<String,Object> data = (HashMap<String, Object>) responseMap.get("data");
                    transactionStatus = data.get("status").toString();
                    if(transactionStatus.equalsIgnoreCase("success")){
                            paymentIntegrationService.updateStatusAndSendToCMS((String) data.get("reference"),data.get("id").toString(),transactionStatus,null);
                            log.info("Successfully updated paystack transaction status to {} ,for orderID: {}",transactionStatus,data.get("reference").toString());
                            PaystackPaymentMessage paymentMessage= new PaystackPaymentMessage(paymentDTO.getCustomerId(),transactionStatus,reference,null);
                            kafkaMessageSender.send(new KafkaMessageData(paymentMessage,paymentMessage.getClass().getSimpleName()));
                            log.info("*********PaystackPaymentMessage kafka message sent to revenue for verify transaction*********");
                    }else {
                        paymentIntegrationService.updateStatusAndSendToCMS((String) data.get("reference"),data.get("id").toString(),transactionStatus,data.get("gateway_response").toString());
                        log.info("Successfully updated paystack transaction status to {} ,for orderID: {}",transactionStatus,data.get("reference").toString());
                    }
                    apiAuditsService.setAuditForCallback(payStackPojo.getVerifyUrl().toString(),"reference:"+reference, ResponseEntity.status(HttpStatus.OK).body(responseMap) , httpHeaders, responseTime , requestInitiationTime , null , 1, "GET" , null,"PAYSTACK",payStackPojo.getReference());

                    HashMap<String, Object> responseData = generateVerifyTrxResponse(data, payStackPojo);
                    genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                    genericDataDTO.setResponseMessage(responseMap.get("message").toString());
                    genericDataDTO.setData(responseData);
                }
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(e.getMessage());
            log.error("error while verifying transaction; {}",e.getMessage(),e);
            e.printStackTrace();
        }
        return genericDataDTO;
    }

    public HashMap<String, Object> generateVerifyTrxResponse(HashMap<String, Object> data,PayStackPojo payStackPojo) {
        // this method is used for add or update customer data in verify response
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("id",data.get("id"));
        responseMap.put("domain",data.get("domain"));
        responseMap.put("gateway_response",data.get("gateway_response"));
        responseMap.put("status",data.get("status"));
        responseMap.put("reference",data.get("reference"));
        responseMap.put("amount",convertSubUnitToAmount(data.get("amount").toString()));
        responseMap.put("paid_at",data.get("paid_at"));
        responseMap.put("via_channel",data.get("channel"));
        responseMap.put("currency",data.get("currency"));
        responseMap.put("requested_amount",data.get("requested_amount"));
        responseMap.put("transaction_date",data.get("transaction_date"));
        responseMap.put("mvnoId",payStackPojo.getCustomerPaymentDTO().getMvnoId());
        responseMap.put("customerId",payStackPojo.getCustomerPaymentDTO().getCustomerId());
        return responseMap;
    }

    public void scheduleForFetchingPaymentStatus(PayStackPojo payStackPojo) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call orderStatus API
            try {
                verifyTransaction(payStackPojo.getReference());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Long.valueOf(payStackPojo.getScheduleTime()), TimeUnit.MINUTES);
    }

    public String convertAmountToSubUnitAmount(String amount) {
            BigDecimal bdAmount = new BigDecimal(amount);
            BigDecimal subUnitAmount = bdAmount.multiply(BigDecimal.valueOf(100));
            return subUnitAmount.setScale(0, RoundingMode.DOWN).toBigInteger().toString();
    }
    public String convertSubUnitToAmount(String subUnitAmount) {
            BigDecimal bdSubUnit = new BigDecimal(subUnitAmount);
            BigDecimal amount = bdSubUnit.divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
            return amount.toPlainString();
    }

}
