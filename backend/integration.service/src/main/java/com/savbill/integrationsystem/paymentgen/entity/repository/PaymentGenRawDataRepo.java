package com.savbill.integrationsystem.paymentgen.entity.repository;

import com.savbill.integrationsystem.paymentgen.entity.PaymentGenRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGenRawDataRepo extends JpaRepository<PaymentGenRawData, Integer>, QuerydslPredicateExecutor<PaymentGenRawData> {
}
