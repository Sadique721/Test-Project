package com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.service;

import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.domain.CustomerOnlinePaymentAudit;
import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.repository.CustomerOnlinePaymentAuditRepository;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.dto.customer.CustPayDTOMessage;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
public class CustomerOnlinePaymentAuditService {


    private final CustomerOnlinePaymentAuditRepository customerOnlinePaymentAuditRepository;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public CustomerOnlinePaymentAuditService(CustomerOnlinePaymentAuditRepository customerOnlinePaymentAuditRepository) {
        this.customerOnlinePaymentAuditRepository = customerOnlinePaymentAuditRepository;
    }

    public CustomerOnlinePaymentAudit convertMessageToEntity(CustPayDTOMessage custPayDTOMessage){
        CustomerOnlinePaymentAudit payment = new CustomerOnlinePaymentAudit();
        payment.setId(custPayDTOMessage.getId());
        payment.setPayment(custPayDTOMessage.getPayment());
        payment.setCustId(custPayDTOMessage.getCustId());
        payment.setMvnoid(custPayDTOMessage.getMvnoid());
        payment.setStatus(custPayDTOMessage.getStatus());
        payment.setPaymentDate(LocalDateTime.parse(custPayDTOMessage.getPaymentDate(),formatter));
        payment.setTransactionDate(LocalDateTime.parse(custPayDTOMessage.getTransactionDate(),formatter));
        if(custPayDTOMessage.getBuid() != null) {
            payment.setBuid(custPayDTOMessage.getBuid());
        }
        payment.setCustomerUsername(custPayDTOMessage.getCustomerUsername());
        if(custPayDTOMessage.getCreditDocumentId() != null) {
            payment.setCreditDocumentId(custPayDTOMessage.getCreditDocumentId());
        }
        payment.setPlanId(custPayDTOMessage.getPlanId());
        payment.setOrderId(custPayDTOMessage.getOrderId());
        payment.setPgTransactionId(custPayDTOMessage.getPgTransactionId());
        payment.setPartnerId(custPayDTOMessage.getPartnerId());
        payment.setCreatedById(custPayDTOMessage.getCreatedById());
        payment.setCreatedByName(custPayDTOMessage.getCreatedByName());
        payment.setAccountNumber(custPayDTOMessage.getAccountNumber());
        return payment;
    }

    public void saveOrUpdateOnlinePayment(CustPayDTOMessage dataMessage) {
        try {
            log.info("Starting saveOrUpdateOnlinePayment for orderId: {}",
                    dataMessage != null ? dataMessage.getOrderId() : null);
            if(dataMessage != null && dataMessage.getOrderId() != null) {
                CustomerOnlinePaymentAudit onlinePaymentAudit = customerOnlinePaymentAuditRepository.findByOrderId(dataMessage.getOrderId());
                if(onlinePaymentAudit != null) {
                       //updating online payment audit
                       log.info("updating customer online payment audit for orderId: {};", dataMessage.getOrderId());
                       if(dataMessage.getPayment() != null){
                           onlinePaymentAudit.setPayment(dataMessage.getPayment());
                       }
                       if (dataMessage.getStatus() != null) {
                           onlinePaymentAudit.setStatus(dataMessage.getStatus());
                       }
                       if (dataMessage.getPaymentDate() != null) {
                           onlinePaymentAudit.setPaymentDate(LocalDateTime.parse(dataMessage.getPaymentDate(),formatter));
                       }
                       if (dataMessage.getTransactionDate() != null) {
                           onlinePaymentAudit.setTransactionDate(LocalDateTime.parse(dataMessage.getTransactionDate(),formatter));
                       }
                       if(dataMessage.getPgTransactionId() != null) {
                           onlinePaymentAudit.setPgTransactionId(dataMessage.getPgTransactionId());
                       }
                }else{
                       //saving online payment Audit
                       log.info("saving customer online payment audit for orderId: {};", dataMessage.getOrderId());
                       dataMessage.setId(getLatestId());
                       onlinePaymentAudit = convertMessageToEntity(dataMessage);
                }
                customerOnlinePaymentAuditRepository.save(onlinePaymentAudit);
                log.info("customer online payment saved/updated successfully");
               }else {
                log.error("Customer online payment audit received null or missing orderId. Data: {}", dataMessage);
                   throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"orderId is null",null);
               }
        } catch (CustomValidationException e) {
            log.error("CustomValidationException in saveOrUpdateOnlinePayment for orderId: {}",
                    dataMessage != null ? dataMessage.getOrderId() : null, e);
            log.error("customer online payment audit error", e);
        }
    }

    public Long getLatestId(){
        Long latestId = 0L;
        latestId = customerOnlinePaymentAuditRepository.getLatestId();
        if(Objects.isNull(latestId)){
            latestId = 1L;
        }
        else {
            latestId = latestId+1L;
        }
        return latestId;
    }
}
