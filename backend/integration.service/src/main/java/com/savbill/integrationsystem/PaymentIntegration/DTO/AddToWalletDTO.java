package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

@Data
public class AddToWalletDTO {

    private String accountNo;

    private String transactionId;

    private String paymentGatewayName;

    private String mobileNumber;

    private Double amount;
}
