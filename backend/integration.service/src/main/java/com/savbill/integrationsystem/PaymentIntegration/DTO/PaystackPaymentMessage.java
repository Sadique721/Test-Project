package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

@Data
public class PaystackPaymentMessage {

    Integer customerId;

    String paymentStatus;

    String referenceNumber;

    Integer creditDocId;

    public PaystackPaymentMessage(Integer customerId, String paymentStatus, String referenceNumber, Integer creditDocId) {
        this.customerId = customerId;
        this.paymentStatus = paymentStatus;
        this.referenceNumber = referenceNumber;
        this.creditDocId = creditDocId;
    }
}
