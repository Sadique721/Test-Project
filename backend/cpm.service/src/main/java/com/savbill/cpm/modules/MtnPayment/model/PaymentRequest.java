package com.savbill.cpm.modules.MtnPayment.model;


import lombok.Data;

@Data
public class PaymentRequest {

    private String amount;
    private String transactionId;

    private String currency;

    private String fromfri;
}
