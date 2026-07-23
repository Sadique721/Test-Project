package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketTatAudits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TatAuditRepository extends JpaRepository<TicketTatAudits,Integer>, QuerydslPredicateExecutor<TicketTatAudits> {

    List<TicketTatAudits> findAllByCaseId(Integer caseId);
}
