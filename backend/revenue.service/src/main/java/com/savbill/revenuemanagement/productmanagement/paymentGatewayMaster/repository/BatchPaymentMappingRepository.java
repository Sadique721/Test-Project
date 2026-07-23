package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository;

import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchPaymentMappingRepository extends JpaRepository<BatchPaymentMapping, Long> , QuerydslPredicateExecutor<BatchPaymentMapping> {
}
