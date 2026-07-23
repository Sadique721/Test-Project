package com.savbill.integrationsystem.kbzIntegration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KbzPayRequest {

    private KbzPayPayload kbzPayPayload;
    @JsonProperty("appid")
    private String secretKey;// appId for KbzPay
    private String gatewayUrl;
    private String callBackUrl;
    private String scheduleTime;
    private String appKey;
    private String paymentUrl;
    private String savbillDomainUrl;
//    private String payload;
}
