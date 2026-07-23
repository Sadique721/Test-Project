package com.savbill.integrationsystem.AirtelIntigration;

import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.savbill.integrationsystem.utility.SendRestApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AirtelIntigrationService {

    private static final Logger logger = LoggerFactory.getLogger(AirtelIntigrationService.class);

    @Autowired
    private SendRestApiService sendRestApiService;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private RevenueClient revenueClient;


    public GenericDataDTO createAirtelpayment(CustomerPaymentDTO customerPaymentDTO, String authToken){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        AirtelPaymentResponseDTO airtelPaymentResponseDTO = new AirtelPaymentResponseDTO();
        try {
            logger.info("Airtel payment intiate api commited");
            Double walletAmount = 0.0;
            Double planPrice = 0.0;
            if(customerPaymentDTO.getPlanId() != null) {
                walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(),authToken);
                planPrice =  cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(),authToken);
            }
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            customerPaymentDTO.setPayerMobileNumber(customerPaymentDTO.getMobileNumber());

            CustomerPayment customerPayment = sendAndSaveAirtelDataForPayment(customerPaymentDTO, "Initiate");
            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();
            /**Get payment parameter**/
            HashMap<String, String> getPaymentParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL, mvnoId);
            /**Call the token api**/
            AirtelAuthorizationRequestDTO airtelAuthorizationRequestDTO = createAirtelAuthorizationPayload(getPaymentParameter);
            AirtelApiAuthorizationResponseDTO airtelApiAuthorizationResponseDTO = getTokenfromAirtelMoney(airtelAuthorizationRequestDTO , getPaymentParameter , mvnoId,customerPayment);
            /**Call the payment api called**/
            AirtelPaymentRequestDTO airtelPaymentRequestDTO = createAirtelPaymentPayload(customerPayment , customerPaymentDTO , getPaymentParameter);
            airtelPaymentResponseDTO = createPaymentRequest(airtelPaymentRequestDTO, airtelApiAuthorizationResponseDTO , getPaymentParameter ,mvnoId,customerPayment);
            /**According to api status do operation below**/
            getStatusByResponseCodeAndDoOperationIt(airtelPaymentResponseDTO.getStatus().getResponse_code(), customerPayment,getPaymentParameter, airtelPaymentResponseDTO.getStatus().getMessage());
            /**All code ended**/
            genericDataDTO.setResponseMessage("Airtel Payment send successfully");
            genericDataDTO.setResponseCode(HttpStatus.SC_OK);
            genericDataDTO.setData(airtelPaymentResponseDTO);
        }
        catch (CustomValidationException exception){
            genericDataDTO.setResponseCode(HttpStatus.SC_EXPECTATION_FAILED);
            genericDataDTO.setResponseMessage(exception.getMessage());
            genericDataDTO.setData(airtelPaymentResponseDTO);
        }
        catch(Exception e){
            genericDataDTO.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage("Something Went Wrong");
            genericDataDTO.setData(airtelPaymentResponseDTO);
        }
        return genericDataDTO;
    }

    public AirtelApiAuthorizationResponseDTO getTokenfromAirtelMoney(AirtelAuthorizationRequestDTO authorizationRequestDTO , HashMap<String , String> paymentGatewayParam , Integer mvnoId,CustomerPayment customerPayment){
        AirtelApiAuthorizationResponseDTO authorizationResponseDTO = new AirtelApiAuthorizationResponseDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            ObjectMapper mapper = new ObjectMapper();
            logger.info("Request for token api: "+authorizationRequestDTO);
            String dto = mapper.writeValueAsString(authorizationRequestDTO);
            String tokenResponse = sendRestApiService.sendHttpAirtelPostRequest(paymentGatewayParam.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_API_URL)+AirtelApiConstant.API_CALLS.GET_TOKEN , dto,null, null , null,mvnoId,customerPayment);
            authorizationResponseDTO = objectMapper.readValue(tokenResponse, AirtelApiAuthorizationResponseDTO.class);
            logger.info("Response from token generation api: "+authorizationResponseDTO.toString());
        }
        catch (Exception e){
            authorizationResponseDTO.setToken_type(null);
            authorizationResponseDTO.setExpires_in(null);
            authorizationResponseDTO.setAccess_token(e.getMessage());
        }
        return  authorizationResponseDTO;
    }


    public AirtelPaymentResponseDTO createPaymentRequest(AirtelPaymentRequestDTO airtelPaymentRequestDTO , AirtelApiAuthorizationResponseDTO airtelApiAuthorizationResponseDTO , HashMap<String , String> paymentGatewayParam , Integer mvnoId,CustomerPayment customerPayment) throws JsonProcessingException {
        AirtelPaymentResponseDTO airtelPaymentResponseDTO = new AirtelPaymentResponseDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        String tokenResponse = null;
        try{
            logger.info("Request for airtel payment  api: "+airtelPaymentRequestDTO);
            ObjectMapper mapper = new ObjectMapper();
            String dto = mapper.writeValueAsString(airtelPaymentRequestDTO);
            tokenResponse = sendRestApiService.sendHttpAirtelPostRequest(paymentGatewayParam.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_API_URL)+AirtelApiConstant.API_CALLS.CREATE_PAYMENT , dto,airtelApiAuthorizationResponseDTO.getToken_type()+" "+airtelApiAuthorizationResponseDTO.getAccess_token(),"UG","UGX",mvnoId,customerPayment);
            airtelPaymentResponseDTO = objectMapper.readValue(tokenResponse, AirtelPaymentResponseDTO.class);
            logger.info("Response from airtel payment  api: "+airtelPaymentResponseDTO.toString());
        }
        catch (UnrecognizedPropertyException e) {
            logger.error("UnrecognizedPropertyException: " + e.getMessage());
            JsonNode rootNode = objectMapper.readTree(tokenResponse);
            String errorMessage = rootNode.has("message") ? rootNode.get("message").asText() : "Unknown error occurred";
            airtelPaymentResponseDTO = new AirtelPaymentResponseDTO();
            airtelPaymentResponseDTO.setStatus(null);
            airtelPaymentResponseDTO.setData(null);
            airtelPaymentResponseDTO.setMessage(errorMessage);
            return airtelPaymentResponseDTO;
        }
        catch (Exception e){
            logger.error(e.getMessage());
            airtelPaymentResponseDTO.setData(null);
            airtelPaymentResponseDTO.setStatus(null);
            airtelPaymentResponseDTO.setMessage(e.getMessage());
        }
        return  airtelPaymentResponseDTO;
    }

    public void checkTransactionStatusApi(CustomerPayment customerPayment , HashMap<String , String> paymentParameter , AirtelApiAuthorizationResponseDTO airtelApiAuthorizationResponseDTO) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        logger.info("::::::::::::::::: URL for get AIRTEL payment status for Order ID: {}", AirtelApiConstant.API_CALLS.CHECK_PAYMENT_STATUS + customerPayment.getOrderId());
        HttpGet request = new HttpGet(paymentParameter.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_API_URL)+AirtelApiConstant.API_CALLS.CHECK_PAYMENT_STATUS + customerPayment.getOrderId());
        request.setHeader("Authorization", airtelApiAuthorizationResponseDTO.getToken_type()+" "+airtelApiAuthorizationResponseDTO.getAccess_token());
        request.addHeader("X-Country", paymentParameter.get(PaymentGatewayConfigurationConstant.AIRTEL.COUNTRY));
        request.addHeader("X-Currency", paymentParameter.get(PaymentGatewayConfigurationConstant.AIRTEL.CURRENCY));
        CloseableHttpResponse response = client.execute(request);
        LocalDateTime requestCompletionTime = LocalDateTime.now();
        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime,requestCompletionTime);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        logger.warn(" ::::::::::::::: AIRTEL PAY Response  :::::::::::::::  {}",responseBody);
        System.out.println(" ::::::::::::::: AIRTEL PAY Response  :::::::::::::::  "+responseBody);
        AirtelApiCheckStatusResponseDTO airtelApiCheckStatusResponseDTO = mapper.readValue(responseBody , AirtelApiCheckStatusResponseDTO.class);
        apiAuditsService.extractDataAndSaveGetApiAuditsForAirtel(paymentParameter.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_API_URL)+AirtelApiConstant.API_CALLS.CHECK_PAYMENT_STATUS + customerPayment.getOrderId(),null,response,request,responseTime,responseBody.toString(),requestInitiationTime,responseBody,null,customerPayment.getMvnoid(),customerPayment.getOrderId().toString());
        String responseCode = airtelApiCheckStatusResponseDTO.getStatus().getResponseCode();
        String airtelTransactionId = null;
        String status = null;
        String message = airtelApiCheckStatusResponseDTO.getStatus().getMessage();
        if(airtelApiCheckStatusResponseDTO.getData() != null){
            airtelTransactionId = airtelApiCheckStatusResponseDTO.getData().getTransaction().getAirtelMoneyId();
            logger.warn(" ::::::::::::::: AIRTEL PAY Response CODE  :::::::::::::::  {}",airtelApiCheckStatusResponseDTO.getStatus().getResponseCode());
            status = airtelApiCheckStatusResponseDTO.getData().getTransaction().getStatus();
        } else {
            status = AirtelApiConstant.getStatusAbbreviation(responseCode);
        }
        if (AirtelApiConstant.STATUS_CODE.Success.equals(responseCode)) {
            paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString() ,airtelTransactionId,"SUCCESSFUL" , customerPayment.getFailureDescription());
        } else {
            updateCustomerPaymentStatus(customerPayment, airtelTransactionId, status, message);
        }
        logger.info("Response for check status api: "+responseBody);
    }


    private void updateCustomerPaymentStatus(CustomerPayment customerPayment, String airtelTransactionId, String status, String message) {
        customerPayment.setPgTransactionId(airtelTransactionId);
        customerPayment.setFailureDescription(message);
        if (!"SUCCESSFUL".equalsIgnoreCase(customerPayment.getStatus())) {
            customerPayment.setStatus(status);
        }
        customerPayment.setGatewayStatus(status);
        customerPaymentRepository.save(customerPayment);
        paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
    }

    public AirtelAuthorizationRequestDTO createAirtelAuthorizationPayload(HashMap<String , String> paymentParameterName){
        AirtelAuthorizationRequestDTO airtelAuthorizationRequestDTO = new AirtelAuthorizationRequestDTO();
        airtelAuthorizationRequestDTO.setGrant_type(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_GRANT_TYPE));
        airtelAuthorizationRequestDTO.setClient_id(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_CLIENT_ID));
        airtelAuthorizationRequestDTO.setClient_secret(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_CLIENT_SECRET));
        return airtelAuthorizationRequestDTO;
    }


    public AirtelPaymentRequestDTO createAirtelPaymentPayload(CustomerPayment customerPayment, CustomerPaymentDTO customerPaymentDTO , HashMap<String , String> paymentParameterName){
        AirtelPaymentRequestDTO airtelPaymentRequestDTO =  new AirtelPaymentRequestDTO();
        if(customerPayment.getAccountNumber() != null && !customerPayment.getAccountNumber().isEmpty()){
            airtelPaymentRequestDTO.setReference(customerPayment.getAccountNumber());
        }
        else {
            airtelPaymentRequestDTO.setReference(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_REFERENCE));
        }
        AirtelPaymentRequestDTO.Subscriber subscriber = new AirtelPaymentRequestDTO.Subscriber();
        subscriber.setCountry(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.COUNTRY));
        subscriber.setCurrency(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.CURRENCY));
        subscriber.setMsisdn(customerPaymentDTO.getMobileNumber());
        airtelPaymentRequestDTO.setSubscriber(subscriber);
        AirtelPaymentRequestDTO.Transaction transaction = new AirtelPaymentRequestDTO.Transaction();
        transaction.setCountry(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.COUNTRY));
        transaction.setCurrency(paymentParameterName.get(PaymentGatewayConfigurationConstant.AIRTEL.CURRENCY));
        transaction.setAmount((int) Math.round(customerPayment.getPayment()));
        transaction.setId(customerPayment.getOrderId().toString());
        airtelPaymentRequestDTO.setTransaction(transaction);
        return airtelPaymentRequestDTO;
    }

    public CustomerPayment sendAndSaveAirtelDataForPayment(CustomerPaymentDTO customerPaymentDTO, String status) {
        CustomerPayment customerPayment = new CustomerPayment();
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
            if(customerPaymentDTO.getCustomerUUID() != null) {
                custPayDTOMessage.setCustomerUUID(customerPaymentDTO.getCustomerUUID());
            }
            customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(getLatestId());
            if(customerPaymentDTO.getInvoiceId() != null){
                customerPayment.setInvoiceId(customerPaymentDTO.getInvoiceId());
            }
            if(customerPaymentDTO.getIsAdvancePayment()!=null) {
                customerPayment.setIsAdvancePayment(customerPaymentDTO.getIsAdvancePayment());
            }
            if(customerPaymentDTO.getWalletAmount() != null) {
                customerPayment.setWalletAmount(customerPaymentDTO.getWalletAmount());
            }if(customerPaymentDTO.getPlanPrice() != null) {
                customerPayment.setPlanPrice(customerPaymentDTO.getPlanPrice());
            }if(customerPaymentDTO.getPayerMobileNumber() != null) {
                customerPayment.setPayerMobileNumber(customerPaymentDTO.getPayerMobileNumber());
            }
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
            customerPayment = customerPaymentRepository.save(customerPayment);
            ApplicationLogger.logger.info("Send Initiated Request of Airtel Data to CMS for referenceId: "+customerPayment.getOrderId());
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
        } catch (Exception e) {
            logger.error("Error While Sending Data of Airtel to CMS Through Kafka. ", e.getMessage());
        }
        return customerPayment;
    }

    public static Long generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return Long.parseLong(id);
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

    /**This function do operation according to airtel given status**/
    public void getStatusByResponseCodeAndDoOperationIt(String responseCode,CustomerPayment customerPayment , HashMap<String , String> paymentGatewayParam, String message){
        String status = AirtelApiConstant.getStatusAbbreviation(responseCode);
        switch (responseCode) {
            case (AirtelApiConstant.STATUS_CODE.Ambiguous):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "The transaction is still processing and is in ambiguous state. Please do the transaction enquiry to fetch the transaction status.",null);
            case (AirtelApiConstant.STATUS_CODE.Success): /**This is for success transaction**/
                paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString() , null,"SUCCESSFUL" , null);
                break;
            case (AirtelApiConstant.STATUS_CODE.Incorrect_Pin):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Incorrect pin has been entered.",null);
            case (AirtelApiConstant.STATUS_CODE.Exceeds_withdrawal_amount_limit):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "The User has exceeded their wallet allowed transaction limit.",null);
            case (AirtelApiConstant.STATUS_CODE.Invalid_Amount):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "The amount User is trying to transfer is less than the minimum amount allowed.",null);
            case (AirtelApiConstant.STATUS_CODE.Transaction_ID_is_invalid):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "User didn't enter the pin.",null);
            case (AirtelApiConstant.STATUS_CODE.In_process):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                customerPaymentRepository.save(customerPayment);
                CustPayDTOMessage custPayDTOMessage = customerPaymentService.convertEntityToMessage(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage,custPayDTOMessage.getClass().getSimpleName()));
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.schedule(() -> {
                    // Call paymentStatus API
                    checkAirtelPaymentStatus(paymentGatewayParam, customerPayment.getMvnoid());
                }, Long.parseLong(paymentGatewayParam.get(PaymentGatewayConfigurationConstant.AIRTEL.SCHEDULED_CALL_TIME)), TimeUnit.MINUTES);
                break;
            case (AirtelApiConstant.STATUS_CODE.Not_enough_balance):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "User wallet does not have enough money to cover the payable amount.",null);
            case (AirtelApiConstant.STATUS_CODE.Refused):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "The transaction was refused.",null);
            case (AirtelApiConstant.STATUS_CODE.Transaction_not_permitted_to_Payee):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Payee is already initiated for churn or barred or not registered on Airtel Money platform." ,null);
            case (AirtelApiConstant.STATUS_CODE.Transaction_Timed_Out):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "The transaction was timed out.",null);
            case (AirtelApiConstant.STATUS_CODE.Transaction_Not_Found):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED,"The transaction was not found.",null);
            case (AirtelApiConstant.STATUS_CODE.Forbidden):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "X-signature and payload did not match.",null);
            case (AirtelApiConstant.STATUS_CODE.Transaction_Expired):
                customerPayment.setStatus(status);
                customerPayment.setGatewayStatus(status);
                customerPayment.setFailureDescription(message);
                paymentIntegrationService.changeCustomerPaymentStatus(customerPayment);
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED , "Transaction has been expired.",null);
        }
    }

    public void checkAirtelPaymentStatus(HashMap<String , String> getPaymentParameter , Integer mvnoId){
        CloseableHttpResponse response = null;
        try{
            List<String> status = Arrays.asList("pending","tip");
            List<CustomerPayment> paymentList = customerPaymentRepository.findAllByStatusAndIsScheduledAndMvnoId(status,false , mvnoId);
            if(!paymentList.isEmpty()) {
                logger.info("Payment list found with pending status and isscheduled false with mvnoId: "+mvnoId+" size: "+paymentList.size());
                AirtelAuthorizationRequestDTO airtelAuthorizationRequestDTO = createAirtelAuthorizationPayload(getPaymentParameter);
                AirtelApiAuthorizationResponseDTO airtelApiAuthorizationResponseDTO = getTokenfromAirtelMoney(airtelAuthorizationRequestDTO, getPaymentParameter, mvnoId,null);
                paymentList.forEach(payment -> payment.setIsScheduled(true));
                customerPaymentRepository.saveAll(paymentList);
                for (CustomerPayment payment : paymentList) {
                    checkTransactionStatusApi(payment, getPaymentParameter, airtelApiAuthorizationResponseDTO);
                }
            }
            else{
                logger.warn("No pending payment list with isscheduled false is found for mvnoId: "+mvnoId);
            }

        }catch (Exception e){
            ApplicationLogger.logger.error("Failed to fetch MomoPe Transaction status " + e.getMessage());
        }
    }

}
