package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TigoAppToCRMDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.TigoPesaResponseDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.core.CommonConstant;
import com.savbill.integrationsystem.core.exceptions.PaymentValidationException;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class TigoPesaAppToCRMService {

    private static final Logger logger = LoggerFactory.getLogger(TigoPesaAppToCRMService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CMSClient cmsClient;


    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    public TigoPesaResponseDTO processC2BRequest(TigoAppToCRMDTO request, String token) throws PaymentValidationException, JAXBException {
        logger.info("processC2BRequest method start for tigoPesa");
//        String authToken = jwtUtil.generateJwtToken(2L);
        TigoPesaResponseDTO response = new TigoPesaResponseDTO();
        if (request.getCustomerReference() != null) {
            try {
                validateTransIdAndOrderId(request.getTransId());
                AirtelAppToCRMDTO req = new AirtelAppToCRMDTO();
                req.setAccountNo(request.getCustomerReference());
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(req, token);
                if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                    throw new PaymentValidationException("Customer Reference cannot be duplicate.", CommonConstant.TIGOPESA_STATUS_CODES.INVALID_CUSTOMER_REFERENCE);
                }
                if (!customerByAccountNumber.getBody().isEmpty()) {
                    AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
//                    validateMobileNumber(customer.getCustId(), request.getMsisdn(), authToken);
                    CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                    Long orderId = generateId(customer.getCustId().longValue());
                    custPayDTOMessage.setOrderId(orderId);
                    custPayDTOMessage.setPgTransactionId(request.getTransId());
                    if (customer.getCustId() != null)
                        custPayDTOMessage.setCustId(customer.getCustId());
                    if (request.getAmount() != null)
                        custPayDTOMessage.setPayment(Double.parseDouble(request.getAmount().replace(",", "")));
                    if (request.getCustomerReference() != null)
                        custPayDTOMessage.setAccountNumber(request.getCustomerReference());
                    custPayDTOMessage.setStatus(CommonConstant.INITIATE);
                    custPayDTOMessage.setGatewayStatus(CommonConstant.INITIATE);
                    custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setMerchantName(request.getSenderName());
                    custPayDTOMessage.setPaymentGatewayName(CommonConstant.TIGOPESA_REVERSE_FLOW);
                    custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setCustomerUsername(customer.getUsername());
                    custPayDTOMessage.setMvnoid(customer.getMvnoId());
                    custPayDTOMessage.setBuid(customer.getBuId());
                    custPayDTOMessage.setPayerMobileNumber(customer.getCustomerMsisdn());
                    CustomerPayment customerPayment = new CustomerPayment();
                    customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//                    customerPayment.setId(getLatestId());
                    customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
                    customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
                    customerPayment = customerPaymentRepository.save(customerPayment);
                    custPayDTOMessage.setId(customerPayment.getId());
                    boolean isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
                    if (isRecordSaved) {
                        paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), request.getTransId(), "SUCCESSFUL", null);
                        customerPayment.setStatus(CommonConstant.SUCCESSFUL);
                        customerPaymentRepository.save(customerPayment);
                        custPayDTOMessage.setStatus(CommonConstant.SUCCESSFUL);
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send SUCCESSFUL Request of TigoPesa Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
                        response.setTransId(request.getTransId());
                        response.setRefId(customerPayment.getOrderId());
                        response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_SUCCESS);
                        response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.SUCCESSFUL_TRANSACTION);
                        response.setErrorDesc("Transaction Successful");
                        response.setMsisdn(request.getMsisdn());
                        response.setFlag("Y");
                        response.setContent("Dear Subscriber your Transaction has been submitted successfully.");
                    } else {
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send Failed Request of Selcom Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
                        response.setTransId(request.getTransId());
                        response.setRefId(customerPayment.getOrderId());
                        response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
                        response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
                        response.setErrorDesc("Transaction Failure");
                        response.setMsisdn(request.getMsisdn());
                        response.setFlag("N");
                        response.setContent("Dear Subscriber your Transaction has not been submitted successfully.");
                        return generateRespone(response);
                    }
                } else {
                    Long orderId = fetchOrderId(request);
                    response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
                    response.setTransId(request.getTransId());
                    response.setRefId(orderId);
                    response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
                    response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_CUSTOMER_REFERENCE);
                    response.setErrorDesc("Transaction Failure");
                    response.setMsisdn(request.getMsisdn());
                    response.setFlag("N");
                    response.setContent("No Customer Reference Found For REFID: " +orderId);
                    return generateRespone(response);
                }
            } catch (PaymentValidationException e) {
                Long orderId = fetchOrderId(request);
                logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
                response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
                response.setTransId(request.getTransId());
                response.setRefId(orderId);
                response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
                response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
                response.setErrorDesc("Transaction Failure");
                response.setMsisdn(request.getMsisdn());
                response.setFlag("N");
                response.setContent(e.getMessage());
                return generateRespone(response);
            } catch (Exception e) {
                Long orderId = fetchOrderId(request);
                logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
                response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
                response.setTransId(request.getTransId());
                response.setRefId(orderId);
                response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
                response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
                response.setErrorDesc("Transaction Failure");
                response.setMsisdn(request.getMsisdn());
                response.setFlag("N");
                response.setContent("Dear Subscriber your Transaction has not been submitted successfully");
                return generateRespone(response);
            }
        }
        logger.info("processC2BRequest method end");
        return generateRespone(response);
    }

    public Long fetchOrderId(TigoAppToCRMDTO request) throws JAXBException {
        Long orderId = null;
        TigoPesaResponseDTO response = new TigoPesaResponseDTO();
        try {
           orderId  = customerPaymentRepository.findOrderIdBypgTransactionId(request.getTransId());
            if (orderId == null)
                throw new PaymentValidationException(
                        "REFID/OrderId cannot found.",
                        CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
                );
        }catch (PaymentValidationException e){
            response.setType(CommonConstant.TIGOPESA_CONSTANTS.SYNC_BILLPAY_RESPONSE);
            response.setTransId(request.getTransId());
            response.setRefId(orderId);
            response.setResult(CommonConstant.TIGOPESA_CONSTANTS.TRANSACTION_FAILURE);
            response.setErrorCode(CommonConstant.TIGOPESA_STATUS_CODES.INVALID_PAYMENT);
            response.setErrorDesc("Transaction Failure");
            response.setMsisdn(request.getMsisdn());
            response.setFlag("N");
            response.setContent(e.getMessage());
            generateRespone(response);
        }
        return orderId;
    }

    public void validateProcessTxRequestData(TigoAppToCRMDTO request) {
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "Type cannot be empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getAmount() != null && Double.parseDouble(request.getAmount().replace(",", "")) < 500) {
            throw new PaymentValidationException(
                    "Amount too low, Try a larger amount.",
                    CommonConstant.TIGOPESA_STATUS_CODES.AMOUNT_TOO_LOW
            );
        }
        if (request.getAmount() != null && Double.parseDouble(request.getAmount().replace(",", "")) > 5000000) {
            throw new PaymentValidationException(
                    "Amount too high,Try a smaller amount.",
                    CommonConstant.TIGOPESA_STATUS_CODES.AMOUNT_TOO_HIGH
            );
        }
        if (request.getTransId() != null && request.getTransId().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "TXNID cannot be empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getMsisdn() != null && request.getMsisdn().trim().isEmpty() || request.getMsisdn() == null) {
            throw new PaymentValidationException(
                    "Msisdn cannot be empty or not found.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getAmount() != null && request.getAmount().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "Amount cannot be empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getAmount() != null && !request.getAmount().trim().isEmpty()) {
            String amount = request.getAmount();
            if (!isValidAmount(amount.replace(",",""))) {
                throw new PaymentValidationException(
                        "Invalid amount",
                        CommonConstant.TIGOPESA_STATUS_CODES.INVALID_AMOUNT
                );
            }
        }
        if (request.getCompanyName() == null && !request.getCompanyName().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "CompanyName cannot be empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getCustomerReference() == null || request.getCustomerReference().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "CustomerReferenceId cannot be null or empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }
        if (request.getSenderName() == null && !request.getSenderName().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "SenderName cannot be empty.",
                    CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR
            );
        }


    }

    public static boolean isValidAmount(String amount) {
        return Pattern.matches("^[0-9]+(\\.[0-9]+)?$", amount);
    }

    public boolean validateMobileNumber(Integer custId, String requestedMobileNum, String authToken) {
        String customerByMobileNo = cmsClient.getMobileNumber(String.valueOf(custId), authToken);
        if (customerByMobileNo == null || customerByMobileNo.isEmpty()) {
            throw new PaymentValidationException("No valid mobile number found in customer data", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        }
        if(!customerByMobileNo.equals(requestedMobileNum)){
            throw new PaymentValidationException("Invalid mobile number pass in request.", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        }
        if (customerByMobileNo.length() != requestedMobileNum.length()) {
            throw new PaymentValidationException("Invalid mobile number. Mobile Num Must Be " + customerByMobileNo.length() + " Digit.", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        } else {
            return true;
        }

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

    public void validateTransIdAndOrderId(String transId) {

        List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(transId);
        if (!allByPgTransactionId.isEmpty() && allByPgTransactionId.size() > 1) {
            throw new PaymentValidationException("TransId cannot be duplicate.", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        }
        if (!allByPgTransactionId.isEmpty() && allByPgTransactionId.get(0).getPgTransactionId().equals(transId)) {
            throw new PaymentValidationException("TransId cannot be duplicate.", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        }
//        List<CustomerPayment> allByOrderId = customerPaymentRepository.findAllByOrderId(orderId);
//        if (!allByOrderId.isEmpty() && allByOrderId.size() > 1) {
//            throw new PaymentValidationException("OrderId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
//        }
//        if (!allByOrderId.isEmpty() && allByOrderId.get(0).getOrderId().equals(orderId)) {
//            throw new PaymentValidationException("OrderId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
//        }
    }

    public TigoPesaResponseDTO generateRespone(TigoPesaResponseDTO response) throws JAXBException {
        // Convert response object to XML
        JAXBContext context = JAXBContext.newInstance(TigoPesaResponseDTO.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return response;
    }


}
