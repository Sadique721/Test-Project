package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.PartnerCreditDebitMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PartnerCreditDebitMappingRepository extends JpaRepository<PartnerCreditDebitMapping, Integer>, QuerydslPredicateExecutor<PartnerCreditDebitMapping> {
}
