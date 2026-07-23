package com.savbill.ticketmanagement.core.modules.PlanService.repository;

import com.savbill.ticketmanagement.core.modules.PlanService.domain.CustomerServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerServiceMappingRepository extends JpaRepository<CustomerServiceMapping, Integer>, QuerydslPredicateExecutor<CustomerServiceMapping> {

}
