package com.savbill.revenuemanagement.core.entity.partner;

import lombok.Data;

@Data
public class PlanCommissionDetail {
    private Integer planGroupId;
    private String planGroupName;
    private Integer PlanId;
    private String planName;
    private Double grossOfferPrice;
    private Double grossTaxAmount;
    private Double grossBaseOfferPrice;
    private Double offerPrice;
    private Double taxAmount;
    private Double baseOfferPriceExcludeAgr;
    private Double agrAmount;
    private Double netCommission;
    private Double partnerTaxAmount;
    private Double tdsAmount;
    private Double payableCommission;
    private Long customerCount;
}
