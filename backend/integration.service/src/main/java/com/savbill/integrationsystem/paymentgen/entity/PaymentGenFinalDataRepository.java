package com.savbill.integrationsystem.paymentgen.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;

public interface PaymentGenFinalDataRepository extends JpaRepository<PaymentGenFinalData, Long>, QuerydslPredicateExecutor<PaymentGenFinalData> {
    PaymentGenFinalData findFirstByPaymentDateAndIsPushedTrue(LocalDate paymentDate);
}
