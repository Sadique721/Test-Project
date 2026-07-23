package com.savbill.radius.repository;

import com.savbill.radius.entity.PlanUsagePercentageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanUsagePercentageMappingRepository extends JpaRepository<PlanUsagePercentageMapping , Long> {
    List<PlanUsagePercentageMapping> findAllByPlanId(Integer planid);
    PlanUsagePercentageMapping findByPlanIdAndLevel(Integer planid , Integer level);
}
