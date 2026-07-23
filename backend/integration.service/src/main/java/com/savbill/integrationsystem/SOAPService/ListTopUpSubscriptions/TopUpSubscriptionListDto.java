package com.savbill.integrationsystem.SOAPService.ListTopUpSubscriptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TopUpSubscriptionListDto {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    private String subscriberIdentity;
    private Integer topUpId;
    private String topUpName;
    private String topUpStatus;
    private Long topUpSubscriptionId;
    private LocalDateTime usageResetTime;
}
