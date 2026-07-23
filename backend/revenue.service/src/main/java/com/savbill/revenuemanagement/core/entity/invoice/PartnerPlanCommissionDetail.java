package com.savbill.revenuemanagement.core.entity.invoice;


import lombok.Data;

import java.util.List;

@Data
public class PartnerPlanCommissionDetail {
    private List<PartnerPlanWiseCommission> partnerPlanWiseCommissionList;
}
