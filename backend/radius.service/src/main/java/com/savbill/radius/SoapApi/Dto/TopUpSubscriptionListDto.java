package com.savbill.radius.SoapApi.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TopUpSubscriptionListDto {

    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private String subscriberIdentity;
    private Integer topUpId;
    private String topUpName;
    private String topUpStatus;
    private Long topUpSubscriptionId;
    private LocalDateTime usageResetTime;
}
