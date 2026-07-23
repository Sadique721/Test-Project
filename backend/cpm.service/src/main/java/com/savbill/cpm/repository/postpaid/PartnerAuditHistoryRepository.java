package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.PartnerAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerAuditHistoryRepository extends JpaRepository<PartnerAuditHistory, Integer>, QuerydslPredicateExecutor<PartnerAuditHistory>{
}
