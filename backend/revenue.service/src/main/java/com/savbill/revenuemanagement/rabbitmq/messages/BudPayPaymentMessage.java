package com.savbill.revenuemanagement.rabbitmq.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BudPayPaymentMessage {

    Integer customerId;

    String paymentStatus;

    String referenceNumber;

    Integer creditDocId;


    public BudPayPaymentMessage(Integer customerId, String referenceNumber, Integer creditDocId) {
        this.customerId = customerId;
        this.referenceNumber = referenceNumber;
        this.creditDocId = creditDocId;
    }
}
