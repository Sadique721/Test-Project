package com.savbill.partnermanagement.modules.partnerdocDetails.repository;


import com.savbill.partnermanagement.modules.partner.entity.PartnerLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PartnerLedgerRepository extends JpaRepository<PartnerLedger,Long>, QuerydslPredicateExecutor<PartnerLedger> {

    PartnerLedger findByPartner_Id(Integer id);
}
