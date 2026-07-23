package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.CreditNoteGenRawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditNoteGenRawDataRepository extends JpaRepository<CreditNoteGenRawData, Integer>, QuerydslPredicateExecutor<CreditNoteGenRawData> {
}
