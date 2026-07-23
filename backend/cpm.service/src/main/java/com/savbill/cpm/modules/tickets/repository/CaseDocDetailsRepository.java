package com.savbill.cpm.modules.tickets.repository;

import com.savbill.cpm.modules.tickets.domain.CaseDocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDocDetailsRepository extends JpaRepository<CaseDocDetails,Long>,  QuerydslPredicateExecutor<CaseDocDetails> {

    List<CaseDocDetails> findAllByTicketId(Long ticketId);

}
