package com.savbill.cpm.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.tickets.domain.CaseAssignment;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
}
