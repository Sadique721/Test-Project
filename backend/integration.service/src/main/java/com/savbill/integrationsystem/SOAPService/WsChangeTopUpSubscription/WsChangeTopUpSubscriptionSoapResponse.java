package com.savbill.integrationsystem.SOAPService.WsChangeTopUpSubscription;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WsChangeTopUpSubscriptionSoapResponse {

    private LocalDateTime endTime;
    private String subscriberIdentity;
    private Integer topUpId;
    private String topUpName;
    private String topUpStatus;
    private Integer topUpSubscriptionId;
    private LocalDateTime usageResetTime;
}
