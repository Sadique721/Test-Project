package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentIntegration.DTO.SelcomAppToCRMDTO;
import com.savbill.integrationsystem.PaymentIntegration.DTO.SelcomResponseDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


@Service
public class SelcomAppToCRMService {

    private static final Logger logger = LoggerFactory.getLogger(SelcomAppToCRMService.class);

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
    private CustomerPaymentRepository repository;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Value("${selcom.reverseFlow.mvnoId}")
    private Integer selcomReverseFlowMvnoId;


    public SelcomResponseDTO processC2BRequest(SelcomAppToCRMDTO request) throws PaymentValidationException {
        logger.info("processC2BRequest method start for selcom");
        String token = jwtUtil.generateJwtToken(selcomReverseFlowMvnoId.longValue());
        SelcomResponseDTO response = new SelcomResponseDTO();
        if (request.getReference() != null) {
            try {
                validateTransIdAndOrderId(request.getTransid(), request.getReference());
                AirtelAppToCRMDTO req = new AirtelAppToCRMDTO();
                req.setAccountNo(request.getUtilityReference());
                req.setMvnoId(selcomReverseFlowMvnoId);
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getCustDetailsByAcctNum(req, token);
                if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                    throw new PaymentValidationException("Utility Reference cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
                }
                if (!customerByAccountNumber.getBody().isEmpty()) {
                    AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
//                    validateMobileNumber(customer.getCustId(), request.getMsisdn(), authToken);
                    CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
//                    Long orderId = generateId(customer.getCustId().longValue());
                    custPayDTOMessage.setOrderId(Long.valueOf(request.getReference()));
                    custPayDTOMessage.setPgTransactionId(request.getTransid());
                    if (customer.getCustId() != null)
                        custPayDTOMessage.setCustId(customer.getCustId());
                    if (request.getAmount() != null)
                        custPayDTOMessage.setPayment(Double.parseDouble(request.getAmount().replace(",", "")));
                    if (request.getUtilityReference() != null)
                        custPayDTOMessage.setAccountNumber(request.getUtilityReference());
                    custPayDTOMessage.setStatus(CommonConstant.INITIATE);
                    custPayDTOMessage.setGatewayStatus(CommonConstant.INITIATE);
                    custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setMerchantName(request.getOperator());
                    custPayDTOMessage.setPaymentGatewayName(CommonConstant.SELCOM_REVERSE_FLOW);
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
                        paymentIntegrationService.updateStatusAndSendToCMS(String.valueOf(request.getReference()), request.getTransid(), "SUCCESSFUL", null);
                        customerPayment.setStatus(CommonConstant.SUCCESSFUL);
                        customerPaymentRepository.save(customerPayment);
                        custPayDTOMessage.setStatus(AirtelValidateConstant.SUCCESSFUL);
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send SUCCESSFUL Request of Selcom Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setReference(request.getReference());
                        response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.SUCCESS);
                        response.setResult(CommonConstant.TRANSACTION_SUCCESS);
                        response.setMessage("Transaction Successful");
                    } else {
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send Failed Request of Selcom Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setReference(request.getReference());
                        response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
                        response.setResult(CommonConstant.TRANSACTION_FAILURE);
                        response.setMessage("Transaction Failed");
                        return response;
                    }
                } else {
                    response.setReference(request.getReference());
                    response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.INVALID_ACCOUNT);
                    response.setResult(CommonConstant.TRANSACTION_FAILURE);
                    response.setMessage("No Utility Reference Found For Reference: " + request.getReference());
                    return response;
                }
            } catch (PaymentValidationException e) {
                logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
                response.setReference(request.getReference());
                response.setResultcode(e.getResultCode());
                response.setResult(CommonConstant.TRANSACTION_FAILURE);
                response.setMessage(e.getMessage());
            } catch (Exception e) {
                logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
                response.setReference(request.getReference());
                response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
                response.setResult(CommonConstant.TRANSACTION_FAILURE);
                response.setMessage("Transaction Failed");
            }
        }
        logger.info("processC2BRequest method end");
        return response;
    }

    public SelcomResponseDTO transactionrespons(SelcomAppToCRMDTO request) throws PaymentValidationException {
        logger.info("transactionrespons method start");
        String token = jwtUtil.generateJwtToken(selcomReverseFlowMvnoId.longValue());
        SelcomResponseDTO response = new SelcomResponseDTO();
        try {
            List<CustomerPayment> customerPaymentList = repository.findAllByPgTransactionId(request.getTransid());
            List<CustomerPayment> paymentList = repository.findAllByOrderId(Long.valueOf(request.getReference()));
            if(paymentList.isEmpty()){
                throw new PaymentValidationException("No Payment Found for reference: "+request.getReference(),CommonConstant.SELCOM_STATUS_CODES.FAILURE);
            }
            if (!customerPaymentList.isEmpty()) {
                /*fetch for account num */
                AirtelAppToCRMDTO req = new AirtelAppToCRMDTO();
                req.setAccountNo(request.getUtilityReference());
                req.setMvnoId(selcomReverseFlowMvnoId);
                /*fetch for mobile num*/
                CustomerPayment customerPayment = customerPaymentList.get(0);
//                String customerByMobileNo = cmsClient.getMobileNumber(String.valueOf(customerPayment.getCustId()), authToken);
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNo = cmsClient.getCustDetailsByAcctNum(req, token);
                if (customerPayment.getStatus().trim().equalsIgnoreCase("Successful") || customerPayment.getStatus().trim().equalsIgnoreCase("Successfull")) {
//                    if (customerByMobileNo != null && !customerByMobileNo.trim().isEmpty()) {
//                        if (request.getMsisdn().equalsIgnoreCase(customerByMobileNo)) {
                            response.setReference(request.getReference());
                            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.SUCCESS);
                            response.setResult(CommonConstant.TRANSACTION_SUCCESS);
                            response.setMessage("Fetched Successfully.");
//                        } else {
//                            response.setReference(request.getReference());
//                            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
//                            response.setResult(CommonConstant.TRANSACTION_FAILURE);
//                            response.setMessage("No Record Available for Requested Mobile Num: " + request.getMsisdn());
//                            return response;
//                        }
//                    }
                    if (customerByAccountNo.getBody() != null && customerByAccountNo.getBody().size() > 1) {
                        throw new PaymentValidationException("Utility Reference cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
                    }
                    if (!customerByAccountNo.getBody().isEmpty()) {
                        AirtelAppToCRMDTO customer = customerByAccountNo.getBody().get(0);
                        if (request.getUtilityReference().equalsIgnoreCase(customer.getAccountNo())) {
                            response.setReference(request.getReference());
                            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.SUCCESS);
                            response.setResult(CommonConstant.TRANSACTION_SUCCESS);
                            response.setMessage("Fetched Successfully.");
                        } else {
                            response.setReference(request.getReference());
                            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
                            response.setResult(CommonConstant.TRANSACTION_FAILURE);
                            response.setMessage("No Record Available for Requested Account Num: " + request.getUtilityReference());
                            return response;
                        }
                    } else {
                        response.setReference(request.getReference());
                        response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
                        response.setResult(CommonConstant.TRANSACTION_FAILURE);
                        response.setMessage("No Record Available for Requested Account Num: " + request.getUtilityReference());
                        return response;
                    }
                }
                if (customerPayment.getStatus().trim().equalsIgnoreCase("failed")) {
                    response.setReference(request.getReference());
                    response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.FAILURE);
                    response.setResult(CommonConstant.TRANSACTION_FAILURE);
                    response.setMessage("Fetched Successfully.");
                }
            } else {
                response.setReference(request.getReference());
                response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
                response.setResult(CommonConstant.TRANSACTION_FAILURE);
                response.setMessage("No Payment Found for transid: " + request.getTransid());
                return response;
            }
        } catch (PaymentValidationException e) {
            logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
            response.setReference(request.getReference());
            response.setResultcode(e.getResultCode());
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error While Sending Data of Selcom to CMS Through Kafka. ", e.getMessage());
            response.setReference(request.getReference());
            response.setResultcode(CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
            response.setResult(CommonConstant.TRANSACTION_FAILURE);
            response.setMessage("Transaction Failed");
        }
        logger.info("transaction response method end");
        return response;
    }

    public void validateProcessTxRequestData(SelcomAppToCRMDTO request) {
        if (request.getUtilityReference() == null || request.getUtilityReference().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "Utility Reference cannot be empty.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        if (request.getAmount() != null && !request.getAmount().trim().isEmpty()) {
            String amount = request.getAmount();
            if (!isValidAmount(amount)) {
                throw new PaymentValidationException(
                        "Invalid amount",
                        CommonConstant.SELCOM_STATUS_CODES.INVALID_ACCOUNT
                );
            }
        }

        if (request.getAmount() != null && Double.parseDouble(request.getAmount().replace(",", "")) < 500) {
            throw new PaymentValidationException(
                    "Amount too low.",
                    CommonConstant.SELCOM_STATUS_CODES.AMOUNT_TOO_LOW
            );
        }

        if (request.getAmount() != null && Double.parseDouble(request.getAmount().replace(",", "")) > 5000000) {
            throw new PaymentValidationException(
                    "Amount too high.",
                    CommonConstant.SELCOM_STATUS_CODES.AMOUNT_TOO_HIGH
            );
        }

        if (request.getTransid() != null && request.getTransid().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "TransId cannot be empty.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST
            );
        }

        if(request.getMsisdn() == null ){
            throw new PaymentValidationException(
                    "Msisdn not found.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST
            );
        }

        if (request.getMsisdn() != null && request.getMsisdn().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "Msisdn cannot be empty.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST
            );
        }


        if (request.getAmount() != null && request.getAmount().trim().isEmpty()) {
            throw new PaymentValidationException(
                    "Amount cannot be empty.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST
            );
        }


        if (request.getReference() == null || request.getReference().isEmpty()) {
            throw new PaymentValidationException(
                    "Reference cannot be null or empty.",
                    CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST
            );
        }


    }

    public static boolean isValidAmount(String amount) {
        return Pattern.matches("^[0-9]+(\\.[0-9]+)?$", amount);
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


    /*public boolean validateMobileNumber(Integer custId, String requestedMobileNum, String authToken) {
        String customerByMobileNo = cmsClient.getMobileNumber(String.valueOf(custId), authToken);
        if (customerByMobileNo == null || customerByMobileNo.isEmpty()) {
            throw new PaymentValidationException("No valid mobile number found in customer data", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
        if (customerByMobileNo.length() != requestedMobileNum.length()) {
            throw new PaymentValidationException("Invalid mobile number. Mobile Num Must Be " + customerByMobileNo.length() + " Digit.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
        if(!customerByMobileNo.equals(requestedMobileNum)){
            throw new PaymentValidationException("Invalid mobile number pass in request.", CommonConstant.TIGOPESA_STATUS_CODES.GENERAL_ERROR);
        }
        else {
            return true;
        }

    }*/

    public void validateTransIdAndOrderId(String transId, String orderId) {
        List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(transId);
        if (!allByPgTransactionId.isEmpty() && allByPgTransactionId.size() > 1) {
            throw new PaymentValidationException("TransId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
        if (!allByPgTransactionId.isEmpty() && allByPgTransactionId.get(0).getPgTransactionId().equals(transId)) {
            throw new PaymentValidationException("TransId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
        List<CustomerPayment> allByOrderId = customerPaymentRepository.findAllByOrderId(Long.valueOf(orderId));
        if (!allByOrderId.isEmpty() && allByOrderId.size() > 1) {
            throw new PaymentValidationException("OrderId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
        if (!allByOrderId.isEmpty() && allByOrderId.get(0).getOrderId().equals(orderId)) {
            throw new PaymentValidationException("OrderId cannot be duplicate.", CommonConstant.SELCOM_STATUS_CODES.BAD_REQUEST);
        }
    }
}



