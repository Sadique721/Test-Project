package com.savbill.revenuemanagement.core.entity.invoice;

import lombok.Data;

@Data
public class PartnerPlanWiseCommission {
    private String planOrPlanGroupName;
    private Boolean isPlanGroup;
    private Long totalCustomerCount;
    private Double commissionAmount;
}
