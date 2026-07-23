package com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long>, QuerydslPredicateExecutor<WorkflowAudit> {
}
