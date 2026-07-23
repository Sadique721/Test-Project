package com.savbill.revenuemanagement.core.entity.partner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanCommissionPojo {
    private Integer id;
    private Boolean isPlanGroup;
    private Double agrPercentage;
    private Double revenueSharePercentage;
    private Integer partnerTaxId;
    private Double tdsPercentage;
}
