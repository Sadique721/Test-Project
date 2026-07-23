package com.savbill.revenuemanagement.scheduler.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerAuditRepository extends JpaRepository<SchedulerAudit, Long> {
}
