package com.savbill.radius.SoapApi.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AddOnSubscriptionListDto {

    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private String subscriberIdentity;
    private Integer addOnId;
    private String addOnName;
    private String addOnStatus;
    private Long addonSubscriptionId;
    private LocalDateTime usageResetTime;
}
