package com.savbill.partnermanagement.modules.partnerdocDetails.repository;


import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerCreditDebitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PartnerCreditDebitMappingRepository extends JpaRepository<PartnerCreditDebitMapping, Integer>, QuerydslPredicateExecutor<PartnerCreditDebitMapping> {
}
