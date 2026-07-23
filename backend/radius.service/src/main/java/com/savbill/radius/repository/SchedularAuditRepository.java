package com.savbill.radius.repository;

import com.savbill.radius.entity.SchedularAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedularAuditRepository extends JpaRepository<SchedularAudit, Long> {

}
