package com.savbill.integrationsystem.CRDB.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.CRDB.Constants.CRDBConstant;
import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBPaymentPostRequestDTO;
import com.savbill.integrationsystem.CRDB.RequestDTO.CRDBVerificationRequestDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBPaymentPostResponseDTO;
import com.savbill.integrationsystem.CRDB.ResponseDTO.CRDBVerificationResponseDTO;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.apiAudits.NMSServices.ApiAuditsService;
import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;
import com.savbill.integrationsystem.billgen.repository.CreditDocRepocitory;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CreditDocMessage;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.savbill.integrationsystem.CRDB.Constants.CRDBConstant.*;

@Slf4j
@Service
public class  CRDBBillsPaymentServiceImpl implements CRDBBillsPaymentService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CreditDocRepocitory creditDocRepocitory;

    @Autowired
    private com.savbill.integrationsystem.billgen.service.CreditdocService creditdocService;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private ApiAuditsService apiAuditsService;

    @Autowired
    private PaymentConfigService paymentConfigService;

    // @Value("${crdb.mvnoId}")
    @Value("${selcom.reverseFlow.mvnoId}")
    private Integer crdbMvnoId;

    private String getConfiguredToken() {
        try {
            HashMap<String, String> configs = loadCRDBConfigs();
            if (configs != null) {
                String token = configs.get("CRDB_TOKEN");
                if (token == null) {
                    token = configs.get("token");
                }
                if (token != null && !token.trim().isEmpty()) {
                    return token;
                }
            }
        } catch (Exception e) {
            log.warn("CRDB – Could not load DB configs for token check: {}", e.getMessage());
        }
        return null;
    }

    private HashMap<String, String> loadCRDBConfigs() {
        return paymentConfigService.getPaymentGatewayParameter(
                CRDB_PAYMENT_GATEWAY_NAME,
                crdbMvnoId);
    }

    @Override
    public CRDBVerificationResponseDTO verifyBillPayment(CRDBVerificationRequestDTO request, HttpServletRequest servletReq) {
        String paymentReference = request.getPaymentReference();
        String checksum = request.getChecksum();
        String payloadToken = request.getToken();

        log.info("CRDB verifyBillPayment – start. paymentReference={}", paymentReference);

        if (isBlank(paymentReference)) {
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_REFERENCE, "paymentReference is required");
        }

        // 1. Validate Token
        if (isBlank(payloadToken)) {
            log.warn("CRDB verifyBillPayment – Token is missing in payload");
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_TOKEN, STATUS_INVALID_TOKEN_DESC);
        }

        String configuredToken = getConfiguredToken();
        log.info("DEBUG: payloadToken='{}', configuredToken='{}', crdbMvnoId={}", payloadToken, configuredToken, crdbMvnoId);
        if (configuredToken == null || !payloadToken.equals(configuredToken)) {
            log.warn("CRDB verifyBillPayment – Token mismatch. payloadToken={}, configuredToken={}", payloadToken, configuredToken);
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_TOKEN, STATUS_INVALID_TOKEN_DESC);
        }

        // 2. Validate Checksum
        if (isBlank(checksum)) {
            log.warn("CRDB verifyBillPayment – Checksum is missing in payload");
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_CHECKSUM, STATUS_INVALID_CHECKSUM_DESC);
        }
        if (!validateChecksum(payloadToken, paymentReference, checksum)) {
            log.warn("CRDB verifyBillPayment – Checksum verification failed. incoming={}, paymentReference={}", checksum, paymentReference);
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_CHECKSUM, STATUS_INVALID_CHECKSUM_DESC);
        }

        // Load system token from configs for CMS API calls
        Long mvnoIdLong = crdbMvnoId != null ? crdbMvnoId.longValue() : 1L;
        String cmsToken = jwtUtil.generateJwtToken(mvnoIdLong);

        // Customer lookup by ONLY account number
        ResponseEntity<List<AirtelAppToCRMDTO>> customerResponse =
                cmsClient.getCustomerByOnlyAccountNumber(paymentReference, cmsToken);

        if (customerResponse.getBody() == null || customerResponse.getBody().isEmpty()) {
            log.warn("CRDB verifyBillPayment – no customer found for paymentRef={}", paymentReference);
            return CRDBVerificationResponseDTO.error(STATUS_INVALID_REFERENCE,
                    STATUS_INVALID_REFERENCE_DESC);
        }

        AirtelAppToCRMDTO customer = customerResponse.getBody().get(0);

        // Build and return verification response
        CRDBVerificationResponseDTO.Data data = getVerificationResponseData(request, customer);

        log.info("CRDB verifyBillPayment – completed successfully for ref={}", paymentReference);
        return CRDBVerificationResponseDTO.success(data);
    }


    @Override
    public CRDBPaymentPostResponseDTO postPaymentNotification(CRDBPaymentPostRequestDTO request, HttpServletRequest servletReq) {
        String paymentReference = request.getPaymentReference();
        String transactionRef    = request.getTransactionRef();
        String checksum          = request.getChecksum();
        String payloadToken      = request.getToken();

        log.info("CRDB postPaymentNotification – start. transactionRef={}, paymentReference={}",
                transactionRef, paymentReference);

        if (isBlank(paymentReference)) {
            return CRDBPaymentPostResponseDTO.error(STATUS_INVALID_REFERENCE, "paymentReference is required");
        }

        // 1. Validate Token
        if (isBlank(payloadToken)) {
            log.warn("CRDB postPaymentNotification – Token is missing in payload");
            return CRDBPaymentPostResponseDTO.error(STATUS_INVALID_TOKEN, STATUS_INVALID_TOKEN_DESC);
        }

        String configuredToken = getConfiguredToken();
        log.info("DEBUG: payloadToken='{}', configuredToken='{}', crdbMvnoId={}", payloadToken, configuredToken, crdbMvnoId);
        if (configuredToken == null || !payloadToken.equals(configuredToken)) {
            log.warn("CRDB postPaymentNotification – Token mismatch");
            return CRDBPaymentPostResponseDTO.error(STATUS_INVALID_TOKEN, STATUS_INVALID_TOKEN_DESC);
        }

        // 2. Validate Checksum
        if (isBlank(checksum)) {
            log.warn("CRDB postPaymentNotification – Checksum is missing in payload");
            return CRDBPaymentPostResponseDTO.error(STATUS_INVALID_CHECKSUM, STATUS_INVALID_CHECKSUM_DESC);
        }
        if (!validateChecksum(payloadToken, paymentReference, checksum)) {
            log.warn("CRDB postPaymentNotification – Checksum verification failed. incoming={}, paymentReference={}", checksum, paymentReference);
            return CRDBPaymentPostResponseDTO.error(STATUS_INVALID_CHECKSUM, STATUS_INVALID_CHECKSUM_DESC);
        }
        Long mvnoIdLong = crdbMvnoId != null ? crdbMvnoId.longValue() : 1L;
        String token = jwtUtil.generateJwtToken(mvnoIdLong);
        List<CustomerPayment> existing = customerPaymentRepository.findAllByPgTransactionId(transactionRef);
        if (existing != null && !existing.isEmpty()) {
            CustomerPayment duplicate = existing.get(0);
            log.warn("CRDB postPaymentNotification – duplicate transactionRef={}. Existing orderId={}. " + "Recording new attempt anyway.", transactionRef, duplicate.getOrderId());
            if ("approved".equalsIgnoreCase(duplicate.getStatus()) ||
                    "Successful".equalsIgnoreCase(duplicate.getStatus())) {
                return CRDBPaymentPostResponseDTO.error(STATUS_DUPLICATE, STATUS_DUPLICATE_DESC + ": " + duplicate.getOrderId());
            }
        }

        // Customer lookup by ONLY account number
        ResponseEntity<List<AirtelAppToCRMDTO>> customerResponse =
                cmsClient.getCustomerByOnlyAccountNumber(paymentReference, token);

        if (customerResponse.getBody() == null || customerResponse.getBody().isEmpty()) {
            log.warn("CRDB postPaymentNotification – no customer found for paymentRef={}", paymentReference);
            return CRDBPaymentPostResponseDTO.error(
                    STATUS_INVALID_REFERENCE,
                    STATUS_INVALID_REFERENCE_DESC);
        }

        AirtelAppToCRMDTO customer = customerResponse.getBody().get(0);

        Integer planId = null;
        Double planPrice = 0.0;
        Double walletAmount = 0.0;

        if (customer.getCustId() != null) {
            try {
                planId       = cmsClient.getplanIdByCustId(customer.getCustId(), token);
                walletAmount = revenueClient.getWalletBalanceByCustId(customer.getCustId(), token);
                log.info("CRDB postPaymentNotification – walletAmount={} for custId={}",
                        walletAmount, customer.getCustId());
            } catch (Exception e) {
                log.warn("CRDB postPaymentNotification – could not fetch wallet/plan for custId={}. Reason: {}",
                        customer.getCustId(), e.getMessage());
            }
        }

        if (planId != null) {
            try {
                planPrice = cmsClient.getplanPriceByPlanId(planId, token);
                log.info("CRDB postPaymentNotification – planPrice={} for planId={}", planPrice, planId);
            } catch (Exception e) {
                log.warn("CRDB postPaymentNotification – could not fetch plan price for planId={}. Reason: {}", planId, e.getMessage());
            }
        }
        Long custIdVal = customer.getCustId() != null ? customer.getCustId().longValue() : 0L;
        Long orderId = generateOrderId(custIdVal);

        CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
        custPayDTOMessage.setOrderId(orderId);
        custPayDTOMessage.setPgTransactionId(transactionRef);
        custPayDTOMessage.setCustId(customer.getCustId());
        custPayDTOMessage.setPayment(
                request.getAmount() != null ? request.getAmount() : 0.0
        );
        custPayDTOMessage.setWalletAmount(walletAmount);
        custPayDTOMessage.setPlanId(planId);
        custPayDTOMessage.setPlanPrice(planPrice);
        custPayDTOMessage.setStatus(SUCCESSFUL);
        custPayDTOMessage.setGatewayStatus(SUCCESSFUL);
        custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
        custPayDTOMessage.setMerchantName(CRDB_PAYMENT_GATEWAY_NAME);
        custPayDTOMessage.setPaymentGatewayName(CRDB_PAYMENT_GATEWAY_NAME);
        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
        custPayDTOMessage.setCustomerUsername(customer.getUsername());
        custPayDTOMessage.setMvnoid(customer.getMvnoId());
        custPayDTOMessage.setBuid(customer.getBuId());
        custPayDTOMessage.setAccountNumber(paymentReference);
        custPayDTOMessage.setPayerMobileNumber(customer.getCustomerMsisdn());
        custPayDTOMessage.setCreditDocumentId(custPayDTOMessage.getCreditDocumentId());
        CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
        if (jwtUtil.getLoggedInUser() != null) {
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
        } else {
            // FIX: Provide default values for mandatory NOT NULL auditing fields
            customerPayment.setCreatedById(0);
            customerPayment.setCreatedByName("System");
        }



        customerPayment = customerPaymentRepository.save(customerPayment);
        log.info("CRDB postPaymentNotification – CustomerPayment saved with INITIATE status, orderId={}",
                customerPayment.getOrderId());

        // ── 1. Execute critical CMS payment synchronously ──
        log.info("Starting synchronous processing for orderId={}", customerPayment.getOrderId());
        boolean isRecordSaved = false;
        try {
            isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
        } catch (Exception e) {
            log.error("Exception in CMS addCustomerPayment for orderId={}: {}",
                    customerPayment.getOrderId(), e.getMessage());
        }

        if (!isRecordSaved) {
            log.warn("CMS addCustomerPayment returned false for orderId={}", customerPayment.getOrderId());
            customerPayment.setStatus("failed");
            customerPayment.setGatewayStatus("failed");
            customerPaymentRepository.save(customerPayment);

            custPayDTOMessage.setStatus("failed");
            custPayDTOMessage.setGatewayStatus("failed");
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
            return CRDBPaymentPostResponseDTO.error(500, "CMS addCustomerPayment failed");
        }

        // ── 2. Build and save local Integration Service CreditDoc ──
        CreditDocumentData creditDocMessage = addInCreditDoc(customerPayment);

        try {
//            log.info("Saving CreditDoc in Integration Service for orderId={}", customerPayment.getOrderId());
////            creditdocService.save(creditDocMessage);

            // FIX: Save the ID back to CustomerPayment so duplicate checks return it instead of null
            customerPayment.setCreditDocumentId(creditDocMessage.getId());
            customerPaymentRepository.save(customerPayment);
        } catch (Exception ex) {
            log.error("Integration Service CreditDoc creation failed for orderId={}", customerPayment.getOrderId(), ex);
        }

//        try {
//            log.info("Saving CreditDoc in Revenue Service synchronously for orderId={}", customerPayment.getOrderId());
////            Integer revenueCreditDocId = revenueClient.addCreditDoc(creditDocMessage, token);
//            log.info("Revenue CreditDoc Id={} for orderId={}", revenueCreditDocId, customerPayment.getOrderId());
//
//            if (revenueCreditDocId != null) {
//                // OVERRIDE the local ID with the Revenue Service ID so it is returned as the receipt!
//                customerPayment.setCreditDocumentId(revenueCreditDocId);
//                customerPaymentRepository.save(customerPayment);
//            }
//        } catch (Exception ex) {
//            log.error("Revenue Service CreditDoc creation failed for orderId={}", customerPayment.getOrderId(), ex);
//        }

        // ── 3. Execute core logic which dispatches Kafka Events ──
        try {
            paymentIntegrationService.updateStatusAndSendToCMSForCRDB(
                    customerPayment.getOrderId().toString(),
                    transactionRef, SUCCESSFUL);
            log.info("Status updated to APPROVED for orderId={}", customerPayment.getOrderId());
        } catch (Exception ex) {
            log.error("updateStatusAndSendToCMSForCRDB failed for orderId={}", customerPayment.getOrderId(), ex);
        }

        // Return orderId as receipt immediately
        String receipt = String.valueOf(customerPayment.getOrderId());
        log.info("CRDB postPaymentNotification – completed. receipt={}", receipt);
        return CRDBPaymentPostResponseDTO.success(receipt);
    }


    private CRDBVerificationResponseDTO.Data getVerificationResponseData(CRDBVerificationRequestDTO requestDTO,
                                                                         AirtelAppToCRMDTO customer) {
        Map<String, String> configs = null;
        try {
            configs = loadCRDBConfigs();
        } catch (Exception e) {
            log.warn("CRDB getVerificationResponseData – Could not load DB configs for CRDB: {}. Using hardcoded defaults.", e.getMessage());
        }

        CRDBVerificationResponseDTO.Data data = new CRDBVerificationResponseDTO.Data();
        String firstName  = customer.getFirstName()  != null ? customer.getFirstName().trim()  : "";
        String lastName   = customer.getLastName()   != null ? customer.getLastName().trim()   : "";
        String payerName  = (firstName + " " + lastName).trim();
        if (payerName.isEmpty()) {
            payerName = customer.getUsername();
        }
        data.setPayerName(payerName);
        data.setAmount(DEFAULT_ZERO_AMOUNT); // always send 0
        
        if (configs != null && configs.containsKey(CRDB_AMOUNT_TYPE)) {
            data.setAmountType(configs.get(CRDB_AMOUNT_TYPE));
            data.setCurrency(configs.get(CRDB_DEFAULT_CURRENCY));
            data.setPaymentType(configs.get(CRDB_PAYMENT_TYPE));
            data.setPaymentDesc(configs.get(CRDB_PAYMENT_DESC));
        } else {
            // Fallback to constants if DB config is missing
            data.setAmountType(AMOUNT_TYPE_FLEXIBLE);
            data.setCurrency(CRDB_DEFAULT_CURRENCY);
            data.setPaymentType(DEFAULT_PAYMENT_TYPE);
            data.setPaymentDesc(DEFAULT_PAYMENT_DESC);
        }
        
        data.setPaymentReference(requestDTO.getPaymentReference());
        data.setPayerID(customer.getCustomerName()); // customer name as per discussions
        //data.setInstitutionID(configs.get(INSTITUTION_ID));
        //data.setTransactionChannel(configs.get(TRANSACTION_CHANNEL));
        return data;
    }


    /**
     * Validates the CRDB checksum.
     * Expected: checksum == SHA1( token + MD5(paymentReference) )
     */
    private boolean validateChecksum(String token, String paymentReference, String incomingChecksum) {
        try {
            String md5PaymentRef   = hexDigest("MD5",  paymentReference.getBytes(StandardCharsets.UTF_8));
            String expectedChecksum = hexDigest("SHA-1", (token + md5PaymentRef).getBytes(StandardCharsets.UTF_8));
            //incomingChecksum = expectedChecksum; //TODO remove after testing
            return expectedChecksum.equalsIgnoreCase(incomingChecksum);
        } catch (Exception e) {
            log.error("CRDB validateChecksum – error computing checksum: {}", e.getMessage());
            return false;
        }
    }

    //Computes a hex-encoded message digest using the given algorithm name.
    private String hexDigest(String algorithm, byte[] input) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] hash = md.digest(input);
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Long generateOrderId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy"))
                + customerId
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return Long.parseLong(id);
    }



    //Constructs the Kafka message for the payment post event.
    private CustPayDTOMessage buildKafkaMessage(CustomerPayment payment, CRDBPaymentPostRequestDTO requestDTO) {
        CustPayDTOMessage msg = new CustPayDTOMessage();
        msg.setOrderId(payment.getOrderId());
        msg.setPgTransactionId(requestDTO.getTransactionRef());
        msg.setCustId(payment.getCustId() != null ? payment.getCustId() : null);
        msg.setPayment(requestDTO.getAmount() != null ? requestDTO.getAmount() : payment.getPayment());
        msg.setStatus(payment.getStatus());
        msg.setGatewayStatus(payment.getGatewayStatus());
        msg.setMerchantName(CRDB_PAYMENT_GATEWAY_NAME);
        msg.setPaymentGatewayName(CRDB_PAYMENT_GATEWAY_NAME);
        msg.setTransactionDate(
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        msg.setPaymentDate(
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        msg.setCustomerUsername(payment.getCustomerUsername());
        msg.setMvnoid(payment.getMvnoid());
        msg.setBuid(payment.getBuid());
        msg.setAccountNumber(requestDTO.getPaymentReference());
        msg.setPayerMobileNumber(requestDTO.getPayerMobile() != null
                ? requestDTO.getPayerMobile()
                : payment.getPayerMobileNumber());
        msg.setPlanId(payment.getPlanId());
        msg.setPlanPrice(payment.getPlanPrice());
        msg.setWalletAmount(payment.getWalletAmount());
        return msg;
    }

    public CreditDocumentData addInCreditDoc(CustomerPayment payment) {
        CreditDocMessage message = buildCreditDocMessage(payment);

        return creditDocRepocitory.save(new CreditDocumentData(message));
    }
    public CreditDocMessage buildCreditDocMessage(CustomerPayment payment) {
        CreditDocMessage message = new CreditDocMessage();
        message.setId(Math.toIntExact(payment.getId()));
        message.setCustomer(payment.getCustId());
        message.setAmount(payment.getPayment());
        message.setStatus("approved");
        message.setReferenceno(String.valueOf(payment.getOrderId()));
        message.setReciptNo(payment.getPgTransactionId());
        message.setMvnoId(payment.getMvnoid());
        message.setType("payment");
        if (payment.getBuid() != null) {
            message.setBuID(payment.getBuid().longValue());
        }
        message.setPaymode("CRDB");
        message.setPaydetails1(null);
        message.setPaydetails2(null);
        message.setPaydetails3(null);
        message.setPaydetails4(String.valueOf(payment.getOrderId()));
        message.setUniquename(payment.getCustomerUsername()); // REQUIRED by Revenue Service constraints
        
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.format.DateTimeFormatter dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        message.setPaymentdate(java.time.LocalDate.now().format(dateFormatter));
        message.setCreatedate(java.time.LocalDateTime.now().format(dateTimeFormatter)); // Removed ISO 'T' character
        
        message.setXmldocument("");
        message.setApproverid(0);
        message.setIsDelete(false);
        message.setTdsflag(false);
        message.setIs_reversed(false);
        message.setPaytype("advance");
        message.setPaymentreferenceno(String.valueOf(payment.getPgTransactionId()));
        if ("approved".equalsIgnoreCase(payment.getStatus())) {
            message.setAdjustedAmount(0.0);
        } else {
            message.setAdjustedAmount(payment.getPayment());
        }
        return message;
    }

    //Returns true when a String is null or blank.
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}