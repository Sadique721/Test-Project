package com.savbill.integrationsystem.SOAPService.wsGetBalance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetBalanceDto {

    private Integer id;
    private Long planId;
    private Double totalQuota;
    private Long totalQuotaLong;
    private Double usedQuota;
    private Long usedQuotaLong;
    private Double currentSessionUsageVolume;
    private Long currentSessionUsageVolumeLong;
    private String usageQuotaType;
    private String service;
    private String uploadQuota;
    private String downloadQuota;
    private String quotaUnit;
    private String planName;
    private String planType;
    private Double currentSessionUsageTime;
    private Long currentSessionUsageTimeLong;
    private Long cprId;

}
