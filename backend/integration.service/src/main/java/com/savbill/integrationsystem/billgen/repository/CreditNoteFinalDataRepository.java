package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.CreditNoteFinalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;

public interface CreditNoteFinalDataRepository extends JpaRepository<CreditNoteFinalData, Integer>, QuerydslPredicateExecutor<CreditNoteFinalData> {
    CreditNoteFinalData findFirstByAddedDateAndIsPushedTrue(LocalDate localDate);
}

