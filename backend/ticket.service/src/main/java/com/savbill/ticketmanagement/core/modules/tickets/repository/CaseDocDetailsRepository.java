package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseDocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseDocDetailsRepository extends JpaRepository<CaseDocDetails,Long>,  QuerydslPredicateExecutor<CaseDocDetails> {

    List<CaseDocDetails> findAllByTicketId(Long ticketId);
    @Query(value = "SELECT t.filename FROM savbillticketmanagement.tblcasedocdetails t WHERE t.ticket_id = :ticketId ORDER BY t.doc_id DESC LIMIT 1", nativeQuery = true)
    String findLastSavedFilenameByTicketId(@Param("ticketId") Long ticketId);

}
