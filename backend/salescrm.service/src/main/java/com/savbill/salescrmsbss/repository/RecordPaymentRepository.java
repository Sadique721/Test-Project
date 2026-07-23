package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.RecordPayment;

@Repository
public interface RecordPaymentRepository extends JpaRepository<RecordPayment, Integer>{

}
