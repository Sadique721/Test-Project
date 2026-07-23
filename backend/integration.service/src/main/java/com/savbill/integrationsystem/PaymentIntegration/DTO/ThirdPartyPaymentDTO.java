package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

@Data
public class ThirdPartyPaymentDTO {

    private String accountNo;

    private String transactionId;

    private String paymentGatewayName;

    private String mobileNumber;

    private Double amount;

    private Integer planId;
}
