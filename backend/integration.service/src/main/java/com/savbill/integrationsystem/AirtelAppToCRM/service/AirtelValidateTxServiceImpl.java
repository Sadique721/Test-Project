package com.savbill.integrationsystem.AirtelAppToCRM.service;

import com.savbill.integrationsystem.AirtelAppToCRM.AirtelValidateTxValidator;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelCRMRequestDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelValidateTxRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.TransactionEnquiryRequest;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.AirtelValidateTxResponse;
import com.savbill.integrationsystem.AirtelAppToCRM.ResponseDTO.TransactionEnquiryResponse;
import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.AirtelIntigration.AirtelIntigrationService;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PaymentIntegration.Service.PaymentIntegrationService;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class AirtelValidateTxServiceImpl implements AirtelValidateTxService {
    private static final Logger logger = LoggerFactory.getLogger(AirtelIntigrationService.class);

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomerPaymentRepository repository;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;


    @Override
    public AirtelValidateTxResponse processB2CRequest(AirtelValidateTxRequest request, String token) throws JAXBException {
        logger.info("processB2CRequest method start");
        AirtelValidateTxResponse response = new AirtelValidateTxResponse();
        if (request.getReference() != null) {
            try {
                List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(request.getReference1());
                if (!allByPgTransactionId.isEmpty()) {
                    throw new AirtelValidateTxValidator(AirtelValidateConstant.REFERENCE_1 + " CAN NOT BE DUPLICATE.", 400);
                }
                AirtelAppToCRMDTO req = new AirtelAppToCRMDTO();
                req.setAccountNo(request.getReference());
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(req, token);
                if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                    throw new AirtelValidateTxValidator(AirtelValidateConstant.REFERENCE + " CAN NOT BE DUPLICATE.", 400);
                }

                if (!customerByAccountNumber.getBody().isEmpty()) {
                    AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
                    CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                    Long orderId = generateId(customer.getCustId().longValue());
                    custPayDTOMessage.setOrderId(orderId);
                    custPayDTOMessage.setPgTransactionId(request.getReference1());
                    if (customer.getCustId() != null)
                        custPayDTOMessage.setCustId(customer.getCustId());
                    if (request.getAmount() != null)
                        custPayDTOMessage.setPayment(Double.parseDouble(request.getAmount()));
                    if (request.getReference() != null)
                        custPayDTOMessage.setAccountNumber(request.getReference());
                    custPayDTOMessage.setStatus(AirtelValidateConstant.INITIATE);
                    custPayDTOMessage.setGatewayStatus(AirtelValidateConstant.INITIATE);
                    custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setMerchantName(AirtelValidateConstant.AIRTEL_REVERSE_FLOW);
                    custPayDTOMessage.setPaymentGatewayName(AirtelValidateConstant.AIRTEL_REVERSE_FLOW);
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
                        paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), request.getReference1(), "SUCCESSFUL", null);
                        customerPayment.setStatus(AirtelValidateConstant.SUCCESSFUL);
                        customerPaymentRepository.save(customerPayment);
                        custPayDTOMessage.setStatus(AirtelValidateConstant.SUCCESSFUL);
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send SUCCESSFUL Request of Airtel Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setStatus(200);
                        response.setTranscationId(String.valueOf(orderId));
                        response.setMessage("Transaction Successful");
                    } else {
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send Failed Request of Airtel Data to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                        response.setStatus(400);
                        response.setTranscationId(String.valueOf(orderId));
                        response.setMessage("Transaction Failed");
                    }
                }else {
                    response.setStatus(400);
                    response.setMessage("REFERENCE DOESN'T EXIST");
                }
            }
            catch (AirtelValidateTxValidator e) {
                logger.error("Error While Sending Data of Airtel to CMS Through Kafka. ", e.getMessage());
                response.setStatus(e.statusCode);
                response.setMessage(e.getMessage());
            }
            catch (Exception e) {
                logger.error("Error While Sending Data of Airtel to CMS Through Kafka. ", e.getMessage());
                response.setStatus(400);
                response.setMessage("Transaction Failed");
            }
        }
        logger.info("processB2CRequest method end");
        return generateRespons(response);
    }

    public boolean saveProcessTransaction(AirtelAppToCRMDTO airtelAppToCRMDTO, CustPayDTOMessage custPayDTOMessage, String token) {
        boolean isRecordSaved = false;
        try {
            AirtelCRMRequestDTO airtelCRMRequestDTO = new AirtelCRMRequestDTO();
            airtelCRMRequestDTO.setAirtelAppToCRMDTO(airtelAppToCRMDTO);
            airtelCRMRequestDTO.setCustPayDTOMessage(custPayDTOMessage);
            isRecordSaved = revenueClient.saveProcessTransaction(airtelCRMRequestDTO, token);
        } catch (Exception e){
            e.printStackTrace();
            logger.error("ERROR while getting saveProcessTransaction");
        }
        return isRecordSaved;
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
    public AirtelValidateTxResponse validateC2BRequest(AirtelValidateTxRequest request, String token) throws JAXBException {
        logger.info("validateC2BRequest method start");
        AirtelAppToCRMDTO dto = new AirtelAppToCRMDTO();
        dto.setCustomerMsisdn(request.getCustomerMsisdn());
        dto.setAccountNo(request.getReference());

        ResponseEntity<List<AirtelAppToCRMDTO>> customerByMobileNumber = cmsClient.getcustomersByAccountNumber(dto, token);

        AirtelValidateTxResponse response;

        if (!customerByMobileNumber.getBody().isEmpty()) {

            AirtelAppToCRMDTO airtelAppToCRMDTO = customerByMobileNumber.getBody().get(0);
//            double walletBalance = Double.parseDouble(airtelAppToCRMDTO.getWalletBalance());
//            double requesetAmmount = Double.parseDouble(request.getAmount());
            response = new AirtelValidateTxResponse();

//            if (!airtelAppToCRMDTO.getStatus().trim().equalsIgnoreCase(AirtelValidateConstant.ACTIVE)) {
//                response.setStatus(400);
//                response.setMessage("Customer is not Active.");
//                return generateRespons(response);
//            }
//            if (walletBalance > requesetAmmount) {
                response.setStatus(200);
                response.setMessage("Customer validated Successfully.");
                return generateRespons(response);
//            }
//            else {
//                response.setStatus(400);
//                response.setMessage("Entered Amount is greater than wallet Balance.");
//                return generateRespons(response);
//            }
        }

        response = new AirtelValidateTxResponse();
        response.setStatus(400);
        response.setMessage("Customer is not valid.");
        logger.info("validateC2BRequest method end");
        return generateRespons(response);
    }

    @Override
    public AirtelValidateTxResponse processLOOKUPRequest(AirtelValidateTxRequest request, String token) throws JAXBException {
        logger.info("processLOOKUPRequest method start");
        AirtelAppToCRMDTO dto = new AirtelAppToCRMDTO();
        dto.setAccountNo(request.getCustomerRef());
        ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNum = cmsClient.getcustomersByAccountNumber(dto, token);

        AirtelValidateTxResponse response = new AirtelValidateTxResponse();

        if (customerByAccountNum.getBody().isEmpty()) {
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setMessage("No record Found with Given CUSTOMERREF.");
            logger.info("processLOOKUPRequest method end");
            return generateRespons(response);
        } else {
            AirtelAppToCRMDTO airtelAppToCRMDTO = customerByAccountNum.getBody().get(0);
            response.setStatus(200);
            response.setFirstName(String.valueOf(airtelAppToCRMDTO.getFirstName()));
            response.setLastName(String.valueOf(airtelAppToCRMDTO.getLastName()));
            response.setMessage("Customer Fetched Successfully");
            logger.info("processLOOKUPRequest method end");
            return generateRespons(response);
        }
    }


    @Override
    public AirtelValidateTxResponse processBILLFETCHRequest(AirtelValidateTxRequest request) throws JAXBException {
        logger.info("processBILLFETCHRequest method start");

        String authToken = jwtUtil.generateJwtToken(2L);
        AirtelAppToCRMDTO dto = new AirtelAppToCRMDTO();
        dto.setAccountNo(request.getCustomerRef());
        dto.setCustomerMsisdn(request.getCustomerMsisdn());
        ResponseEntity<AirtelAppToCRMDTO> customerByMobileNo = cmsClient.getcustomersbillFetch(dto, authToken);

        AirtelAppToCRMDTO body = customerByMobileNo.getBody();
        if (body != null) {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(200);
            response.setFirstName(body.getFirstName());
            response.setLastName(body.getLastName());
            LocalDate date = LocalDate.parse(body.getDueDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            response.setDueDate(formattedDate);
            response.setAmmount(toggleAmountSign(body.getWalletBalance()));
            response.setCurrency(body.getCurrencyCode());
            response.setMessage("Bill Fetched Successfully.");
            logger.info("processBILLFETCHRequest method end");
            return generateRespons(response);
        } else {
            AirtelValidateTxResponse response = new AirtelValidateTxResponse();
            response.setStatus(404);
            response.setFirstName("");
            response.setLastName("");
            response.setDueDate("");
            response.setAmmount("");
            response.setCurrency("");
            response.setMessage("No record Found with Given " + AirtelValidateConstant.CUSTOMER_REF + ".");
            logger.info("processBILLFETCHRequest method end");
            return generateRespons(response);
        }
    }

    public static String toggleAmountSign(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be null or empty");
        }

        // Convert string to double
        double value = Double.parseDouble(amount.trim());

        // Toggle sign only if value is not zero
        if (value != 0) {
            value = -value;
        }

        // Convert back to string, removing .0 for whole numbers
        return (value == (int) value) ? String.valueOf((int) value) : String.valueOf(value);
    }

    @Override
    public void validateLookUpRequestData(AirtelValidateTxRequest request) {
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "TYPE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            if(!request.getType().trim().equals("LOOKUP")){
                throw new AirtelValidateTxValidator(
                        "INCORRECT TYPE.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        if (request.getCustomerMsisdn() == null || (request.getCustomerMsisdn() != null && request.getCustomerMsisdn().trim().isEmpty())) {
            throw new AirtelValidateTxValidator(
                    "CUSTOMERMSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

//        if (request.getUsername() == null || (request.getUsername() != null && request.getUsername().trim().isEmpty())) {
//            throw new AirtelValidateTxValidator(
//                    "USERNAME CANNOT BE EMPTY.",
//                    HttpStatus.BAD_REQUEST.value()
//            );
//        }
//
//        if (request.getPassword() == null || (request.getPassword() != null && request.getPassword().trim().isEmpty())) {
//            throw new AirtelValidateTxValidator(
//                    "PASSWORD CANNOT BE EMPTY.",
//                    HttpStatus.BAD_REQUEST.value()
//            );
//        }

    }

    @Override
    public void validateBillFetchRequestData(AirtelValidateTxRequest request) {
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "TYPE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            if(!request.getType().trim().equals("BILLFETCH")){
                throw new AirtelValidateTxValidator(
                        "INCORRECT TYPE.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        if (request.getCustomerMsisdn() == null || (request.getCustomerMsisdn() != null && request.getCustomerMsisdn().trim().isEmpty())) {
            throw new AirtelValidateTxValidator(
                    "CUSTOMERMSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }


        if (request.getCustomerRef() == null || (request.getCustomerRef() != null && request.getCustomerRef().trim().isEmpty())) {
            throw new AirtelValidateTxValidator(
                    AirtelValidateConstant.CUSTOMER_REF + " CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    @Override
    public void validateC2BRequestData(AirtelValidateTxRequest request) {
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "TYPE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            if(!request.getType().trim().equals("C2B")){
                throw new AirtelValidateTxValidator(
                        "INCORRECT TYPE.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        if (request.getCustomerMsisdn() != null && request.getCustomerMsisdn().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "CUSTOMERMSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

//        if (request.getMerchantMsisdn() != null && request.getMerchantMsisdn().trim().isEmpty()) {
//            throw new AirtelValidateTxValidator(
//                    "MERCHANTMSISDN CANNOT BE EMPTY.",
//                    HttpStatus.BAD_REQUEST.value()
//            );
//        }

        if (request.getAmount() != null && request.getAmount().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "AMOUNT CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getAmount() != null && !request.getAmount().trim().isEmpty()) {
            String amount = request.getAmount();
            if (!isValidAmount(amount)) {
                throw new AirtelValidateTxValidator(
                        "INVALID AMOUNT.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }


        if (request.getReference() != null && request.getReference().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "REFERENCE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getReference1() != null && request.getReference1().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "REFERENCE1 CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );

        }
    }

    public static boolean isValidAmount(String amount) {
        return Pattern.matches("^[0-9]+(\\.[0-9]+)?$", amount);
    }

    @Override
    public void validateProcessTxRequestData(AirtelValidateTxRequest request) {
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "TYPE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            if(!request.getType().trim().equals("C2B")){
                throw new AirtelValidateTxValidator(
                        "INCORRECT TYPE.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        if(request.getCustomerMsisdn() == null || request.getMerchantMsisdn() == null){
            throw new AirtelValidateTxValidator(
                    "CUSTOMERMSISDN OR MERCHANTMSISDN NOT FOUND.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getCustomerMsisdn() != null && request.getCustomerMsisdn().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "CUSTOMERMSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getMerchantMsisdn() != null && request.getMerchantMsisdn().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "MERCHANTMSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getAmount() != null && request.getAmount().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "AMOUNT CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getAmount() != null && !request.getAmount().trim().isEmpty()) {
            String amount = request.getAmount();
            if (!isValidAmount(amount)) {
                throw new AirtelValidateTxValidator(
                        "INVALID AMOUNT.",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
        }

        if (request.getReference() != null && request.getReference().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "REFERENCE CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getReference1() != null && request.getReference1().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "REFERENCE1 CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value()
            );

        }
    }


    @Override
    public AirtelValidateTxResponse generateRespons(AirtelValidateTxResponse response) throws JAXBException {
        // Convert response object to XML
        JAXBContext context = JAXBContext.newInstance(AirtelValidateTxResponse.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return response;
    }

    @Override
    public TransactionEnquiryResponse transactionrespons(TransactionEnquiryRequest request) throws JAXBException {
        logger.info("transactionrespons method start");
        String authToken = jwtUtil.generateJwtToken(2L);

        List<CustomerPayment> customerPaymentList = repository.findAllByPgTransactionId(request.getTXNID());
        if (!customerPaymentList.isEmpty()) {
            CustomerPayment customerPayment = customerPaymentList.get(0);
//            String customerByMobileNo = cmsClient.getMobileNumber(String.valueOf(customerPayment.getCustId()), authToken);
            if(customerPayment.getStatus().trim().equalsIgnoreCase("Successful") || customerPayment.getStatus().trim().equalsIgnoreCase("Successfull")) {
//                if (customerByMobileNo != null && !customerByMobileNo.trim().isEmpty()) {
//                    if (request.getMSISDN().equalsIgnoreCase(customerByMobileNo)) {
                        TransactionEnquiryResponse response = new TransactionEnquiryResponse();
                        response.setStatus(200);
                        response.setMessage("Fetched Successfully.");
                        response.setReference(String.valueOf(customerPayment.getOrderId()));
                        return response;
//                    }
//                }
            }
            if(customerPayment.getStatus().trim().equalsIgnoreCase("failed")) {
                TransactionEnquiryResponse response = new TransactionEnquiryResponse();
                response.setStatus(400);
                response.setMessage("Fetched Successfully");
                response.setReference(String.valueOf(customerPayment.getOrderId()));
                return response;
            }
        }
        else {
            TransactionEnquiryResponse response = new TransactionEnquiryResponse();
            response.setStatus(404);
            response.setMessage("Transaction ID Not Found.");
            return response;
        }
        TransactionEnquiryResponse response = new TransactionEnquiryResponse();
        response.setStatus(404);
        response.setMessage("Transaction ID Not Found.");
        response.setReference("");
        logger.info("transactionrespons method end");
        return response;
    }

    @Override
    public void validateTransactionRequestData(TransactionEnquiryRequest request) {
        if (request.getMSISDN() == null || request.getMSISDN().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "MSISDN CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value(),
                    "reference"
            );
        }
        if (request.getTXNID() == null || request.getTXNID().trim().isEmpty()) {
            throw new AirtelValidateTxValidator(
                    "TXNID CANNOT BE EMPTY.",
                    HttpStatus.BAD_REQUEST.value(),
                    "reference"
            );
        }
    }

    @Override
    public TransactionEnquiryResponse generatetransactionRespons(TransactionEnquiryResponse response) throws JAXBException {
        // Convert response object to XML
        JAXBContext context = JAXBContext.newInstance(TransactionEnquiryResponse.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return response;
    }

}
