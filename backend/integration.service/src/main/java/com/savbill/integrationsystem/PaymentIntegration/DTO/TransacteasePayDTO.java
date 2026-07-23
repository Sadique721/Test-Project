package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransacteasePayDTO {

    private String accessKey;
    private String allowedPaymentMethods;
    private String secretKey;
    private String channel;
//    private String clientCredientials;
    private String expiredTimeInSeconds;
    private String insId;
    private String merchantId;
    private String requestUrl;
    private String redirectTimeInSeconds;
    private String callbackUrl;
    private String redirectUrl;
    private String scheduleTime;
    private String currency;
    private String clientSecret;
    private String country;

    public TransacteasePayDTO(String accessKey, String allowedPaymentMethods, String secretKey, String channel,  String expiredTimeInSeconds, String insId, String merchantId, String requestUrl, String redirectTimeInSeconds, String callbackUrl, String redirectUrl, String scheduleTime,String currency,String clientSecret,String country) {
        this.accessKey = accessKey;
        this.allowedPaymentMethods = allowedPaymentMethods;
        this.secretKey = secretKey;
        this.channel = channel;
//        this.clientCredientials = clientCredientials;
        this.expiredTimeInSeconds = expiredTimeInSeconds;
        this.insId = insId;
        this.merchantId = merchantId;
        this.requestUrl = requestUrl;
        this.redirectTimeInSeconds = redirectTimeInSeconds;
        this.callbackUrl = callbackUrl;
        this.redirectUrl = redirectUrl;
        this.scheduleTime = scheduleTime;
        this.currency = currency;
        this.clientSecret = clientSecret;
        this.country = country;
    }
}
