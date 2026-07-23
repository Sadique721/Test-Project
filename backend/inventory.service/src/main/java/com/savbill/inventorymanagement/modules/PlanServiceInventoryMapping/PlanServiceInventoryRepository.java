package com.savbill.inventorymanagement.modules.PlanServiceInventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanServiceInventoryRepository extends JpaRepository<PlanServiceInventoryMapping,Long>, QuerydslPredicateExecutor<PlanServiceInventoryMapping> {
    List<PlanServiceInventoryMapping> findAllByPlanService_Id(Integer planService_id);
}
