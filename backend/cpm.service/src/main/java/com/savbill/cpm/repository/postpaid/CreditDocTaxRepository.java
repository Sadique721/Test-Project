package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.creditdoc.CreditDocTaxRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDocTaxRepository extends JpaRepository<CreditDocTaxRel, Integer>, QuerydslPredicateExecutor<CreditDocTaxRel> {
}
