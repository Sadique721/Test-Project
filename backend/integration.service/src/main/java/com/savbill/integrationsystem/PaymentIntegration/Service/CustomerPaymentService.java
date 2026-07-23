package com.savbill.integrationsystem.PaymentIntegration.Service;

import com.savbill.integrationsystem.AirtelAppToCRM.RequestDTO.AirtelAppToCRMDTO;
import com.savbill.integrationsystem.AirtelAppToCRM.constant.AirtelValidateConstant;
import com.savbill.integrationsystem.Migration.CMSClient;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.DTO.*;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PostpaidPlan.PostpaidPlanRepository;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.mvno.RevenueClient;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class
CustomerPaymentService {

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerPaymentService.class);

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private JwtUtil  jwtUtil;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;

    @Autowired
    private RevenueClient revenueClient;

    public void saveCustomerPayment(CustPayDTOMessage customerPayment) {
        try {
            if (customerPayment != null) {
                CustomerPayment saveCustPay = convertMessageToEntity(customerPayment);
                customerPaymentRepository.save(saveCustPay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerPayment(CustPayDTOMessage customerPayment) {
        try {
            CustomerPayment updatedCustomerPayment = customerPaymentRepository.findByOrderId((customerPayment.getOrderId()));
            if (updatedCustomerPayment != null) {
                updatedCustomerPayment.setPaymentDate(getDate(customerPayment.getPaymentDate()));
                updatedCustomerPayment.setStatus(customerPayment.getStatus());
                updatedCustomerPayment.setTransactionDate(getDate(customerPayment.getTransactionDate()));
                customerPaymentRepository.save(updatedCustomerPayment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomerPayment convertMessageToEntity(CustPayDTOMessage custPayDTOMessage){
        CustomerPayment payment = new CustomerPayment();

        payment.setId(custPayDTOMessage.getId());
        payment.setPayment(custPayDTOMessage.getPayment());
        payment.setCustId(custPayDTOMessage.getCustId());
        payment.setMvnoid(custPayDTOMessage.getMvnoid());
        payment.setMerchantName(custPayDTOMessage.getMerchantName());
        payment.setStatus(custPayDTOMessage.getStatus());
        payment.setGatewayStatus(custPayDTOMessage.getGatewayStatus());
        payment.setPaymentDate(getDate(custPayDTOMessage.getPaymentDate()));
        payment.setTransactionDate(getDate(custPayDTOMessage.getTransactionDate()));
        payment.setBuid(custPayDTOMessage.getBuid());
        payment.setCustomerUsername(custPayDTOMessage.getCustomerUsername());
        payment.setCreditDocumentId(custPayDTOMessage.getCreditDocumentId());
        payment.setPlanId(custPayDTOMessage.getPlanId());
        payment.setPaymentLink(custPayDTOMessage.getPaymentLink());
        payment.setOrderId(custPayDTOMessage.getOrderId());
        payment.setIsFromCaptive(custPayDTOMessage.getIsFromCaptive());
        payment.setPgTransactionId(custPayDTOMessage.getPgTransactionId());
        payment.setPaymentLink(custPayDTOMessage.getPaymentLink());
        payment.setChecksum(custPayDTOMessage.getChecksum());
        payment.setPartnerId(custPayDTOMessage.getPartnerId());
        payment.setAccountNumber(custPayDTOMessage.getAccountNumber());
        payment.setPartnerPaymentId(custPayDTOMessage.getPartnerPaymentId());
        payment.setPlanId(null);
        if(custPayDTOMessage.getCustomerUUID() != null){
            payment.setCustomerUUID(custPayDTOMessage.getCustomerUUID());
        }if(custPayDTOMessage.getWalletAmount() != null) {
            payment.setWalletAmount(custPayDTOMessage.getWalletAmount());
        }if(custPayDTOMessage.getPlanPrice() != null) {
            payment.setPlanPrice(custPayDTOMessage.getPlanPrice());
        }if(custPayDTOMessage.getPayerMobileNumber() != null) {
            payment.setPayerMobileNumber(custPayDTOMessage.getPayerMobileNumber());
        }if(custPayDTOMessage.getChildId() != null){
            payment.setChildId(custPayDTOMessage.getChildId());
        }
        return payment;
    }

    public CustPayDTOMessage convertEntityToMessage(CustomerPayment customerPayment){
        CustPayDTOMessage payment = new CustPayDTOMessage();

        payment.setId(customerPayment.getId());
        payment.setPayment(customerPayment.getPayment());
        payment.setCustId(customerPayment.getCustId());
        payment.setMvnoid(customerPayment.getMvnoid());
        payment.setMerchantName(customerPayment.getMerchantName());
        payment.setStatus(customerPayment.getStatus());
        payment.setPaymentDate(customerPayment.getPaymentDate().toString());
        payment.setTransactionDate(customerPayment.getTransactionDate().toString());
        payment.setBuid(customerPayment.getBuid());
        payment.setCustomerUsername(customerPayment.getCustomerUsername());
        payment.setCreditDocumentId(customerPayment.getCreditDocumentId());
        payment.setPlanId(customerPayment.getPlanId());
        payment.setPaymentLink(customerPayment.getPaymentLink());
        payment.setOrderId(customerPayment.getOrderId());
        payment.setIsFromCaptive(customerPayment.getIsFromCaptive());
        payment.setPgTransactionId(customerPayment.getPgTransactionId());
        payment.setPaymentLink(customerPayment.getPaymentLink());
        payment.setPartnerId(customerPayment.getPartnerId());
        payment.setPartnerPaymentId(customerPayment.getPartnerPaymentId());
        payment.setIsAdvancePayment(customerPayment.getIsAdvancePayment());
        payment.setAccountNumber(customerPayment.getAccountNumber());
        if(customerPayment.getInvoiceId() != null){
            payment.setInvoiceId(customerPayment.getInvoiceId());
        }
        if(customerPayment.getCreatedById() != null){
            payment.setCreatedById(customerPayment.getCreatedById());
        }
        if(customerPayment.getCreatedByName() != null){
            payment.setCreatedByName(customerPayment.getCreatedByName());
        }
        if(customerPayment.getChildId() != null){
            payment.setChildId(customerPayment.getChildId());
        }
      //  payment.setCustomerUUID(customerPayment.getCustomerUUID());
        return payment;
    }

    public LocalDateTime getDate(String dateString){
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        // Parse the string to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

        return dateTime;
    }

    public void validateAddToWalletRequest(AddToWalletDTO addToWalletDTO){
        if(addToWalletDTO.getAccountNo() == null || addToWalletDTO.getAccountNo().isEmpty() || addToWalletDTO.getAccountNo().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Account number can't be empty.",null);
        }
        if(addToWalletDTO.getPaymentGatewayName() == null || addToWalletDTO.getPaymentGatewayName().isEmpty() || addToWalletDTO.getPaymentGatewayName().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Payment Gateway name can't be empty.",null);
        }
        if(addToWalletDTO.getMobileNumber() == null || addToWalletDTO.getMobileNumber().isEmpty() || addToWalletDTO.getMobileNumber().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Mobile Number can't be empty.",null);
        }
        if(addToWalletDTO.getTransactionId() == null || addToWalletDTO.getTransactionId().isEmpty() || addToWalletDTO.getTransactionId().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "TransactionId can't be empty.",null);
        }
        if(addToWalletDTO.getAmount() == null || addToWalletDTO.getAmount() < 1){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Amount can't be zero.",null);
        }
    }

    public void processAddToWallet(AddToWalletDTO addToWalletDTO, String token) {
        logger.info("addToWallet method start");
        if (addToWalletDTO.getTransactionId() != null) {
                List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(addToWalletDTO.getTransactionId());
                if (!allByPgTransactionId.isEmpty()) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , addToWalletDTO.getTransactionId() + " can not be duplicate.", null);
                }
                AirtelAppToCRMDTO airtelAppToCRMDTO = new AirtelAppToCRMDTO();
                airtelAppToCRMDTO.setAccountNo(addToWalletDTO.getAccountNo());
                ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(airtelAppToCRMDTO, token);
                if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , addToWalletDTO.getAccountNo() + " can not be duplicate.Please connect the administrator.", null);
                }

                if (!customerByAccountNumber.getBody().isEmpty()) {
                    AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
                    CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                    Long orderId = generateId(customer.getCustId().longValue());
                    custPayDTOMessage.setOrderId(orderId);
                    custPayDTOMessage.setPgTransactionId(addToWalletDTO.getTransactionId());
                    if (customer.getCustId() != null)
                        custPayDTOMessage.setCustId(customer.getCustId());
                    if (addToWalletDTO.getAmount() != null)
                        custPayDTOMessage.setPayment(addToWalletDTO.getAmount());
                    if (addToWalletDTO.getTransactionId() != null)
                        custPayDTOMessage.setAccountNumber(addToWalletDTO.getTransactionId());
                    custPayDTOMessage.setStatus(AirtelValidateConstant.INITIATE);
                    custPayDTOMessage.setGatewayStatus(AirtelValidateConstant.INITIATE);
                    custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setMerchantName(addToWalletDTO.getPaymentGatewayName());
                    custPayDTOMessage.setPaymentGatewayName(addToWalletDTO.getPaymentGatewayName());
                    custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                    custPayDTOMessage.setCustomerUsername(customer.getUsername());
                    custPayDTOMessage.setMvnoid(customer.getMvnoId());
                    custPayDTOMessage.setBuid(customer.getBuId());
                    CustomerPayment customerPayment = new CustomerPayment();
                    customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//                    customerPayment.setId(getLatestId());
                    customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
                    customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
                    customerPayment.setPayerMobileNumber(addToWalletDTO.getMobileNumber());
                    customerPayment = customerPaymentRepository.save(customerPayment);
                    custPayDTOMessage.setId(customerPayment.getId());
                    boolean isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
                    if (isRecordSaved) {
                        paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), addToWalletDTO.getTransactionId(), "SUCCESSFUL", null);
                        customerPayment.setStatus(AirtelValidateConstant.SUCCESSFUL);
                        customerPaymentRepository.save(customerPayment);
                        custPayDTOMessage.setStatus(AirtelValidateConstant.SUCCESSFUL);
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send SUCCESSFUL Request of Wallet to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                    } else {
                        custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                        ApplicationLogger.logger.info("Send Failed Request of Wallet to CMS for referenceId: " + customerPayment.getOrderId());
                        kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                    }
                }
                else{
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), addToWalletDTO.getAccountNo() + " not found in system.", null);
                }
            }

    }


    public void validateAddPayment(ThirdPartyPaymentDTO thirdPartyPaymentDTO){
        if(thirdPartyPaymentDTO.getAccountNo() == null || thirdPartyPaymentDTO.getAccountNo().isEmpty() || thirdPartyPaymentDTO.getAccountNo().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Account number can't be empty.",null);
        }
        if(thirdPartyPaymentDTO.getPaymentGatewayName() == null || thirdPartyPaymentDTO.getPaymentGatewayName().isEmpty() || thirdPartyPaymentDTO.getPaymentGatewayName().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Payment Gateway name can't be empty.",null);
        }
        if(thirdPartyPaymentDTO.getMobileNumber() == null || thirdPartyPaymentDTO.getMobileNumber().isEmpty() || thirdPartyPaymentDTO.getMobileNumber().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Mobile Number can't be empty.",null);
        }
        if(thirdPartyPaymentDTO.getTransactionId() == null || thirdPartyPaymentDTO.getTransactionId().isEmpty() || thirdPartyPaymentDTO.getTransactionId().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "TransactionId can't be empty.",null);
        }
        if(thirdPartyPaymentDTO.getAmount() == null || thirdPartyPaymentDTO.getAmount() < 1){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Amount can't be zero.",null);
        }
        if(thirdPartyPaymentDTO.getPlanId() == null || thirdPartyPaymentDTO.getTransactionId().isEmpty() || thirdPartyPaymentDTO.getTransactionId().equals(" ")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan Id can't be empty.",null);
        }
    }

    public void processAddPayment(ThirdPartyPaymentDTO thirdPartyPaymentDTO, String token) {
        logger.info("addPayment method start");
        if (thirdPartyPaymentDTO.getTransactionId() != null) {
            List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(thirdPartyPaymentDTO.getTransactionId());
            if (!allByPgTransactionId.isEmpty()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , thirdPartyPaymentDTO.getTransactionId() + " can not be duplicate.", null);
            }
            if(thirdPartyPaymentDTO.getPlanId() !=null){
                boolean planExists = postpaidPlanRepository.existsById(thirdPartyPaymentDTO.getPlanId());
                if (!planExists) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), thirdPartyPaymentDTO.getPlanId()+ " This plan does not exist in the system.", null);
                }
            }
            AirtelAppToCRMDTO airtelAppToCRMDTO = new AirtelAppToCRMDTO();
            airtelAppToCRMDTO.setAccountNo(thirdPartyPaymentDTO.getAccountNo());
            ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(airtelAppToCRMDTO, token);
            if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , thirdPartyPaymentDTO.getAccountNo() + " can not be duplicate.Please connect the administrator.", null);
            }

            if (!customerByAccountNumber.getBody().isEmpty()) {
                AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
                CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
                Long orderId = generateId(customer.getCustId().longValue());
                custPayDTOMessage.setOrderId(orderId);
                if (customer.getCustId() != null)
                    custPayDTOMessage.setCustId(customer.getCustId());
                if (thirdPartyPaymentDTO.getAmount() != null)
                    custPayDTOMessage.setPayment(thirdPartyPaymentDTO.getAmount());
                if (thirdPartyPaymentDTO.getTransactionId() != null)
                    custPayDTOMessage.setAccountNumber(thirdPartyPaymentDTO.getAccountNo());
                custPayDTOMessage.setStatus(AirtelValidateConstant.INITIATE);
                custPayDTOMessage.setGatewayStatus(AirtelValidateConstant.INITIATE);
                custPayDTOMessage.setPaymentDate(LocalDateTime.now().toString());
                custPayDTOMessage.setMerchantName(thirdPartyPaymentDTO.getPaymentGatewayName());
                custPayDTOMessage.setPaymentGatewayName(thirdPartyPaymentDTO.getPaymentGatewayName());
                custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                custPayDTOMessage.setCustomerUsername(customer.getUsername());
                custPayDTOMessage.setMvnoid(customer.getMvnoId());
                custPayDTOMessage.setBuid(customer.getBuId());
                custPayDTOMessage.setPlanId(thirdPartyPaymentDTO.getPlanId());
                CustomerPayment customerPayment = new CustomerPayment();
                customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//                customerPayment.setId(getLatestId());
                customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
                customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());
                customerPayment.setPayerMobileNumber(thirdPartyPaymentDTO.getMobileNumber());
                customerPayment = customerPaymentRepository.save(customerPayment);
                custPayDTOMessage.setId(customerPayment.getId());
                boolean isRecordSaved = cmsClient.addCustomerPayment(custPayDTOMessage, token);
                if (isRecordSaved) {
                    paymentIntegrationService.updateStatusAndSendToCMS(orderId.toString(), thirdPartyPaymentDTO.getTransactionId(), "SUCCESSFUL", null);
                    customerPayment.setStatus(AirtelValidateConstant.SUCCESSFUL);
                    customerPaymentRepository.save(customerPayment);
                    custPayDTOMessage.setStatus(AirtelValidateConstant.SUCCESSFUL);
                    custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                    ApplicationLogger.logger.info("Send SUCCESSFUL Request of Wallet to CMS for referenceId: " + customerPayment.getOrderId());
                    kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                } else {
                    custPayDTOMessage.setTransactionDate(LocalDateTime.now().toString());
                    ApplicationLogger.logger.info("Send Failed Request of Wallet to CMS for referenceId: " + customerPayment.getOrderId());
                    kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
                }
            }
            else{
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), thirdPartyPaymentDTO.getAccountNo() + " not found in system.", null);
            }
        }

    }

    public Double getCustomerPendingAmount(String accountNo, String token) {
        logger.info("Pending amount method start");

            AirtelAppToCRMDTO airtelAppToCRMDTO = new AirtelAppToCRMDTO();
            airtelAppToCRMDTO.setAccountNo(accountNo);
            ResponseEntity<List<AirtelAppToCRMDTO>> customerByAccountNumber = cmsClient.getcustomersByAccountNumber(airtelAppToCRMDTO, token);
            if (customerByAccountNumber.getBody() != null && customerByAccountNumber.getBody().size() > 1) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , accountNo + " can not be duplicate.Please connect the administrator.", null);
            }

            if (!customerByAccountNumber.getBody().isEmpty()) {
                AirtelAppToCRMDTO customer = customerByAccountNumber.getBody().get(0);
                if ("PREPAID".equalsIgnoreCase(customer.getCusttype())) {
                    throw new CustomValidationException(HttpStatus.OK.value(), "No bills or invoices are available for prepaid users", null);
                }

                // Postpaid but billing not started
                Double  walletBalance = Double.parseDouble(customer.getWalletBalance());
                if ("POSTPAID".equalsIgnoreCase(customer.getCusttype()) &&  walletBalance == 0.0) {
                    throw new CustomValidationException(HttpStatus.OK.value(), "No pending invoices yet or billing is not started", null);
                }
                CustomerLedgerDtlsPojo customerLedgerDtlsPojo = new CustomerLedgerDtlsPojo();
                customerLedgerDtlsPojo.setCustId(customer.getCustId());
                ResponseEntity<?> responseEntity = revenueClient.fetchPendingAmount(customerLedgerDtlsPojo , token);
                Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
                Double amount = (Double) responseBody.get("customerWalletDetails");
                if(amount < 0){
                    amount = (amount)*-1;
                }
                else{
                    amount = 0.0000;
                }
                return amount;

            }
            else{
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), accountNo + " not found in system.", null);
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

    public void validateGetPlanListByparameter(ThirdPartyPlanFetchDTO thirdPartyPlanFetchDTO){
        if(thirdPartyPlanFetchDTO.getPlanGroupTypes() == null || thirdPartyPlanFetchDTO.getPlanGroupTypes().isEmpty()){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan group list can't be empty",null);
        }
        if(thirdPartyPlanFetchDTO.getSa() == null || thirdPartyPlanFetchDTO.getSa().isEmpty()){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service area list can't be empty",null);
        }
        if(thirdPartyPlanFetchDTO.getPlanType() == null || thirdPartyPlanFetchDTO.getPlanType().isEmpty()){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan type can't be empty",null);
        }
        if(!thirdPartyPlanFetchDTO.getPlanType().equalsIgnoreCase("Prepaid") && !thirdPartyPlanFetchDTO.getPlanType().equalsIgnoreCase("Postpaid")){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Plan type either Prepaid or Postpaid.",null);
        }
    }

    public List<LightPostpaidPlanDTO> getPlanListByparameter(ThirdPartyPlanFetchDTO thirdPartyPlanFetchDTO, String token){
        List<LightPostpaidPlanDTO> getPlanList = new ArrayList<>();
        ServiceAreaFetchDTO serviceAreaFetchDTO = new ServiceAreaFetchDTO();
        serviceAreaFetchDTO.setSa(thirdPartyPlanFetchDTO.getSa());
        serviceAreaFetchDTO.setPlanGroupTypes(thirdPartyPlanFetchDTO.getPlanGroupTypes());
        serviceAreaFetchDTO.setServiceIds(new ArrayList<>());
        ResponseEntity<?> response = cmsClient.fetchPlanByParameter(serviceAreaFetchDTO,token);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

            Object planListObj = responseBody.get("planList");
            List<LightPostpaidPlanDTO> postpaidPlanList = new ArrayList<>();

            if (planListObj instanceof List<?>) {
                ObjectMapper mapper = new ObjectMapper();
                List<?> rawList = (List<?>) planListObj;
                postpaidPlanList = rawList.stream()
                        .map(item -> mapper.convertValue(item, LightPostpaidPlanDTO.class))
                        .collect(Collectors.toList());
                postpaidPlanList = postpaidPlanList.stream().filter(lightPostpaidPlanDTO -> lightPostpaidPlanDTO.getPlantype().equalsIgnoreCase(thirdPartyPlanFetchDTO.getPlanType())).collect(Collectors.toList());
            }

            // Now you can use `postpaidPlanList`
            if (!postpaidPlanList.isEmpty()) {
                // process successful result
            } else {
                // handle empty list
            }
            getPlanList = postpaidPlanList;
        }
        else{

        }

        return getPlanList;
    }

    public CustomerPayment getCustomerPaymentBytransactionId(String transactionId){
        List<CustomerPayment> customerPaymentList = customerPaymentRepository.findAllByPgTransactionId(transactionId);
        if(customerPaymentList.size() > 1){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Multiple transaction found for transactionId.",null);
        }
        if(customerPaymentList.isEmpty()){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No transaction found for transactionId.",null);
        }
        return customerPaymentList.get(0);
    }




}
