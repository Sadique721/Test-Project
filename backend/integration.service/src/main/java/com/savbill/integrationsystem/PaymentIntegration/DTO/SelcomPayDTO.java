package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelcomPayDTO {

    private String gatewayUrl;

    private String callBackUrl;

    private String jsonPayload;

    private String apiKey;

    private String secretKey;

    private String scheduleTime;


    public SelcomPayDTO( String apiKey, String secretKey,String gatewayUrl, String jsonPayload, String scheduleTime) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.gatewayUrl = gatewayUrl;
        this.jsonPayload = jsonPayload;
        this.scheduleTime = scheduleTime;
    }
}
