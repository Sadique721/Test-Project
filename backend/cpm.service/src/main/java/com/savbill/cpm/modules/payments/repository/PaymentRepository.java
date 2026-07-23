package com.savbill.cpm.modules.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.payments.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByTxnId(String txnId);

}
