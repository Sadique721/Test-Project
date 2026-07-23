package com.savbill.notification.repository;

import com.savbill.notification.entity.SchedulerAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerAuditRepository extends JpaRepository<SchedulerAudit, Long> {
}

