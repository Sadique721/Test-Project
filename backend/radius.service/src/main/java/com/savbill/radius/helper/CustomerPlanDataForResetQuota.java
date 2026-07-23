package com.savbill.radius.helper;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerPlanDataForResetQuota {
    private Long cprId; //custpackageid
    private LocalDateTime startDate; //startdate
    private LocalDateTime endDate; //enddate
    private Integer planId; //POSTPAIDPLANID
    private String unitsofvalidity; //unitsofvalidity
    private String quotarestinterval; //quotarestinterval
    private Integer validity; //validity
    private String planName; //NAME
    private CustomerQuotaDataForReset customerQuotaData;
}
