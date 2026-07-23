package com.savbill.integrationsystem.SOAPService.WsChangeAddOnSubscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WsChangeAddOnSubscriptionResponseDto {

    private Integer addOnId;
    private String addOnName;
    private String addOnStatus;
    private Integer addonSubscriptionId;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endTime;
    private String parameter1;
    private String parameter2;
    private String subscriberIdentity;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime usageResetTime;
}
