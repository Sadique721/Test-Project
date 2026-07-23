package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnePayDto {

    private String gatewayUrl;

    private String callBackUrl;

    private String jsonPayload;

    private String secretKey;

    private String scheduleTime;

    private String channel;

    private String merchantUserId;

    private String onepayPhoneNo;


}
