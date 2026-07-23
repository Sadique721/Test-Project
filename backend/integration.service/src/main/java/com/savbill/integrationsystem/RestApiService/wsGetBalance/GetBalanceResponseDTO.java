package com.savbill.integrationsystem.RestApiService.wsGetBalance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetBalanceResponseDTO {

    private String parameter1;
    private String parameter2;
    private int responseCode;
    private String responseMessage;
    private long requestId;

    private String packageId;
    private String packageName;
    private String packageType;
    private int carryForword;

    private String quotaProfileId;
    private String quotaProfileName;


    private String aggregationKey;
    private String serviceId;
    private String serviceName;

    private String downloadOctetsBalance;
    private String timeBalance;
    private String totalOctetsBalance;
    private String uploadOctetsBalance;

    private String downloadOctetsCurrentUsage;
    private String timeCurrentUsage;
    private String totalOctetsCurrentUsage;
    private String uploadOctetsCurrentUsage;

    private String downloadOctetsHSQLimit;
    private String timeHSQLimit;
    private String totalOctetsHSQLimit;
    private String uploadOctetsHSQLimit;
}
