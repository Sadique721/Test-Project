package com.savbill.inventorymanagement.modules.PlanGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface PlanGroupRepository extends JpaRepository<PlanGroup, Integer>, QuerydslPredicateExecutor<PlanGroup> {
}
