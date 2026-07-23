package com.savbill.integrationsystem.PaymentIntegration.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhonePePayment {



    private String merchantId;

    private String merchantTransactionId;

    private String merchantUserId;

    private Long amount;

    private String redirectUrl;

    private String redirectMode;

    private String callbackUrl;

    private String mobileNumber;

    private PaymentInstrument paymentInstrument;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentInstrument{
        private String type;
    }



}
