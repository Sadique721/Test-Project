package com.savbill.radius.SoapApi.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsGetBalanceRequestDTO {

    private String usageResetTime;

    private String subscriberId;

    private String packageId;

    private String packageName;

    private String packageType;

    private String balance;

    private String downloadOctet;

    private String uploadOctet;

    private String totalOctet;

    private String time;

    private String currentUsage;

    private String hsqLimit;

    private String serviceId;

    private String serviceName;

    private String quotaProfileId;

    private String quotaProfileName;

    private String addOnStatus;

    private String addonSubscriptionId;

    private String endTime;

    private String startTime;
    private String quotaUnit;
    private Double currentSessionUsageVolume;
    private Double usedQuota;
    private Double totalQuota;
    private Long cprId;

}

