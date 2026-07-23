package com.savbill.inventorymanagement.modules.PlanGroupServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanGroupServiceAreaMappingRepo extends JpaRepository<PlanGroupServiceAreaMapping, Integer>, QuerydslPredicateExecutor<PlanGroupServiceAreaMapping> {
}
