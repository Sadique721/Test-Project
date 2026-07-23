package com.savbill.integrationsystem.PaymentIntegration.Model;

import com.savbill.integrationsystem.PaymentIntegration.DTO.CustomerPaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayStackPojo {

    private String email;

    private String amount;

    private String reference;

    private String gatewayUrl;

    private String callBackUrl;

    private String verifyUrl;

    private String currency;

    private String scheduleTime;

    private String secretKey;

    private String publicKey;

    private CustomerPaymentDTO customerPaymentDTO;

    public PayStackPojo(String gatewayUrl, String callBackUrl, String verifyUrl, String scheduleTime, String secretKey, String publicKey) {
        this.gatewayUrl = gatewayUrl;
        this.callBackUrl = callBackUrl;
        this.verifyUrl = verifyUrl;
        this.scheduleTime = scheduleTime;
        this.secretKey = secretKey;
        this.publicKey = publicKey;
    }
}
