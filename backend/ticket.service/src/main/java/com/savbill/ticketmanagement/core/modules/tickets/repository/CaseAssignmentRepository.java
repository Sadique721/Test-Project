package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
}
