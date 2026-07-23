package com.savbill.radius.services;

import com.savbill.radius.entity.PlanUsagePercentageMapping;

import java.util.List;

public interface PlanUsagePercentageMappingService {

    List<PlanUsagePercentageMapping> findPlanUsageMappingByPlanId(Integer planid);

    PlanUsagePercentageMapping getPlanUsageMappinglevelBylevel(Integer planid , Integer level);

}
