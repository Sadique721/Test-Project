package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.BatchPaymentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchPaymentMappingRepository extends JpaRepository<BatchPaymentMapping, Long>, QuerydslPredicateExecutor<BatchPaymentMapping> {
}
