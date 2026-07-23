package com.savbill.integrationsystem.commonMethods;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.kafka.KafkaMessageData;
import com.savbill.integrationsystem.kafka.KafkaMessageSender;
import com.savbill.integrationsystem.rabbitmq.CustPayDTOMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

@Component
public class IntegrationGenericMethods {

    @Autowired
    private CustomerPaymentService customerPaymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;
    private static final Logger logger = LoggerFactory.getLogger(IntegrationGenericMethods.class);


    public CustomerPayment sendAndSaveDataForPayment(CustomerPaymentDTO customerPaymentDTO, String status) {
        try {
            logger.info(" Inside save payment data in customerPayment and send custPayDTOMessage to CMS ");
            CustPayDTOMessage custPayDTOMessage = new CustPayDTOMessage();
            Long orderId = Long.valueOf(generateId(customerPaymentDTO.getCustomerId().longValue()));
            if(customerPaymentDTO.getOrderId() != null && !customerPaymentDTO.getOrderId().isEmpty()) {
                custPayDTOMessage.setOrderId(Long.parseLong(customerPaymentDTO.getOrderId()));
            }else {
                custPayDTOMessage.setOrderId(orderId);
            }
            if (customerPaymentDTO.getCustomerId() != null)
                custPayDTOMessage.setCustId(customerPaymentDTO.getCustomerId());
            if (customerPaymentDTO.getPartnerId() != null)
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            if (customerPaymentDTO.getAccountNumber() != null)
                custPayDTOMessage.setAccountNumber(customerPaymentDTO.getAccountNumber());
            if(customerPaymentDTO.getActualAmount() != null){
                custPayDTOMessage.setPayment(Double.valueOf(customerPaymentDTO.getActualAmount()));
            } else {
                custPayDTOMessage.setPayment(Double.valueOf(customerPaymentDTO.getAmount()));
            }
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
            custPayDTOMessage.setPayerMobileNumber(customerPaymentDTO.getPayerMobileNumber());
            if(customerPaymentDTO.getWalletAmount() != null){
                custPayDTOMessage.setWalletAmount(customerPaymentDTO.getWalletAmount());
            }
            if (customerPaymentDTO.getCustServiceMappingId() != null) {
                custPayDTOMessage.setCustServiceMappingId(customerPaymentDTO.getCustServiceMappingId());
            }
            customerPaymentDTO.setOrderId(orderId.toString());
            if (customerPaymentDTO.getPartnerId() != null) {
                custPayDTOMessage.setPartnerId(customerPaymentDTO.getPartnerId());
            }
            if(customerPaymentDTO.getChildId() != null){
                custPayDTOMessage.setChildId(customerPaymentDTO.getChildId());
            }
            custPayDTOMessage.setCustomerUUID(customerPaymentDTO.getCustomerUUID());
            CustomerPayment customerPayment = customerPaymentService.convertMessageToEntity(custPayDTOMessage);
//            customerPayment.setId(getLatestId());
            if (customerPaymentDTO.getInvoiceId() != null) {
                customerPayment.setInvoiceId(customerPaymentDTO.getInvoiceId());
            }
            if (customerPaymentDTO.getIsAdvancePayment() != null) {
                customerPayment.setIsAdvancePayment(customerPaymentDTO.getIsAdvancePayment());
            }
            if (customerPaymentDTO.getPayerMobileNumber() != null) {
                customerPayment.setPayerMobileNumber(customerPaymentDTO.getPayerMobileNumber());
            }
            if(customerPaymentDTO.getCommission()!=null){
                customerPayment.setCommission(customerPaymentDTO.getCommission());
            }
            customerPayment.setCreatedById(jwtUtil.getLoggedInUser().getUserId());
            customerPayment.setCreatedByName(jwtUtil.getLoggedInUser().getUsername());

            ApplicationLogger.logger.info("Send Initiated Request to CMS for referenceId: " + customerPayment.getCustomerUUID());
            kafkaMessageSender.send(new KafkaMessageData(custPayDTOMessage, custPayDTOMessage.getClass().getSimpleName()));
            customerPayment = customerPaymentRepository.save(customerPayment);
            return customerPayment;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error While Sending Data to CMS Through Kafka. ", e.getMessage());
            return null;
        }
    }

    public String generateId(Long customerId) {
        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmmss"));
        return String.valueOf(id);
    }

    public Long getLatestId() {
        Long latestId = 0L;
        latestId = customerPaymentRepository.getLatestId();
        if (Objects.isNull(latestId)) {
            latestId = 1L;
        } else {
            latestId = latestId + 1L;
        }
        return latestId;
    }



    public String base64UrlEncode(String input) throws UnsupportedEncodingException {
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes("UTF-8"));
        String encoded = new String(encodedBytes, "UTF-8");
        return encoded
                .replace('+', '-')
                .replace('/', '_')
                .replace("=", "");
    }
}
