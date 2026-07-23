package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentDetails {
    @JsonProperty("PaymentDate")
    String paymentDate;

    @JsonProperty("Amount")
    String amount;

    @JsonProperty("ReceiptNo")
    String receiptNo;

    @JsonProperty("InvNo")
    String invNo;

    @JsonProperty("Mode")
    String mode;

    @JsonProperty("Status")
    String status;

    @JsonProperty("PaymentStatus")
    String paymentStatus;

}
