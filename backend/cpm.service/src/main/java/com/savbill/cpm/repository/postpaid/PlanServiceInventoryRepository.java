package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.PlanServiceInventoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PlanServiceInventoryRepository extends JpaRepository<PlanServiceInventoryMapping,Long>, QuerydslPredicateExecutor<PlanServiceInventoryMapping> {
}
