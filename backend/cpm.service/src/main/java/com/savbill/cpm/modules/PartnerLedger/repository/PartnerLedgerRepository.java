package com.savbill.cpm.modules.PartnerLedger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.PartnerLedger.domain.PartnerLedger;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PartnerLedgerRepository extends JpaRepository<PartnerLedger,Long>, QuerydslPredicateExecutor<PartnerLedger> {

    PartnerLedger findByPartner_Id(Integer id);
}
