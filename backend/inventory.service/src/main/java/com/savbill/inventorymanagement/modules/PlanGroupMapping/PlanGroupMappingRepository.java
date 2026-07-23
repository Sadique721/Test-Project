package com.savbill.inventorymanagement.modules.PlanGroupMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface PlanGroupMappingRepository extends JpaRepository<PlanGroupMapping, Integer>, QuerydslPredicateExecutor<PlanGroupMapping> {
}
