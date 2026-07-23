package com.savbill.revenuemanagement.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBResult {

    private Integer id;

    private String username;

    private boolean isRoyaltyApply;

    private String planGroupName;

    private boolean isFirstChargeApply;

    private Integer historyId;

    private String saccode;

    private Integer billingCycle;

    private Integer chargeType;

    private String planName;

    private String chargeName;

    private String chargeDesc;

    private Integer planId;

    private Integer custpackageid;

    private LocalDateTime startdate;

    private LocalDateTime expirydate;

    private Integer planValidityDays;


}
