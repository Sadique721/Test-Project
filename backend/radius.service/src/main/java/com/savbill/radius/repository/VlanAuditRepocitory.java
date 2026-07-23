package com.savbill.radius.repository;

import com.savbill.radius.entity.VlanAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface VlanAuditRepocitory extends JpaRepository<VlanAudit,Long>, QuerydslPredicateExecutor<VlanAudit> {
}
