package com.savbill.revenuemanagement.core.entity.partner;

import com.savbill.revenuemanagement.core.dto.invoice.ServiceLevelCommission;
import lombok.Data;

import java.util.List;

@Data
public class PartnerLedgerDetailsServiceLevelDTO {
    private Integer serialNo;
    private String planOrPlanGroupName;
    private Boolean isPlanGroup;
    private Integer planOrPlanGroupId;
    private Double planOrPlanGroupPrice;
    private Double basePlanOrPlanGroupPrice;
    private Long customerCount;
    private Double totalSale;
    private Double CommissionSharePercentage;
    private Double netCommission;
    private Double totalCommission;
    private Double totalPlanCommission;
    private String transcategory;
    private Double credit;
    private Double debit;
    private Double amount;
    private Double balAmount;
    private Double partnerTaxId;
    private Double agrPercentage;
    private Double tdsPercentage;
    private Double revenueSharePercentage;
    private Integer serviceId;
    private String serviceName;
    private String transType;
    private PlanCommissionDetailList commissionDetailList;
    List<ServiceLevelCommission> serviceLevelCommissions;
}
