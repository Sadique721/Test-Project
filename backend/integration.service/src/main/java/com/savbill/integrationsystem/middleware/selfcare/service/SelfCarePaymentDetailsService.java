package com.savbill.integrationsystem.middleware.selfcare.service;

import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;
import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.entity.DebitDocument;
import com.savbill.integrationsystem.billgen.repository.CreditDocRepocitory;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.billgen.repository.DebitDocumentRepo;
import com.savbill.integrationsystem.core.exceptions.CustomValidationException;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.middleware.selfcare.model.PaymentDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SelfCarePaymentDetailsService {

    @Autowired
    DebitDocumentRepo debitDocumentRepo;

    @Autowired
    CreditDocRepocitory creditDocRepocitory;

    @Autowired
    CustomerRepository customerRepository;

    public ArrayList<PaymentDetails> getPaymentDetailsByUserName(String userName) {
        Integer customerId;
        try {
            ArrayList<PaymentDetails> subscriberPaymentDetails = new ArrayList<PaymentDetails>();
            CustomerData customer = customerRepository.findByUsername(userName);
            if (customer != null) {
                customerId = customer.getId();
                List<CreditDocumentData> customerPaymentList = creditDocRepocitory.findAllByCustomerAndType(customerId, APIConstants.PAYTYPE);
                if (customerPaymentList != null) {
                    for (CreditDocumentData creditDocPayment : customerPaymentList) {
                        PaymentDetails selfCarePaymentDetails = new PaymentDetails();
                        selfCarePaymentDetails.setPaymentDate(convertToUTC(creditDocPayment.getCreatedate()).toString());
                        selfCarePaymentDetails.setStatus(APIConstants.COMPLETED);
                        selfCarePaymentDetails.setPaymentStatus(converPaymentStatus(creditDocPayment.getStatus()));
                        selfCarePaymentDetails.setAmount("Cr " + creditDocPayment.getAmount().toString());
                        selfCarePaymentDetails.setReceiptNo(creditDocPayment.getReciptNo());
                        selfCarePaymentDetails.setMode(creditDocPayment.getPaymode());
                        selfCarePaymentDetails.setInvNo(getInvoiceNoFromCreditDoc(creditDocPayment.getInvoiceId()));
                        subscriberPaymentDetails.add(selfCarePaymentDetails);
                    }
                }
            }
            return subscriberPaymentDetails;

        } catch (CustomValidationException ce) {
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ce.getMessage(), null);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    String getInvoiceNoFromCreditDoc(Integer invoiceId) {
        String invoiceNumber = "";
        DebitDocument debitDocument = null;
        debitDocument = debitDocumentRepo.findById(invoiceId).orElse(null);
        if (debitDocument != null) {
            invoiceNumber = debitDocument.getDebitdocumentnumber();
            if(invoiceNumber != null && !invoiceNumber.trim().isEmpty()){
                return invoiceNumber;
            }
            else{
                invoiceNumber = APIConstants.NA;
                return invoiceNumber;
            }
        }
        else{
            invoiceNumber = APIConstants.NA;
            return invoiceNumber;
        }
    }

    private LocalDateTime convertToUTC(String paymentDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(paymentDate, formatter);
            ZonedDateTime zoneLocalTime = dateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime zoneUtcTime = ZonedDateTime.ofInstant(zoneLocalTime.toInstant(), ZoneId.of("UTC"));
            return zoneUtcTime.toLocalDateTime();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(APIConstants.SELFCARE + ex.getMessage(), ex);
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, ex.getMessage(), null);
        }
    }

    String converPaymentStatus(String paymentStatus) {
        String newPaymentStatus = "";
        if(paymentStatus.equals(APIConstants.FULLY_ADJUSTED)
                || paymentStatus.equals(APIConstants.PARTIALY_ADJUSTED)
                || paymentStatus.equals(APIConstants.APPROVED)){
            newPaymentStatus = APIConstants.VERIFIED;
            return newPaymentStatus;
        }
        else{
            newPaymentStatus = StringUtils.capitalize(paymentStatus);
            return newPaymentStatus;
        }
    }
}
