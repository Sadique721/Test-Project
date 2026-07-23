package com.savbill.inventorymanagement.modules.CustPlanMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CustPlanMapppingRepository extends JpaRepository<CustPlanMappping, Integer>, QuerydslPredicateExecutor<CustPlanMappping> {

}
