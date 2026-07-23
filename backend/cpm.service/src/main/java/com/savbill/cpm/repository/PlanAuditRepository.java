package com.savbill.cpm.repository;

import com.savbill.cpm.pojo.PlanAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAuditRepository extends JpaRepository<PlanAudit,Long> {
}
