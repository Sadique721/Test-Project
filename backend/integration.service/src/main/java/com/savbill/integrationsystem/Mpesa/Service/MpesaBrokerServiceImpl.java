package com.savbill.integrationsystem.Mpesa.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.AirtelValidateTxValidator;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaBrokerRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaC2BRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.TransactionStatusRequestDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaBrokerResponseDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaC2BValidateResponseDTO;
import com.savbill.integrationsystem.Mpesa.ResponseDTO.MpesaQrResponseDTO;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.commonMethods.IntegrationGenericMethods;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.MDC;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MpesaBrokerServiceImpl implements MpesaBrokerService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CMSClient cmsClient;
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    @Autowired
    private CustomerPaymentService customerPaymentService;
    @Autowired
    private RevenueClient revenueClient;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private PaymentIntegrationService paymentIntegrationService;
    @Autowired
    private PaymentConfigService paymentConfigService;
    private static final Logger logger = LoggerFactory.getLogger(MpesaBrokerServiceImpl.class);
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\d{14}");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\+\\d{4}");
    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private IntegrationGenericMethods integrationGenericMethods;

    /* using mvnoId to generate token*/
    @Value("${selcom.reverseFlow.mvnoId}")
    private Integer selcomReverseFlowMvnoId;
    private static final String qrCodeSize="300";
    @Override
    public void validateProcessTxRequestData(MpesaBrokerRequestDTO request) {
        if (request.getRequest().getServiceProvider().getSpId() <= 0) {
            throw new IllegalArgumentException("Invalid spId: Must be a positive long value.");
        }
        if (request.getRequest().getServiceProvider().getSpPassword() == null
                || request.getRequest().getServiceProvider().getSpPassword().isEmpty()) {
            throw new IllegalArgumentException("Invalid spPassword: Cannot be null or empty.");
        }
        if (!TIMESTAMP_PATTERN.matcher(String.valueOf(request.getRequest().getServiceProvider().getTimestamp())).matches()) {
            throw new IllegalArgumentException("Invalid timestamp: Must be in format yyyyMMddHHmmss.");
        }

        MpesaBrokerRequestDTO.Transaction transaction = request.getRequest().getTransaction();

        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount: Must be greater than zero.");
        }
        if (transaction.getCommandID() == null || transaction.getCommandID().isEmpty()) {
            throw new IllegalArgumentException("Invalid commandID: Cannot be null or empty.");
        }
        if (transaction.getRecipient() <= 0) {
            throw new IllegalArgumentException("Invalid recipient: Must be a positive long value.");
        }
//        if (!DATE_PATTERN.matcher(transaction.getTransactionDate()).matches()) {
//            throw new IllegalArgumentException("Invalid transactionDate: Must be in format yyyy-MM-dd HH:mm:ssZ.");
//        }
        if (transaction.getAccountReference() == null || transaction.getAccountReference().isEmpty()) {
            throw new IllegalArgumentException("Invalid accountReference: Cannot be null or empty.");
        }
        if (transaction.getInitiator() == null || transaction.getInitiator().isEmpty()) {
            throw new IllegalArgumentException("Invalid initiator: Cannot be null or empty.");
        }
        if (transaction.getInitiatorPassword() == null || transaction.getInitiatorPassword().isEmpty()) {
            throw new IllegalArgumentException("Invalid initiatorPassword: Cannot be null or empty.");
        }
        if (transaction.getMpesaReceipt() == null || transaction.getMpesaReceipt().isEmpty()) {
            throw new IllegalArgumentException("Invalid mpesaReceipt: Cannot be null or empty.");
        }
        if (transaction.getOriginatorConversationID() == null || transaction.getOriginatorConversationID().isEmpty()) {
            throw new IllegalArgumentException("Invalid originatorConversationID: Cannot be null or empty.");
        }
        if (transaction.getConversationID() == null || transaction.getConversationID().isEmpty()) {
            throw new IllegalArgumentException("Invalid conversationID: Cannot be null or empty.");
        }
    }

    @Override
    public MpesaBrokerResponseDTO processB2CRequest(MpesaBrokerRequestDTO request, String token) throws JAXBException {
        logger.info("processB2CRequest method start");
        MpesaBrokerResponseDTO response = new MpesaBrokerResponseDTO();
        MpesaBrokerRequestDTO.Transaction transaction = request.getRequest().getTransaction();
        if (transaction.getAccountReference() != null) {
            try {
                List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(String.valueOf(transaction.getTransactionID()));
                if (!allByPgTransactionId.isEmpty()) {
                    throw new AirtelValidateTxValidator(ValidateMpesaConstant.TRANSACTION_ID + " CAN NOT BE DUPLICATE.", 400);
                }
                AirtelAppToCRMDTO req = new AirtelAppToCRMDTO();
                req.setAccountNo(transaction.getAccountReference());
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(req, token);
                if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                    throw new AirtelValidateTxValidator(ValidateMpesaConstant.ACCOUNT_REFERENCE + " CAN NOT BE DUPLICATE.", 400);
                }

                if (!customerByAccountNumber.getBody().isEmpty()) {
                    AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
                    CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                    Long orderId = generateId(customer.getCustId().longValue());
                    custPayDTOMessage.setOrderId(orderId);
                    custPayDTOMessage.setPgTransactionId(String.valueOf(transaction.getTransactionID()));
                    if (customer.getCustId() != null)
                        custPayDTOMessage.setCustId(customer.getCustId());
                    if (transaction.getAmount() != null)
                        custPayDTOMessage.setPayment(Double.parseDouble(String.valueOf(transaction.getAmount())));
                    custPayDTOMessage.setStatus(ValidateMpesaConstant.INITIATE);
                    custPayDTOMessage.setGatewayStatus(ValidateMpesaConstant.INITIATE);
                    custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setMerchantName(ValidateMpesaConstant.MPESA_FLOW);
                    custPayDTOMessage.setPaymentGatewayName(ValidateMpesaConstant.MPESA_FLOW);
                    custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setCustomerUsername(customer.getUsername());
                    custPayDTOMessage.setMvnoid(customer.getMvnoId());
                    custPayDTOMessage.setBuid(customer.getBuId());
                    custPayDTOMessage.setAccountNumber(transaction.getAccountReference());
                    custPayDTOMessage.setPayerMobileNumber(customer.getCustomerMsisdn());
                    CustomerPayment customerPayment = new CustomerPayment();
                    customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//                    customerPayment.setId(getLatestId());
                    customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
                    customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
                    customerPayment = customerPaymentRepository.save(customerPayment);
                    boolean isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
                    if (isRecordSaved) {
                        paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), String.valueOf(transaction.getTransactionID()), "SUCCESSFUL", null);
                        customerPayment.setStatus(ValidateMpesaConstant.SUCCESSFUL);
                        customerPaymentRepository.save(customerPayment);
                        custPayDTOMessage.setStatus(ValidateMpesaConstant.SUCCESSFUL);
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send SUCCESSFUL Request of Mpesa Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setConversationID(transaction.getConversationID());
                        response.setOriginatorConversationID(transaction.getOriginatorConversationID());
                        response.setResponseCode(String.valueOf(HttpStatus.OK));
                        response.setServiceStatus("Success");
//                        response.setServiceID();
                        response.setTransactionID(transaction.getTransactionID());
                        response.setResponseDesc("Transaction SuccessFull");
                    } else {
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send Failed Request of Mpesa Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setConversationID(transaction.getConversationID());
                        response.setOriginatorConversationID(transaction.getOriginatorConversationID());
                        response.setResponseCode(String.valueOf(500));
                        response.setServiceStatus("Fail");
//                        response.setServiceID();
                        response.setTransactionID(transaction.getTransactionID());
                        response.setResponseDesc("Transaction Failed");
                    }
                } else {
                    response.setServiceStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR));
                    response.setResponseDesc("ACCOUNT NUMBER DOESN'T EXIST");
                }
            } catch (AirtelValidateTxValidator e) {
                logger.error("Error While Sending Data of Mpesa to CMS Through Kafka. ", e.getMessage());
                response.setResponseCode(String.valueOf(e.statusCode));
                response.setResponseDesc(e.getMessage());
            }
            catch (Exception e) {
                logger.error("Error While Sending Data of Mpesa to CMS Through Kafka. ", e.getMessage());
                response.setResponseCode(String.valueOf(400));
                response.setResponseDesc("TRANSACTION FAILED");
            }
        }
        logger.info("processB2CRequest method end");
        return generateResponse(response);
//        return response;
    }

    public static Long generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return Long.parseLong(id);
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

    @Override
    public MpesaBrokerResponseDTO generateResponse(MpesaBrokerResponseDTO response) throws JAXBException {
        // Convert response object to XML
        JAXBContext context = JAXBContext.newInstance(MpesaBrokerResponseDTO.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return response;
    }

    @Override
    public MpesaC2BValidateResponseDTO validateC2BRequest(MpesaC2BRequestDTO requestDTO, HttpServletRequest request) {
        String token = jwtUtil.generateJwtToken(selcomReverseFlowMvnoId.longValue());
        MpesaC2BValidateResponseDTO responseDTO = new MpesaC2BValidateResponseDTO();
        CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
        Integer planId = null;
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        if (Objects.isNull(requestDTO)) {
            responseDTO.setResultCode("C2B00016");
            responseDTO.setResultDesc("Other Error");
            return responseDTO;
        }
        if (requestDTO.getTransAmount() == null || requestDTO.getTransAmount().compareTo(BigDecimal.ZERO) <= 0 ) {
            responseDTO.setResultCode("C2B00013");
            responseDTO.setResultDesc("Invalid Amount");
            return responseDTO;
        }
        if (requestDTO.getBusinessShortCode() == null) {
            responseDTO.setResultCode("C2B00015");
            responseDTO.setResultDesc("Invalid Short code");
            return responseDTO;
        }
        AirtelAppToCRMDTO dto = new AirtelAppToCRMDTO();
        dto.setAccountNo(requestDTO.getBillRefNumber());
        dto.setMvnoId(selcomReverseFlowMvnoId);
        ResponseEntity<List<AirtelAppToCRMDTO>> customersByAccNumber = cmsClient.getCustDetailsByAcctNum(dto, token);
            if(customersByAccNumber.getBody() == null || customersByAccNumber.getBody().isEmpty()) {
                responseDTO.setResultCode("C2B00012");
                responseDTO.setResultDesc("Invalid Account Number");
                return responseDTO;
            }
            AirtelAppToCRMDTO customer = customersByAccNumber.getBody().get(0);
            if(customersByAccNumber.getBody() == null || customer.getStatus().equalsIgnoreCase("Terminate")){
                responseDTO.setResultCode("C2B00016");
                responseDTO.setResultDesc("Other Error");
                return responseDTO;
            }
            if(customer.getCustId() != null) {
                planId = cmsClient.getplanIdByCustId(customer.getCustId(), token);
                logger.info("fetching wallet ammount from revenue by customerId : {},", customer.getCustId());
                walletAmount = revenueClient.getWalletBalanceByCustId(customer.getCustId(), token);
                custPayDTOMessage.setWalletAmount(walletAmount);
            }
            if (planId != null) {
                logger.info("fetching plan price from CMS by planId : {},", planId);
                planPrice = cmsClient.getplanPriceByPlanId(planId, token);
                custPayDTOMessage.setPlanId(planId);
                custPayDTOMessage.setPlanPrice(planPrice);
            }
        if(isUniqueTransId(requestDTO.getTransID())){
            custPayDTOMessage.setPgTransactionId(String.valueOf(requestDTO.getTransID()));
        }else {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"Transaction ID must be unique",null);
        }
        Long orderId = generateId(customer.getCustId().longValue());
        custPayDTOMessage.setOrderId(orderId);
        //custPayDTOMessage.setPgTransactionId(String.valueOf(requestDTO.getTransID()));
        if (customer.getCustId() != null)
            custPayDTOMessage.setCustId(customer.getCustId());
        if (requestDTO.getTransAmount() != null)
            custPayDTOMessage.setPayment(Double.parseDouble(String.valueOf(requestDTO.getTransAmount())));
        custPayDTOMessage.setStatus(ValidateMpesaConstant.INITIATE);
        custPayDTOMessage.setGatewayStatus(ValidateMpesaConstant.INITIATE);
        custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
        custPayDTOMessage.setMerchantName(ValidateMpesaConstant.MPESA_FLOW);
        custPayDTOMessage.setPaymentGatewayName(ValidateMpesaConstant.MPESA_FLOW);
        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
        custPayDTOMessage.setCustomerUsername(customer.getUsername());
        custPayDTOMessage.setMvnoid(customer.getMvnoId());
        custPayDTOMessage.setBuid(customer.getBuId());
        custPayDTOMessage.setAccountNumber(requestDTO.getBillRefNumber());
        custPayDTOMessage.setPayerMobileNumber(customer.getCustomerMsisdn());
        CustomerPayment customerPayment = new CustomerPayment();
        customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//        customerPayment.setId(getLatestId());
        customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
        customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
        customerPayment = customerPaymentRepository.save(customerPayment);
        boolean isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
//        if (isRecordSaved) {
//            paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), String.valueOf(requestDTO.getTransID()), "INITIATE", null);
//            customerPayment.setStatus(ValidateMpesaConstant.SUCCESSFUL);
//            customerPaymentRepository.save(customerPayment);
//            custPayDTOMessage.setStatus(ValidateMpesaConstant.SUCCESSFUL);
//            custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
//            ApplicationLogger.logger.info("Send SUCCESSFUL Request of Mpesa Data to CMS for referenceId: " + customerPayment.getOrderId());
//            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
//        }
        responseDTO.setResultCode("0");
        responseDTO.setResultDesc("Accepted");
        responseDTO.setOrderId(customerPayment.getOrderId().toString());
        return responseDTO;
    }

    public String getAccessToken(MpesaDTO dto, Integer mvnoId, CustomerPayment customerPayment) {
        String accessToken = null;
        try {
            HttpPost httpPost = null;
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(dto.getRequestUrl() + URLConstants.MpesaUrlConstants.GENERATE_ACCESS_TOKEN);

            // Prepare Basic Auth Header (Base64 encode referenceId:apiKey)
            String authHeader = Base64.getEncoder().encodeToString((dto.getConsumerKey() + ":" + dto.getConsumerSecret()).getBytes());

            // Set headers
            request.setHeader("Authorization", "Basic " + authHeader);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "*/*");
            request.setHeader("Accept-Encoding", "gzip, deflate, br");
            // Execute the request

            CloseableHttpResponse response = client.execute(request);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            ApplicationLogger.logger.info("Request sent for Token genaration for user: "+dto.getConsumerKey());
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity, "UTF-8");
                System.out.println("getAccessToken Response from server : "+responseBody);
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String errorMessage = null;
                if (response != null) {
                    Integer response_code = response.getStatusLine().getStatusCode();
                    if (response_code != 200) {
                        errorMessage = rootNode.path("message").asText();
                        //                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Error while Generate Token In Mpesa Payment: " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSaveGetApiAudits(dto.getRequestUrl() + URLConstants.MpesaUrlConstants.GENERATE_ACCESS_TOKEN,    null,response, request,   responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId  ,customerPayment.getOrderId().toString());
                    }
                    if (response_code >= 400) {
                        //                    ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for token generation : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSaveGetApiAudits(dto.getRequestUrl() + URLConstants.MpesaUrlConstants.GENERATE_ACCESS_TOKEN,    null,response, request,   responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId  ,customerPayment.getOrderId().toString());
                    } else {
                        accessToken = rootNode.path("access_token").asText();
                        errorMessage = rootNode.path("message").asText();
                        //                    ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Token Generate Successfully for user : " + dto.getConsumerKey()  + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                        apiAuditsService.extractDataAndSaveGetApiAudits(dto.getRequestUrl() + URLConstants.MpesaUrlConstants.GENERATE_ACCESS_TOKEN,    null,response, request,   responseTime, errorMessage, requestInitiationTime, responseBody,  customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, mvnoId  ,customerPayment.getOrderId().toString());
                    }
                }
            }else throw new RuntimeException("Access Token Response from server is null");
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("Error While Generating Token ", e.getMessage());

        }
        return accessToken;
    }
    @Override
    public MpesaC2BValidateResponseDTO handleC2BConfirmation(MpesaC2BRequestDTO requestDTO, String token, HttpServletRequest request) {
        MpesaC2BValidateResponseDTO responseDTO = new MpesaC2BValidateResponseDTO();
        try {
            logger.info("processing Mpesa c2b confirmation request");
            if(Objects.nonNull(requestDTO)) {
                if(requestDTO.getTransID() != null) {
                    List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(requestDTO.getTransID());
                    if(allByPgTransactionId != null && !allByPgTransactionId.isEmpty()) {
                        CustomerPayment customerPayment = allByPgTransactionId.get(0);

                        paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(), customerPayment.getPgTransactionId(), "SUCCESSFUL",null);
                        logger.info("customer payment status updated successfully for trxnId: " + customerPayment.getPgTransactionId());
                        responseDTO.setResultCode("0");
                        responseDTO.setResultDesc("Success");
                        responseDTO.setOrderId(customerPayment.getOrderId().toString());
                    }else {
                        responseDTO.setResultCode("C2B00016");
                        responseDTO.setResultDesc("Invalid TransId");
                    }
                }else {
                    responseDTO.setResultCode("C2B00016");
                    responseDTO.setResultDesc("Other Error");
                }
            } else {
                responseDTO.setResultCode("C2B00016");
                responseDTO.setResultDesc("Required parameter is missing");
            }
            return responseDTO;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error while handlew MPESA C2B Confirmation request", e);
        }
        return responseDTO;
    }
    @Override
    public GenericDataDTO initiateB2CMpesaPayment(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request){
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        GenericDataDTO dataDTO = new GenericDataDTO();
        MpesaDTO mpesaDTO= new MpesaDTO();
        String access_token=null;
        try {
            walletAmount=revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(),request.getHeader("Authorization"));
            planPrice = Double.valueOf(customerPaymentDTO.getAmount());
            if (customerPaymentDTO.getPlanId() != null) {
                log.info("fetching wallet ammount from revenue by customerId : {},", customerPaymentDTO.getCustomerId());
                log.info("fetching plan price from CMS by planId : {},", customerPaymentDTO.getPlanId());
                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), request.getHeader("Authorization"));
            }
            customerPaymentDTO.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MPESA);
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            if (!customerPaymentDTO.getAmount().contains(".")) {
                customerPaymentDTO.setAmount(customerPaymentDTO.getAmount() + ".00");
            }
            mpesaDTO= fetchGatewayParameters(customerPaymentDTO.getMerchantName(),customerPaymentDTO.getMvnoId());
            CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            customerPaymentDTO.setOrderId(customerPayment.getOrderId().toString());
            access_token=getAccessToken(mpesaDTO,customerPaymentDTO.getMvnoId(),customerPayment);
            dataDTO = generateB2CResponse(customerPaymentDTO, mpesaDTO,access_token);
        }catch (Exception e){
            dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
            dataDTO.setResponseMessage("Exception while performing payment for B2C request.");
            log.error("error while validating b2c request; Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return dataDTO;
    }


    public MpesaDTO fetchGatewayParameters(String paymentGatewayName, Integer mvnoId) {
        try {
            HashMap<String, String> params = paymentConfigService.getPaymentGatewayParameter(paymentGatewayName, mvnoId);
            String requestUrl = params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_REQUEST_URL);
            String commandId = params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_COMMAND_ID);
            String resultUrl=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_RESULT_URL);
            String queueTimeOutUrl=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_QUEUE_TIMEOUT_URL);
            String partyA=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_PARTY_A);
            String consumerKey=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_CONSUMER_KEY);
            String consumerSecret=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_CONSUMER_SECRET);
            String scheduleTime=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_SCHEDULE_TIME);
            String redirectTimeInSeconds=params.get(PaymentGatewayConfigurationConstant.MPESA.REDIRECT_TIME_IN_SECONDS);
            String certificatePath=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_CERTIFICATE_PATH);
            String passkey=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_PASSKEY);
            String c2BCallbackUrl=params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_C2B_CALLBACK_URL);
            String transactionType=params.get(PaymentGatewayConfigurationConstant.MPESA.EXPRESS_SIMULATE_TRANSACTION_TYPE);
            String trxCode=params.get(PaymentGatewayConfigurationConstant.MPESA.QR_CODE_TRANSACTION_TYPE);
            String partyB = params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_EXPRESS_PARTY_B);
            String initiatorName = params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_INITIATOR_NAME);
            String initiatorPassword = params.get(PaymentGatewayConfigurationConstant.MPESA.MPESA_INITIATOR_PASSWORD);
            return new MpesaDTO(requestUrl,resultUrl,queueTimeOutUrl,partyA,commandId,consumerKey,consumerSecret,scheduleTime,redirectTimeInSeconds,certificatePath,c2BCallbackUrl,passkey,transactionType,trxCode,partyB,initiatorName,initiatorPassword);
        } catch (Exception e) {
            log.error("error while fetching gateway parameters; Message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
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
            if (paymentDTO.getCustomerUserName() == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Customer username cannot be null", null);
            }
        }
    }

    public GenericDataDTO generateB2CResponse(CustomerPaymentDTO customerPaymentDTO,MpesaDTO mpesaDTO,String access_token){
        GenericDataDTO dataDTO = new GenericDataDTO();
        Map<String, String> data = new LinkedHashMap<>();
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        String errorMessage = null;
        Long responseTime = null;
        try {
            String originatorConversationID = customerPaymentDTO.getOrderId();
            String encrypted = encryptInitiatorPassword(mpesaDTO.getInitiatorPassword(), mpesaDTO.getCertificatePath());
            data.put("OriginatorConversationID",originatorConversationID);
            data.put("InitiatorName",mpesaDTO.getInitiatorName());
            data.put("SecurityCredential",encrypted);
            data.put("CommandID",mpesaDTO.getCommandId());
            data.put("Amount",customerPaymentDTO.getAmount());
            data.put("PartyA",mpesaDTO.getPartyA());
            data.put("PartyB",customerPaymentDTO.getMobileNumber());
            data.put("Remarks",ValidateMpesaConstant.REMARK);
            data.put("QueueTimeOutURL",mpesaDTO.getQueueTimeOutUrl());
            data.put("ResultURL",mpesaDTO.getResultUrl());
            String jsonRequestPayload = objectMapper.writeValueAsString(data);

            LocalDateTime requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = new HttpPost(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.B2C_PAYMENT_REQUEST);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + access_token);

            StringEntity stringEntity = new StringEntity(jsonRequestPayload);
            httpPost.setEntity(stringEntity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            System.out.println("generateB2CResponse responseString : "+responseString);
            if (!responseString.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(responseString);
                String orderId = customerPaymentDTO.getOrderId();
                if (response != null) {
                    Integer response_code = rootNode.path("ResponseCode").asInt();
                    String error_code = rootNode.path("errorCode").asText();
                    String transection_status = rootNode.path("ResponseDescription").asText();
                     if (error_code.equals("400.002.02") || response_code>=001){
                         errorMessage = rootNode.path("errorMessage").asText();
                         dataDTO.setData(rootNode);
                        dataDTO.setResponseCode(org.apache.http.HttpStatus.SC_BAD_REQUEST);
                         dataDTO.setResponseMessage(errorMessage);
                         ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Fetching Order status of B2C Payment  : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                         apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.B2C_PAYMENT_REQUEST, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString,  customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null, customerPaymentDTO.getMvnoId(), URLConstants.MpesaUrlConstants.MPESA, customerPaymentDTO.getOrderId().toString());
                         return dataDTO;
                     }
                    if (response_code==0 && error_code=="") {
                        if (transection_status.equalsIgnoreCase("Accept the service request successfully.")) {
                            transection_status = "Successful";
                        }
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setData(rootNode);
                        dataDTO.setResponseCode(org.apache.http.HttpStatus.SC_OK);
                        dataDTO.setResponseMessage("success");
                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.B2C_PAYMENT_REQUEST, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null, customerPaymentDTO.getMvnoId(), URLConstants.MpesaUrlConstants.MPESA, customerPaymentDTO.getOrderId().toString());
                    }else {
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setResponseCode(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);
                        dataDTO.setResponseMessage("error");
                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.B2C_PAYMENT_REQUEST, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPaymentDTO.getCustomerUserName() != null ? customerPaymentDTO.getCustomerUserName() : null, customerPaymentDTO.getMvnoId(), URLConstants.MpesaUrlConstants.MPESA, customerPaymentDTO.getOrderId().toString());
                    }
                }
            }else throw new RuntimeException("B2C Response String is empty");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dataDTO;
    }

    public String encryptInitiatorPassword(String initiatorPassword, String certFilePath) throws Exception {
        // Load the X.509 certificate
//        certFilePath="C:\\Certificate\\SandboxCertificate.cer";
        FileInputStream fis = new FileInputStream(certFilePath);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fis);

        // Extract the public key
        PublicKey publicKey = certificate.getPublicKey();

        // Encrypt using RSA/ECB/PKCS1Padding
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(initiatorPassword.getBytes(StandardCharsets.UTF_8));

        // Encode encrypted password to Base64
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public GenericDataDTO checkTransactionStatusResponse(TransactionStatusRequestDTO transactionStatusRequestDTO){
        GenericDataDTO dataDTO = new GenericDataDTO();
        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        String errorMessage = null;
        Long responseTime = null;

        MpesaDTO mpesaDTO= new MpesaDTO();
        String access_token=null;
        try {
            String transactionId=transactionStatusRequestDTO.getTransactionId();
            List<CustomerPayment> customer = customerPaymentRepository.findAllByPgTransactionId(transactionId);
            if(Objects.isNull(customer) ||customer.isEmpty()  ) throw new RuntimeException("Invalid transaction Id");
            CustomerPayment customerPayment = customer.get(0);
            mpesaDTO= fetchGatewayParameters(customerPayment.getMerchantName(),customerPayment.getMvnoid());
            access_token=getAccessToken(mpesaDTO,customerPayment.getMvnoid(),customerPayment);
            String encrypted = encryptInitiatorPassword(mpesaDTO.getInitiatorPassword(), mpesaDTO.getCertificatePath());
            Integer identifierType= transactionStatusRequestDTO.getIdentifierType();
            if (identifierType != 1 && identifierType != 2 && identifierType != 4) {
                throw new RuntimeException("Bad Request: Identifier Type is " + identifierType);
            }
            data.put("Initiator",mpesaDTO.getInitiatorName());
            data.put("SecurityCredential",encrypted);
            data.put("CommandID",ValidateMpesaConstant.STATUS_COMMAND_ID);
            data.put("TransactionID",transactionId);
            data.put("PartyA",Integer.valueOf(mpesaDTO.getPartyA()));
            data.put("IdentifierType",identifierType);
            data.put("ResultURL",mpesaDTO.getResultUrl());
            data.put("QueueTimeOutURL",mpesaDTO.getQueueTimeOutUrl());
            data.put("Remarks",ValidateMpesaConstant.REMARK);
            String jsonRequestPayload = objectMapper.writeValueAsString(data);
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = new HttpPost(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + access_token);

            StringEntity stringEntity = new StringEntity(jsonRequestPayload);
            httpPost.setEntity(stringEntity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            if (!responseString.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(responseString);
                String orderId = customerPayment.getOrderId().toString();
                if (response != null) {
                    Integer response_code = rootNode.path("ResponseCode").asInt();
                    String error_code = rootNode.path("ResponseCode").asText();
                    String transection_status = rootNode.path("ResponseDescription").asText();
                    if (response_code != 0) {
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setData(errorMessage);
                        dataDTO.setResponseCode(response_code);
                        dataDTO.setResponseMessage("error");
                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, customerPayment.getMvnoid(), URLConstants.MpesaUrlConstants.MPESA, customerPayment.getOrderId().toString());
                    }
                    if (response_code >= 1) {
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setData(errorMessage);
                        dataDTO.setResponseCode(response_code);
                        dataDTO.setResponseMessage("error");
                        ApplicationLogger.logger.error(LogConstants.REQUEST_FROM + "gui" + LogConstants.REQUEST_FOR + "Invalid Data was sent in request for Fetching Transaction Status  : " + LogConstants.REQUEST_BY + MDC.get("userName") + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_ERROR + errorMessage + LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, customerPayment.getMvnoid(), URLConstants.MpesaUrlConstants.MPESA, customerPayment.getOrderId().toString());
                    } else {
                        if (transection_status.equalsIgnoreCase("Accept the service request successfully.")) {
                            transection_status = "Successful";
                        }
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setData(rootNode);
                        dataDTO.setResponseCode(org.apache.http.HttpStatus.SC_OK);
                        dataDTO.setResponseMessage("success");
                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null, customerPayment.getMvnoid(), URLConstants.MpesaUrlConstants.MPESA, customerPayment.getOrderId().toString());
                    }
                }
            }else throw new RuntimeException("check Transaction Status Response String is empty");


        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dataDTO;
    }

    @Override
    public GenericDataDTO initiateC2BMpesaExpressSimulate(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request) {
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        String token = request.getHeader("Authorization");
        String accessToken = null; // accessToken for safaricom request
        CustomerPayment customerPayment = new CustomerPayment();
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            if (customerPaymentDTO.getCustomerId() != null) {
                log.info("fetching wallet ammount from revenue by customerId : {},", customerPaymentDTO.getCustomerId());
                walletAmount = revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(), token);
            }
            if (customerPaymentDTO.getPlanId() != null) {
                log.info("fetching plan price from CMS by planId : {},", customerPaymentDTO.getPlanId());
                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), token);
            }
            customerPaymentDTO.setPlanPrice(planPrice);
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setMerchantName(URLConstants.MpesaUrlConstants.MPESA);
            MpesaDTO mpesaDTO = fetchGatewayParameters(customerPaymentDTO.getMerchantName(), customerPaymentDTO.getMvnoId());
            customerPaymentDTO.setMerchantName(URLConstants.MpesaUrlConstants.MPESA_EXPRESS_SIMULATE);
            if (mpesaDTO == null) {
                throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"Payment Gateway configs not found for this mvno",null);
            }
            Long orderId = generateUniqueId(customerPaymentDTO.getCustomerId().longValue());

            customerPaymentDTO.setOrderId(orderId.toString());
            customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            customerPaymentDTO.setOrderId(customerPayment.getOrderId().toString());
            accessToken = getAccessToken(mpesaDTO, customerPaymentDTO.getMvnoId(), customerPayment);
            dataDTO = processRequestForMpesaExpressSimulate(mpesaDTO, customerPayment, accessToken, request);
            schedulePaymentStatusConfirm(customerPayment,accessToken,mpesaDTO);
        } catch (FeignException e) {
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
        }
        return dataDTO;
    }

    private GenericDataDTO processRequestForMpesaExpressSimulate(MpesaDTO mpesaDTO, CustomerPayment customerPayment, String accessToken, HttpServletRequest request) throws Exception {
        Map<String,Object> requestBody = new HashMap<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        GenericDataDTO dataDTO =  new GenericDataDTO();
        try {

            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(mpesaDTO.getPartyB(), mpesaDTO.getPassKey(),timeStamp);
            requestBody.put("BusinessShortCode",mpesaDTO.getPartyB());
            requestBody.put("Password",password);
            requestBody.put("Timestamp",timeStamp);
            requestBody.put("TransactionType",mpesaDTO.getTransactionType());
            requestBody.put("Amount",customerPayment.getPayment());
            requestBody.put("PartyA",customerPayment.getPayerMobileNumber());
            requestBody.put("PartyB",mpesaDTO.getPartyB());
            requestBody.put("PhoneNumber",customerPayment.getPayerMobileNumber());
            requestBody.put("CallBackURL",mpesaDTO.getExpressSimulateCallbackUrl());
            requestBody.put("AccountReference",customerPayment.getAccountNumber());
            requestBody.put("TransactionDesc",ValidateMpesaConstant.TRANSACTION_DESC);
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(requestBody));
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = new HttpPost(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.EXPRESS_SIMULATE_PROCESS_REQUEST);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setEntity(entity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("response : " + responseString);
            Map<String, Object> stringObjectMap = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {
            });
            String errorMessage = null;
            Integer responseCode = APIConstants.EXPECTATION_FAILED;
            if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                if (stringObjectMap != null) {
                    if(stringObjectMap.get("ResponseCode") != null && stringObjectMap.get("ResponseCode").toString().equals("0")){
                        customerPayment.setCheckoutRequestId(stringObjectMap.get("CheckoutRequestID").toString());
                        paymentIntegrationService.updateStatusAndSendToCMS(customerPayment.getOrderId().toString(),customerPayment.getPgTransactionId(),customerPayment.getStatus(),null);
                        apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), request, httpResponse, httpPost, null, responseTime, null, requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,customerPayment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA,customerPayment.getOrderId().toString());
                        dataDTO.setData(stringObjectMap);
                        dataDTO.setResponseMessage("Success");
                        dataDTO.setResponseCode(APIConstants.SUCCESS);
                    }else{
                        errorMessage = stringObjectMap.get("errorMessage").toString();
                        dataDTO.setResponseMessage(errorMessage);
                        dataDTO.setResponseCode(Integer.parseInt(stringObjectMap.get("errorCode").toString()));
                        dataDTO.setData(stringObjectMap);
                        apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), request, httpResponse, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,customerPayment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA,customerPayment.getOrderId().toString());

                    }
                }else {
                    dataDTO.setResponseMessage("Error");
                    dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                }
            }else {
                logger.error("Error while parsing response");
                errorMessage = httpResponse.getStatusLine().getReasonPhrase();
                responseCode = httpResponse.getStatusLine().getStatusCode();
                dataDTO.setResponseMessage(errorMessage);
                dataDTO.setResponseCode(responseCode);
                dataDTO.setData(stringObjectMap);
                apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), request, httpResponse, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,customerPayment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA,customerPayment.getOrderId().toString());
            }
            return dataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseMessage(e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            return dataDTO;
        }
    }


    public static Long generateUniqueId(Long customerId) {
        long timeComponent = System.currentTimeMillis() % 10_000_000L; // 7 digits
        int random = new Random().nextInt(90) + 10; // 2 digits
        long customerComponent = customerId % 1000; // 3 digits
        // customer (3) + time (7) + random (2)
        return customerComponent * 10_000_000L * 100 + timeComponent * 100 + random;
    }

    public static String generatePassword(String shortCode, String passKey,String timeStamp) {
//        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String dataToEncode = shortCode + passKey + timeStamp;
        String encodedPassword = Base64.getEncoder().encodeToString(dataToEncode.getBytes());

        return encodedPassword;
    }

    public void schedulePaymentStatusConfirm(CustomerPayment customerPayment, String accessToken, MpesaDTO mpesaDTO) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Call orderStatus API
            try {
                veryTransaction(customerPayment,accessToken,mpesaDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Long.valueOf(mpesaDTO.getScheduleTime()), TimeUnit.MINUTES);
    }

    public void veryTransaction(CustomerPayment customerPayment, String accessToken, MpesaDTO mpesaDTO) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            List<CustomerPayment> pendingPayments = customerPaymentRepository.findAllByStatusAndMerchantNameAndIsScheduled(URLConstants.MpesaUrlConstants.MPESA_EXPRESS_SIMULATE, false, "Initiate");
            pendingPayments.stream().filter(cP -> cP.getPgTransactionId() == null).forEach(cP ->{
                cP.setStatus("Failed");
                cP.setIsScheduled(true);
            });
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(mpesaDTO.getPartyB(), mpesaDTO.getPassKey(), timeStamp);
            for(CustomerPayment payment : pendingPayments) {
                    LocalDateTime requestInitiationTime = LocalDateTime.now();
                    if(payment.getPgTransactionId() != null){
                    HashMap<String,Object> requestBody = new HashMap<>();
                    requestBody.put("BusinessShortCode",mpesaDTO.getPartyB());
                    requestBody.put("Password",password);
                    requestBody.put("Timestamp",timeStamp);
                    requestBody.put("CheckoutRequestID",payment.getCheckoutRequestId());
                    HttpPost httpPost = new HttpPost(mpesaDTO.getRequestUrl()+ URLConstants.MpesaUrlConstants.EXPRESS_SIMULATE_CHECK_QUERY);
                    httpPost.setHeader("Authorization", "Bearer " + accessToken);
                    httpPost.setHeader("Content-Type", "application/json");
                    httpPost.setHeader("Accept", "application/json");
                    String payload = objectMapper.writeValueAsString(requestBody);
                    StringEntity entity = new StringEntity(payload);
                    httpPost.setEntity(entity);
                    CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity responseEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(responseEntity, "UTF-8");
                    HashMap<String, Object> responseMap = objectMapper.readValue(responseString, new TypeReference<HashMap<String, Object>>() {
                    });
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        Map<String, Object> body = (Map<String, Object>) responseMap.get("Body");
                        if(responseMap.get("ResponseCode").toString().equals("0")){
                            if (body != null) {
                                Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");

                                if (stkCallback != null) {


                                    String resultCode = stkCallback.get("ResultCode") != null
                                            ? stkCallback.get("ResultCode").toString()
                                            : null;

                                    String resultDesc = stkCallback.get("ResultDesc") != null
                                            ? stkCallback.get("ResultDesc").toString()
                                            : null;

                                    String mpesaReceiptNumber = null;
                                    Map<String, Object> callbackMetadata =
                                            (Map<String, Object>) stkCallback.get("CallbackMetadata");
                                    if (callbackMetadata != null) {
                                        List<Map<String, Object>> items =
                                                (List<Map<String, Object>>) callbackMetadata.get("Item");

                                        if (items != null) {
                                            for (Map<String, Object> item : items) {
                                                if ("MpesaReceiptNumber".equals(item.get("Name"))) {
                                                    mpesaReceiptNumber = item.get("Value").toString();
                                                    break;
                                                }
                                            }
                                        }
                                        boolean isFailed = false;

                                        switch (resultCode) {
                                            case "1037":
                                                payment.setStatus("Declined");
                                                isFailed = true;
                                                break;
                                            case "1032":
                                                payment.setStatus("Declined");
                                                isFailed = true;
                                                break;
                                            case "1019":
                                                payment.setStatus("Declined");
                                                isFailed = true;
                                                break;

                                            default:
                                                payment.setStatus("Successful");
                                                payment.setPgTransactionId(mpesaReceiptNumber);
                                                break;
                                        }
                                        paymentIntegrationService.updateStatusAndSendToCMS(
                                                payment.getOrderId().toString(),
                                                payment.getPgTransactionId(),
                                                payment.getStatus(),
                                                isFailed ? resultDesc : null
                                        );


                                    }
                                }
                            }
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), null, httpResponse, httpPost, null, responseTime,null , requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,payment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA_EXPRESS_SIMULATE,payment.getOrderId().toString());
                        }else {
                            payment.setStatus("Failed");
                            paymentIntegrationService.updateStatusAndSendToCMS(payment.getOrderId().toString(),payment.getPgTransactionId(),payment.getStatus(),null);
                            LocalDateTime requestCompletionTime = LocalDateTime.now();
                            Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                            apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), null, httpResponse, httpPost, null, responseTime,null , requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,payment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA_EXPRESS_SIMULATE,payment.getOrderId().toString());
                        }
                    }else {
                        LocalDateTime requestCompletionTime = LocalDateTime.now();
                        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
                        apiAuditsService.extractDataAndSavePostApiAudits(httpPost.getURI().toString(), null, httpResponse, httpPost, null, responseTime, httpResponse.getStatusLine().getReasonPhrase(), requestInitiationTime, responseString,customerPayment.getCustomerUsername() != null ? customerPayment.getCustomerUsername() : null,payment.getMvnoid(),URLConstants.MpesaUrlConstants.MPESA_EXPRESS_SIMULATE,payment.getOrderId().toString());
                    }

                }
            }
            paymentIntegrationService.updateAndsendFailedPaymentsToCMS(pendingPayments);

        } catch (Exception e) {
            logger.error("error while verifying and sent status to CMS for payment", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public MpesaQrResponseDTO initiateQRCodePayment(CustomerPaymentDTO customerPaymentDTO, HttpServletRequest request){
        Double walletAmount = 0.0;
        Double planPrice = 0.0;
        MpesaQrResponseDTO dataDTO = new MpesaQrResponseDTO();
        MpesaDTO mpesaDTO= new MpesaDTO();
        String access_token=null;
        try {
            walletAmount=revenueClient.getWalletBalanceByCustId(customerPaymentDTO.getCustomerId(),request.getHeader("Authorization"));
            planPrice = Double.valueOf(customerPaymentDTO.getAmount());
            if (customerPaymentDTO.getPlanId() != null) {
                log.info("fetching wallet ammount from revenue by customerId : {},", customerPaymentDTO.getCustomerId());
                log.info("fetching plan price from CMS by planId : {},", customerPaymentDTO.getPlanId());
                planPrice = cmsClient.getplanPriceByPlanId(customerPaymentDTO.getPlanId(), request.getHeader("Authorization"));
            }
            customerPaymentDTO.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MPESA);
            customerPaymentDTO.setWalletAmount(walletAmount);
            customerPaymentDTO.setPlanPrice(planPrice);
            if (!customerPaymentDTO.getAmount().contains(".")) {
                customerPaymentDTO.setAmount(customerPaymentDTO.getAmount() + ".00");
            }
            mpesaDTO= fetchGatewayParameters(customerPaymentDTO.getMerchantName(),customerPaymentDTO.getMvnoId());
            CustomerPayment customerPayment = integrationGenericMethods.sendAndSaveDataForPayment(customerPaymentDTO, "Initiate");
            customerPaymentDTO.setOrderId(customerPayment.getOrderId().toString());
            access_token=getAccessToken(mpesaDTO,customerPaymentDTO.getMvnoId(),customerPayment);
            dataDTO = generateDynamicQR(customerPaymentDTO, mpesaDTO,access_token);
        }catch (Exception e){
           e.printStackTrace();
           throw new RuntimeException(e);
        }
        return dataDTO;
    }

    public MpesaQrResponseDTO generateDynamicQR(CustomerPaymentDTO customerPaymentDTO,MpesaDTO mpesaDTO,String access_token){
        MpesaQrResponseDTO dataDTO= new MpesaQrResponseDTO();
        Map<String, Object> data = new LinkedHashMap<>();
        String errorMessage = null;
        Long responseTime = null;
        try {
            String transactionType= mpesaDTO.getTrxCode();
//            String transactionType= "BG";
            if (!transactionType.equals("BG") && !transactionType.equals("WA") && !transactionType.equals("PB") && !transactionType.equals("SM") && !transactionType.equals("SB")) {

                throw new RuntimeException("TrxCode Bad Request: Invalid Transaction Type: " + transactionType);
            }
            data.put("MerchantName",customerPaymentDTO.getMerchantName());
            data.put("RefNo",customerPaymentDTO.getCustomerUUID());
            data.put("Amount",Double.valueOf(customerPaymentDTO.getAmount()));
            data.put("TrxCode",transactionType);
            //CPI: Credit Party Identifier.
            if(transactionType.equals("PB")) { //Paybill or Business number.
                data.put("CPI", mpesaDTO.getPartyA());  //Paybill or Business number,
            }
            else if(transactionType.equals("SM")) { //Send Money(Mobile number)
                data.put("CPI", customerPaymentDTO.getMobileNumber());
            }
            else if(transactionType.equals("BG")) { //Pay Merchant (Buy Goods).
                data.put("CPI", mpesaDTO.getPartyA());
            }
            else if(transactionType.equals("WA")) { //Withdraw Cash at Agent Till
                data.put("CPI", customerPaymentDTO.getMobileNumber());  // Agent Till
            }
            else if(transactionType.equals("SB")) {  // Sent to Business. Business number CPI in MSISDN format.
                data.put("CPI", customerPaymentDTO.getMobileNumber());
            }
            data.put("Size",qrCodeSize);
            String jsonRequestPayload = objectMapper.writeValueAsString(data);
            LocalDateTime requestInitiationTime = LocalDateTime.now();
            HttpPost httpPost = new HttpPost(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.DYNAMIC_QR);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Authorization", "Bearer " + access_token);
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");

            StringEntity stringEntity = new StringEntity(jsonRequestPayload);
            httpPost.setEntity(stringEntity);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(httpPost);
            LocalDateTime requestCompletionTime = LocalDateTime.now();
            responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            if (!responseString.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(responseString);
                String orderId = null;
                if (response != null) {
                    Integer response_code = rootNode.path("ResponseCode").asInt();
                    String error_code = rootNode.path("ResponseCode").asText();
                    String transection_status = rootNode.path("ResponseDescription").asText();
                    if (response_code != 00) {
                        errorMessage = rootNode.path("ResponseDescription").asText();
//                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, null, customerPayment.getMvnoid(), URLConstants.MpesaUrlConstants.MPESA, customerPayment.getOrderId().toString());
                    }
                    else {
                        if (transection_status.equalsIgnoreCase("QR Code Successfully Generated.")) {
                            transection_status = "Successful";
                        }
                        errorMessage = rootNode.path("ResponseDescription").asText();
                        dataDTO.setResponseCode(rootNode.path("ResponseCode").asText());
                        dataDTO.setResponseDescription(rootNode.path("ResponseDescription").asText());
                        dataDTO.setQrCode(rootNode.path("QRCode").asText());
//                        apiAuditsService.extractDataAndSavePostApiAudits(mpesaDTO.getRequestUrl() + URLConstants.MpesaUrlConstants.TRANSACTION_STATUS, null, response, httpPost, null, responseTime, errorMessage, requestInitiationTime, responseString, null, customerPayment.getMvnoid(), URLConstants.MpesaUrlConstants.MPESA, customerPayment.getOrderId().toString());
                    }
                }
            }else throw new RuntimeException("Dynamic QR Response String is empty");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return dataDTO;
    }
    public Boolean isUniqueTransId(String transId){
        List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(transId);
        if(allByPgTransactionId != null && !allByPgTransactionId.isEmpty()){
            return false;
        }else {
            return true;
        }
    }
}
