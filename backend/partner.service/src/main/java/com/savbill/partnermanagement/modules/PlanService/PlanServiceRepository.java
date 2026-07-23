package com.savbill.partnermanagement.modules.PlanService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface PlanServiceRepository extends JpaRepository<PlanService, Integer>  , QuerydslPredicateExecutor<PlanService> {
}
