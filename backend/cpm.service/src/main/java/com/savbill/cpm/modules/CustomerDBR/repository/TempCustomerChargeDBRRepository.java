package com.savbill.cpm.modules.CustomerDBR.repository;

import com.savbill.cpm.modules.CustomerDBR.domain.TempCustomerChargeDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TempCustomerChargeDBRRepository extends JpaRepository<TempCustomerChargeDBR, Long>, QuerydslPredicateExecutor<TempCustomerChargeDBR> {
}