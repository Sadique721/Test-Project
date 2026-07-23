package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.CreditDebitDocMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDebtMappingRepository extends JpaRepository<CreditDebitDocMapping, Integer>, QuerydslPredicateExecutor<CreditDebitDocMapping> {
}
