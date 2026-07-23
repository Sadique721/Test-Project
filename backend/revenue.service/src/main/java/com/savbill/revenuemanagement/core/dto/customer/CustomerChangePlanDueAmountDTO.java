package com.savbill.revenuemanagement.core.dto.customer;

import lombok.Data;

@Data
public class CustomerChangePlanDueAmountDTO {

    private Integer custPackRelId;

    private Integer custId;

    private Double newPlanPrice;

    private Double oldPlanPrice;

    private Double newPlanGroupPrice;

    private Integer oldPlanGroupId;

    private Integer newPlanGroupId;

    private Double discount;

    private Integer discountPercentage;

    private Integer newPlanId;

    private Integer oldPlanId;

    private String changePlanBillingCycle;

    private String purchaseType;



}
