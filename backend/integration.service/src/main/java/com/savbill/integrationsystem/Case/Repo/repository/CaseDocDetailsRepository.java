package com.savbill.integrationsystem.Case.Repo.repository;

import com.savbill.integrationsystem.Case.CaseDocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDocDetailsRepository extends JpaRepository<CaseDocDetails,Long>,  QuerydslPredicateExecutor<CaseDocDetails> {

    List<CaseDocDetails> findAllByTicketId(Long ticketId);

}
