package com.savbill.integrationsystem.PaymentIntegration.Service;


import com.savbill.integrationsystem.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaConstant;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class PaymentToWalletService {

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Value("${mobile.prefix}")
    private String prefixlength;

    public GenericDataDTO addToWalletByOrderId(Long orderId, String transactionId){
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Transaction ID should not be empty", null);
        }
        List<CustomerPayment> allByPgTransactionId = customerPaymentRepository.findAllByPgTransactionId(transactionId);
        if (!allByPgTransactionId.isEmpty()) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Transaction ID already exists", null);
        }
        CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(orderId);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if(customerPayment != null){
            customerPayment.setStatus("Successful");
            customerPayment.setPaymentDate(LocalDateTime.now());
            customerPayment.setIsScheduled(true);
            customerPayment.setAutoPaymentInitiator(getLoggedInUser().getUsername());
            customerPayment.setPgTransactionId(transactionId);
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

            genericDataDTO.setResponseCode(200);
            genericDataDTO.setData("Transaction successfully");
        } else {
            genericDataDTO.setResponseCode(404);
            genericDataDTO.setData("Transaction not found for "+ orderId +" ");
        }
        return genericDataDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {

        }
        return loggedInUser;
    }

    public String getGatewayfromMobileNumber(String mobileNumber) throws JsonProcessingException {
        String prefix = getPrefixFromMobileNumber(mobileNumber);
        String gateway = getGatewayFromPrefix(getLoggedInUser().getMvnoId() , prefix);
        return gateway;
    }

    public String getGatewayFromPrefix(Integer mvnoId , String prefix) throws JsonProcessingException {
        String gateway = null;
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String , String> getPaymentGatewayParameterForGatewateway = paymentConfigService.getPaymentGatewayParameterForGatewateway(mvnoId);
        String MOMO_PREFIX = getPaymentGatewayParameterForGatewateway.getOrDefault(
                PaymentGatewayConfigurationConstant.MOMOPE.MOMO_PAY_PREFIX,
                null
        );
        String AIRTEL_PREFIX = getPaymentGatewayParameterForGatewateway.getOrDefault(
                PaymentGatewayConfigurationConstant.AIRTEL.AIRTEL_PREFIX,
                null
        );

        if(MOMO_PREFIX == null && AIRTEL_PREFIX == null){
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Payment prefix is missing or configuration for Airtel/MoMoPay is not set.",null);
        }


        if(MOMO_PREFIX != null){
            List<String> momoPrefixes = objectMapper.readValue(MOMO_PREFIX, List.class);
            if (momoPrefixes.contains(prefix)) {
                gateway = PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY;
            }
        }

        if(AIRTEL_PREFIX != null){
            List<String> momoPrefixes = objectMapper.readValue(AIRTEL_PREFIX, List.class);
            if (momoPrefixes.contains(prefix)) {
                gateway = PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.AIRTEL;
            }
        }

        if(gateway == null){
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mobile number is not match with any configured prefix",null);
        }

        return gateway;

    }

    public String getPrefixFromMobileNumber(String mobilenumber){
       String prefix = null;
       if(mobilenumber == null || mobilenumber.isEmpty()){
           throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Mobile number cant be empty or null.",null);
       }
       if(!mobilenumber.matches("\\d+")){
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Please enter valid value.",null);
       }
       if(mobilenumber.length() < Integer.parseInt(prefixlength)){
           throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Mobile Number is too short.",null);
       }
       prefix = mobilenumber.substring(0,Integer.parseInt(prefixlength));
       return prefix;

    }
}
