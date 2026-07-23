package com.savbill.inventorymanagement.modules.PostpaidPlanServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostPaidPlanServiceAreaMappingRepo extends JpaRepository<PostPaidPlanServiceAreaMapping, Integer>, QuerydslPredicateExecutor<PostPaidPlanServiceAreaMapping> {
}
