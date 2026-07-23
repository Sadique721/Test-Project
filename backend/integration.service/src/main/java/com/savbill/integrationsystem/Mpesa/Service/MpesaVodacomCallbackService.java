package com.savbill.integrationsystem.Mpesa.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaVodacomBrokerRequestDTO;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaVodacomBrokerRequestDTO.Request;
import com.savbill.integrationsystem.Mpesa.RequestDTO.MpesaVodacomBrokerRequestDTO.Transaction;
import com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO.MpesaVodacomAckResponseDTO;
import com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO.MpesaVodacomAckResponseDTO.Response;
import com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO.MpesaVodacomCallbackResultDTO;
import com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO.MpesaVodacomCallbackResultDTO.CallbackTransaction;
import com.savbill.integrationsystem.Mpesa.MpesaVodacomResponseDTO.MpesaVodacomCallbackResultDTO.Result;
import com.savbill.integrationsystem.Mpesa.Util.JAXBHelper;
import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;
import com.savbill.integrationsystem.billgen.repository.CreditDocRepocitory;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.savbill.integrationsystem.Mpesa.Constants.ValidateMpesaConstant.*;
import static com.savbill.integrationsystem.Mpesa.Util.SpPasswordUtil.encrypt;
import static com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant.MPESA_VODACOM.*;

@Slf4j
@Service
public class MpesaVodacomCallbackService {

    private static final DateTimeFormatter CALLBACK_TS_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter SERVICE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CMSClient cmsClient;
    @Autowired
    private RevenueClient revenueClient;
    @Autowired
    private PaymentConfigService paymentConfigService;
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    @Autowired
    private CustomerPaymentService customerPaymentService;
    @Autowired
    private PaymentIntegrationService paymentIntegrationService;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CreditDocRepocitory creditDocRepocitory;
    @Autowired
    private ApiAuditsService  apiAuditsService;

    // @Autowired
    // private Executor c2bExecutor;

    /* using mvnoId to generate token*/
    @Value("${selcom.reverseFlow.mvnoId}")
    private Integer selcomReverseFlowMvnoId;

    private final RestTemplate restTemplate = new RestTemplate();

    public String handleIncomingXml(String xml, HttpServletRequest request, String token) {
        try {
            log.info("M-PESA XML received. rawXmlSize={}", xml != null ? xml.length() : 0);

            MpesaVodacomBrokerRequestDTO brokerRequest = JAXBHelper.unmarshal(xml, MpesaVodacomBrokerRequestDTO.class);

            Transaction transaction = brokerRequest.getRequest().getTransaction();
            log.info("Parsed request. receipt={}, conversationId={}, originatorConversationId={}, transactionId={}",
                    transaction.getMpesaReceipt(), transaction.getConversationID(),
                    transaction.getOriginatorConversationID(), transaction.getTransactionID());

            String ackXml = buildImmediateAckXml(brokerRequest);
            CompletableFuture.runAsync(() -> processTransaction(brokerRequest, request, token));
            log.info("Immediate ACK generated for receipt={}", transaction.getMpesaReceipt());
            return ackXml;
        } catch (Exception ex) {
            log.error("Failed to handle incoming XML", ex);
            throw new IllegalStateException("Invalid XML request", ex);
        }
    }

    private void validateRequestStructure(MpesaVodacomBrokerRequestDTO request) {
        if (request == null || request.getRequest() == null
                || request.getRequest().getServiceProvider() == null
                || request.getRequest().getTransaction() == null) {
            throw new IllegalStateException("Invalid M-PESA request structure");
        }
    }

    private void validateBaseFields(MpesaVodacomBrokerRequestDTO request) {
        MpesaVodacomBrokerRequestDTO.ServiceProvider sp = request.getRequest().getServiceProvider();
        Transaction tx = request.getRequest().getTransaction();

        if (sp.getSpId() <= 0) {
            throw new IllegalArgumentException("Invalid spId");
        }
        if (sp.getSpPassword() == null || sp.getSpPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid spPassword");
        }
        if (!String.valueOf(sp.getTimestamp()).matches("\\d{14}")) {
            throw new IllegalArgumentException("Invalid timestamp");
        }

        if (tx.getAmount() == null || tx.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (tx.getCommandID() == null || tx.getCommandID().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid commandID");
        }
        if (tx.getRecipient() <= 0) {
            throw new IllegalArgumentException("Invalid recipient");
        }
//        if (tx.getInitiator() == null || tx.getInitiator().trim().isEmpty()) {
//            throw new IllegalArgumentException("Invalid initiator");
//        }
//        if (tx.getInitiatorPassword() == null || tx.getInitiatorPassword().trim().isEmpty()) {
//            throw new IllegalArgumentException("Invalid initiatorPassword");
//        }
        if (tx.getMpesaReceipt() == null || tx.getMpesaReceipt().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid mpesaReceipt");
        }
        if (tx.getOriginatorConversationID() == null || tx.getOriginatorConversationID().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid originatorConversationID");
        }
        if (tx.getConversationID() == null || tx.getConversationID().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid conversationID");
        }
        if (tx.getAccountReference() == null || tx.getAccountReference().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid accountReference");
        }
        if (tx.getTransactionID() == null || tx.getTransactionID().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid transactionID");
        }
    }

    private String buildImmediateAckXml(MpesaVodacomBrokerRequestDTO request) throws Exception {
        Transaction tx = request.getRequest().getTransaction();

        Response responseBody = new Response();
        responseBody.setConversationID(tx.getConversationID());
        responseBody.setOriginatorConversationID(tx.getOriginatorConversationID());
        responseBody.setTransactionID(tx.getTransactionID());
        responseBody.setResponseCode(ACK_RESPONSE_CODE);
        responseBody.setResponseDesc(RECEIVED);
        responseBody.setServiceStatus(SUCCESS);

        MpesaVodacomAckResponseDTO response = new MpesaVodacomAckResponseDTO();
        response.setVersion(VERSION_CODE);
        response.setResponse(responseBody);

        return JAXBHelper.marshal(response);
    }

    private void processTransaction(MpesaVodacomBrokerRequestDTO brokerRequest, HttpServletRequest httpServletRequest, String token) {
        LocalDateTime requestInitiationTime = LocalDateTime.now();
        validateRequestStructure(brokerRequest);

        Request request = brokerRequest.getRequest();
        MpesaVodacomBrokerRequestDTO.ServiceProvider incomingSp = request.getServiceProvider();
        Transaction tx = request.getTransaction();

        String mpesaReceipt = tx.getMpesaReceipt();
        Map<String, String> gatewayParams = null;
        CustomerPayment customerPayment = null;
        String callbackUrl = null;

        try {
            log.info("Async processing started. mpesaReceipt={}", mpesaReceipt);

            // Keep the same validations as the existing code style
            validateBaseFields(brokerRequest);

            // Duplicate mPesa receipt check
            List<CustomerPayment> existing = customerPaymentRepository.findAllByPgTransactionId(mpesaReceipt);
            if (existing != null && !existing.isEmpty()) {
                customerPayment = existing.get(0);
                gatewayParams = loadGatewayParams(customerPayment, incomingSp.getSpId());
                callbackUrl = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_CALLBACK_URL);
                log.warn("Duplicate mpesaReceipt detected. mpesaReceipt={}, existingStatus={}",
                        mpesaReceipt, customerPayment.getStatus());

                Result duplicateCallback = buildCallbackFromExistingPayment(brokerRequest, customerPayment, gatewayParams);
                postCallback(callbackUrl, duplicateCallback, httpServletRequest, customerPayment);


                return;
            }

            customerPayment = prepareInitialCustomerPaymentAndSave(brokerRequest, token);
            gatewayParams = loadGatewayParams(customerPayment, incomingSp.getSpId());
            callbackUrl = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_CALLBACK_URL);
            log.info("Initial payment saved. mpesaReceipt={}, paymentId={}, orderId={}",
                    mpesaReceipt, customerPayment.getId(), customerPayment.getOrderId());

            String configuredSpIdValue = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_SP_ID);
            long configuredSpId = Long.parseLong(configuredSpIdValue);
            String configuredSpPasswordPlain = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_SP_PASSWORD);
            if (incomingSp.getSpId() != configuredSpId) {
                throw new IllegalArgumentException("Invalid SPID");
            }
            String expectedEncrypted = encrypt(incomingSp.getSpId(),
                    configuredSpPasswordPlain, incomingSp.getTimestamp());
            if (!expectedEncrypted.equals(incomingSp.getSpPassword())) {
                throw new IllegalArgumentException("Invalid SP password");
            }

            // Final success state
            markSuccessful(customerPayment, requestInitiationTime, httpServletRequest);
            Result successCallback = buildCallback(brokerRequest, COMPLETED,
                    TRANSACTION_SUCCESS_CODE, TRANSACTION_SUCCESS, gatewayParams);
            postCallback(callbackUrl, successCallback, httpServletRequest, customerPayment);
            log.info("Transaction processed successfully. mpesaReceipt={}", mpesaReceipt);

        } catch (Exception ex) {
            log.error("Async processing failed. mpesaReceipt={}", mpesaReceipt, ex);

            try {
                if (customerPayment == null) {
                    List<CustomerPayment> existing = customerPaymentRepository.findAllByPgTransactionId(mpesaReceipt);
                    if (existing != null && !existing.isEmpty()) {
                        customerPayment = existing.get(0);
                    }
                }

                if (customerPayment != null) {
                    markFailed(customerPayment, requestInitiationTime, httpServletRequest, ex.getMessage());
                    if (gatewayParams == null) {
                        gatewayParams = loadGatewayParams(customerPayment, incomingSp.getSpId());
                    }
                    if (callbackUrl == null) {
                        callbackUrl = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_CALLBACK_URL);
                    }
                    Result failureCallback = buildCallback(brokerRequest, TRANSACTION_FAILED,
                            TRANSACTION_FAILED_CODE, ex.getMessage(), gatewayParams);
                    postCallback(callbackUrl, failureCallback, httpServletRequest, customerPayment);
                }
            } catch (Exception inner) {
                log.error("Recovery failed. mpesaReceipt={}", mpesaReceipt, inner);
            }
        }
    }

    public static Long generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return Long.parseLong(id);
    }

    private CustomerPayment getInitialPayment(MpesaVodacomBrokerRequestDTO brokerRequest, String rawXml) {
        Transaction tx = brokerRequest.getRequest().getTransaction();

        CustPayDTOMessage message = new CustPayDTOMessage();
        //message.setOrderId(System.currentTimeMillis());
        message.setPgTransactionId(tx.getMpesaReceipt());
        message.setPayment(tx.getAmount());
        message.setStatus(INITIATE);
        message.setGatewayStatus(INITIATE);
        message.setPaymentDate(LocalDateTime.now().toString());
        String inputDate = tx.getTransactionDate();

        LocalDateTime transactionDate;

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toFormatter();
        transactionDate = LocalDateTime.parse(inputDate.trim(), formatter);

        message.setTransactionDate(
                transactionDate.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                )
        );
        message.setMerchantName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MPESA_VODACOM);
        message.setPaymentGatewayName(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MPESA_VODACOM);
        message.setAccountNumber(tx.getAccountReference());
        message.setCustomerUsername(tx.getInitiator());

        CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(message);
        log.info("Raw XML captured for audit only. receipt={}, size={}",
                tx.getMpesaReceipt(), rawXml != null ? rawXml.length() : 0);
        return customerPayment;
    }

    private void updateSavedPaymentWithCustomer(CustomerPayment payment, AirtelAppToCRMDTO customer, String token) {
        if (customer.getCustId() != null) {
            payment.setCustId(customer.getCustId());
            payment.setOrderId(generateOrderId(customer.getCustId()));
            log.info("fetching wallet amount from revenue by customerId : {},", customer.getCustId());
            payment.setWalletAmount(revenueClient.getWalletBalanceByCustId(customer.getCustId(), token));
//            payment.setPlanId(cmsClient.getplanIdByCustId(customer.getCustId(), token));
//            if (payment.getPlanId() != null) {
//                payment.setPlanPrice(cmsClient.getPlanPriceByCustId(customer.getCustId(), token));
//            }
        }
        if (customer.getUsername() != null) {
            payment.setCustomerUsername(customer.getUsername());
        }
        if (customer.getMvnoId() != null) {
            payment.setMvnoid(customer.getMvnoId());
        }
        if (customer.getBuId() != null) {
            payment.setBuid(customer.getBuId());
        }
        if (customer.getCustomerMsisdn() != null) {
            payment.setPayerMobileNumber(customer.getCustomerMsisdn());
        }
    }

    private void markSuccessful(CustomerPayment payment, LocalDateTime requestInitiationTime, HttpServletRequest request) {
        paymentIntegrationService.updateStatusAndSendToCMS(null, payment.getPgTransactionId(), TRANSACTION_SUCCESS, null);

        payment.setStatus(TRANSACTION_SUCCESS);
        payment.setGatewayStatus(TRANSACTION_SUCCESS);
        payment.setTransactionDate(LocalDateTime.now());
        customerPaymentRepository.save(payment);

        // addInCreditDoc(payment, request); // Add transaction in CreditDoc Table after successful transaction
        CustPayDTOMessage message = customerPaymentService.convertEntityToMessage(payment);
        message.setPaymentGatewayName(payment.getMerchantName());
        ApplicationLogger.logger.info("Send Successful Request of Mpesa Vodacom Data to CMS for referenceId: " + payment.getOrderId());
        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

        LocalDateTime requestCompletionTime = LocalDateTime.now();
        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
        apiAuditsService.extractDataAndSavePostApiAudits(
                request.getRequestURI(),
                request,
                null,
                (org.apache.http.client.methods.HttpPost) null, // IMPORTANT
                null,
                responseTime,
                null, // error message
                requestInitiationTime,
                null,
                null,
                null,
                payment.getCustomerUsername(),
                payment.getAccountNumber()
        );

        log.info("Payment marked Successful. orderId={}, receipt={}",
                payment.getOrderId(), payment.getPgTransactionId());
    }

    private void markFailed(CustomerPayment payment, LocalDateTime requestInitiationTime, HttpServletRequest request, String reason) {
        // paymentIntegrationService.updateStatusAndSendToCMS(null, payment.getPgTransactionId(), TRANSACTION_FAILED, reason);

        payment.setStatus(TRANSACTION_FAILED);
        payment.setGatewayStatus(TRANSACTION_FAILED);
        payment.setTransactionDate(LocalDateTime.now());
        payment.setFailureDescription(reason);
        customerPaymentRepository.save(payment);

//        CustPayDTOMessage message = customerPaymentService.convertEntityToMessage(payment);
//        message.setPaymentGatewayName(payment.getMerchantName());
//        ApplicationLogger.logger.info("Send Failed Request of Mpesa Vodacom Data to CMS for referenceId: " + payment.getOrderId());
//        kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

        LocalDateTime requestCompletionTime = LocalDateTime.now();
        Long responseTime = apiAuditsService.measureResponseTime(requestInitiationTime, requestCompletionTime);
        apiAuditsService.extractDataAndSavePostApiAudits(
                request.getRequestURI(),
                request,
                null,
                (org.apache.http.client.methods.HttpPost) null,
                null,
                responseTime,
                reason, // error message
                requestInitiationTime,
                null,
                null,
                null,
                payment.getCustomerUsername(),
                payment.getAccountNumber()
        );

        log.warn("Payment marked Failed. orderId={}, receipt={}, reason={}",
                payment.getOrderId(), payment.getPgTransactionId(), reason);
    }

    private AirtelAppToCRMDTO fetchCustomerByAccountReference(String accountReference, String token) {
        ResponseEntity<List<AirtelAppToCRMDTO>> response = cmsClient.getCustomerByOnlyAccountNumber(accountReference, token);
        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            return null;
        }
        if (response.getBody().size() > 1) {
            throw new IllegalArgumentException("Customer reference cannot be duplicate");
        }
        return response.getBody().get(0);
    }

    private void addInCreditDoc(CustomerPayment payment, HttpServletRequest request) {
        CreditDocMessage message = new CreditDocMessage();
        message.setId(payment.getOrderId().intValue());
        message.setAmount(payment.getPayment());
        message.setReferenceno(payment.getAccountNumber());
        message.setBuID(payment.getBuid().longValue());
//        message.setCreatedate(payment.getTransactionDate().toString());
//        message.setPaymentdate(payment.getPaymentDate().toString());
        message.setCreatedate(
                payment.getTransactionDate().format(SERVICE_DATE_FMT)
        );

        message.setPaymentdate(
                payment.getPaymentDate().format(SERVICE_DATE_FMT)
        );
        message.setUniquename(payment.getCustomerUsername());
        message.setMvnoId(payment.getMvnoid());
        message.setCustomer(payment.getCustId());
        message.setStatus(payment.getStatus());
        message.setPaydetails4(payment.getMerchantName());
        message.setReciptNo(payment.getPgTransactionId());
        message.setIsDelete(false);

        creditDocRepocitory.save(new CreditDocumentData(message));
    }

    private Result buildCallbackFromExistingPayment(MpesaVodacomBrokerRequestDTO brokerRequest,
                                                    CustomerPayment existingPayment,
                                                    Map<String, String> gatewayParams) {
        String resultType;
        String resultCode;
        String resultDesc;

        if (existingPayment.getStatus() != null && existingPayment.getStatus().equalsIgnoreCase(SUCCESSFUL)) {
            resultType = COMPLETED;
            resultCode = TRANSACTION_SUCCESS_CODE;
            resultDesc = TRANSACTION_SUCCESS;
        } else {
            resultType = TRANSACTION_FAILED;
            resultCode = TRANSACTION_FAILED_CODE;
            resultDesc = existingPayment.getFailureDescription() != null
                    ? existingPayment.getFailureDescription()
                    : "Duplicate transaction";
        }

        return buildCallback(brokerRequest, resultType, resultCode, resultDesc, gatewayParams);
    }

    private Result buildCallback(MpesaVodacomBrokerRequestDTO brokerRequest, String resultType, String resultCode,
                                 String resultDesc, Map<String, String> gatewayParams) {

        Transaction tx = brokerRequest.getRequest().getTransaction();
        LocalDateTime now = LocalDateTime.now();
        long timestamp = Long.parseLong(now.format(CALLBACK_TS_FMT));

        long callbackSpId = Long.parseLong(getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_SP_ID));
        String callbackSpPasswordPlain = getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_SP_PASSWORD);
        MpesaVodacomCallbackResultDTO.ServiceProvider callbackServiceProvider = new MpesaVodacomCallbackResultDTO.ServiceProvider();
        callbackServiceProvider.setSpId(callbackSpId);
        callbackServiceProvider.setSpPassword(encrypt(callbackSpId, callbackSpPasswordPlain, timestamp));
        callbackServiceProvider.setTimestamp(timestamp);

        CallbackTransaction callbackTransaction = new CallbackTransaction();
        callbackTransaction.setResultType(resultType);
        callbackTransaction.setResultCode(resultCode);
        callbackTransaction.setResultDesc(resultDesc);
        callbackTransaction.setServiceReceipt(tx.getMpesaReceipt());
        callbackTransaction.setServiceDate(now.format(SERVICE_DATE_FMT));
        callbackTransaction.setOriginatorConversationID(tx.getOriginatorConversationID());
        callbackTransaction.setConversationID(tx.getConversationID());
        callbackTransaction.setTransactionID(tx.getTransactionID());
        callbackTransaction.setInitiator(getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_CALLBACK_INITIATOR));
        callbackTransaction.setInitiatorPassword(encrypt(callbackSpId,
                getRequiredGatewayValue(gatewayParams, MPESA_VODACOM_CALLBACK_INITIATOR_PASSWORD), timestamp));

        Result result = new Result();
        result.setServiceProvider(callbackServiceProvider);
        result.setTransaction(callbackTransaction);

        log.info("Built callback payload. receipt={}, resultCode={}, resultDesc={}",
                tx.getMpesaReceipt(), resultCode, resultDesc);

        return result;
    }

    private CustomerPayment prepareInitialCustomerPaymentAndSave(MpesaVodacomBrokerRequestDTO brokerRequestDTO, String token) {
        Transaction transaction = brokerRequestDTO.getRequest().getTransaction();
        AirtelAppToCRMDTO customer = fetchCustomerByAccountReference(transaction.getAccountReference(), token);
        if (customer == null) {
            throw new IllegalArgumentException("Invalid account reference");
        }
        if (customer.getMvnoId() == null) {
            throw new IllegalArgumentException("MVNO ID not found for customer: " + transaction);
        }
        CustomerPayment customerPayment = getInitialPayment(brokerRequestDTO, token);
        // Update the saved payment with customer details
        updateSavedPaymentWithCustomer(customerPayment, customer, token);
        if (jwtUtil.getLoggedInUser() != null) {
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
        }
        CustPayDTOMessage message = customerPaymentService.convertEntityToMessage(customerPayment);
        boolean isRecordSaved = cmsClient.addCustomerPayment(message, token);
        return customerPaymentRepository.save(customerPayment);
    }

    private Map<String, String> loadGatewayParams(CustomerPayment c, Long spId) {
        HashMap<String, String> params = paymentConfigService.getPaymentGatewayParameter(c.getMerchantName(), selcomReverseFlowMvnoId);
        String configSpId = params.get(MPESA_VODACOM_SP_ID);
        if (configSpId != null && configSpId.trim().equals(String.valueOf(spId))) {
            return params;
        }
        throw new IllegalStateException("No M-PESA gateway configuration found for spId=" + spId);
    }

    private String getRequiredGatewayValue(Map<String, String> params, String key) {
        String value = params != null ? params.get(key) : null;
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing gateway parameter: " + key);
        }
        return value.trim();
    }

    private static Long generateOrderId(long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return Long.parseLong(id);
    }

    public void postCallback(String callbackUrl, MpesaVodacomCallbackResultDTO.Result result,
                             HttpServletRequest request,
                             CustomerPayment customerPayment) {

        LocalDateTime requestInitiationTime = LocalDateTime.now();

        CloseableHttpResponse response = null;
        HttpPost httpPost = null;

        try {

            String requestXml = JAXBHelper.marshal(result);

            log.info("Posting callback. receipt={}, payloadSize={}",
                    result.getTransaction().getServiceReceipt(),
                    requestXml != null ? requestXml.length() : 0);

            httpPost = new HttpPost(callbackUrl);

            httpPost.setHeader(
                    HttpHeaders.CONTENT_TYPE,
                    MediaType.APPLICATION_XML_VALUE
            );

            httpPost.setHeader(
                    HttpHeaders.ACCEPT,
                    MediaType.APPLICATION_XML_VALUE + "," + MediaType.TEXT_XML_VALUE
            );

            StringEntity stringEntity =
                    new StringEntity(requestXml, StandardCharsets.UTF_8);

            httpPost.setEntity(stringEntity);

            CloseableHttpClient httpClient = HttpClients.createDefault();

            response = httpClient.execute(httpPost);

            String ackXml = null;

            if (response.getEntity() != null) {
                ackXml = EntityUtils.toString(response.getEntity(),
                        StandardCharsets.UTF_8
                );
            }

            log.info("Callback ACK received. receipt={}, ackSize={}",
                    result.getTransaction().getServiceReceipt(),
                    ackXml != null ? ackXml.length() : 0);

            LocalDateTime requestCompletionTime = LocalDateTime.now();

            Long responseTime =
                    apiAuditsService.measureResponseTime(
                            requestInitiationTime,
                            requestCompletionTime
                    );

            apiAuditsService.extractDataAndSavePostApiAudits(
                    callbackUrl,
                    request,
                    response,
                    httpPost,
                    null,
                    responseTime,
                    null,
                    requestInitiationTime,
                    ackXml,
                    null,
                    null,
                    customerPayment.getCustomerUsername(),
                    customerPayment.getAccountNumber()
            );

            log.info("Callback audit save request completed. receipt={}",
                    result.getTransaction().getServiceReceipt());

            if (ackXml == null || ackXml.trim().isEmpty()) {

                log.warn("Empty callback ACK returned. receipt={}",
                        result.getTransaction().getServiceReceipt());

                return;
            }

            try {

                MpesaVodacomAckResponseDTO ack =
                        JAXBHelper.unmarshal(
                                ackXml,
                                MpesaVodacomAckResponseDTO.class
                        );

                log.info(
                        "Callback ACK parsed. receipt={}, responseCode={}, responseDesc={}, serviceStatus={}",
                        result.getTransaction().getServiceReceipt(),
                        ack.getResponse().getResponseCode(),
                        ack.getResponse().getResponseDesc(),
                        ack.getResponse().getServiceStatus()
                );

            } catch (Exception parseEx) {

                log.warn(
                        "Callback ACK received but could not be parsed as XML. receipt={}",
                        result.getTransaction().getServiceReceipt(),
                        parseEx
                );
            }

        } catch (Exception ex) {

            log.error("Failed while posting callback", ex);

            apiAuditsService.extractDataAndSavePostApiAudits(
                    callbackUrl,
                    request,
                    response,
                    httpPost == null ? new HttpPost(callbackUrl) : httpPost,
                    null,
                    apiAuditsService.measureResponseTime(
                            requestInitiationTime,
                            LocalDateTime.now()
                    ),
                    ex.getMessage(),
                    requestInitiationTime,
                    "NA",
                    null,
                    null,
                    customerPayment.getCustomerUsername(),
                    customerPayment.getAccountNumber()
            );

        } finally {

            try {
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error("Failed to close response", e);
            }
        }
    }
}