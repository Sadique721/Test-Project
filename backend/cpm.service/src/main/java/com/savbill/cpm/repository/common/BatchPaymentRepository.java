package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.BatchPayment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface BatchPaymentRepository extends JpaRepository<BatchPayment, Long> ,QuerydslPredicateExecutor<BatchPayment> {
    List<BatchPayment> findAllByIdIn(List<Long> batchIds);
}
