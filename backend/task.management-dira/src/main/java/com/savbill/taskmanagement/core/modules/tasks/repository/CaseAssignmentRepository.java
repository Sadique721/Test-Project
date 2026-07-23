package com.savbill.taskmanagement.core.modules.tasks.repository;


import com.savbill.taskmanagement.core.modules.tasks.domain.CaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
}
