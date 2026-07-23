package com.savbill.salescrmsbss.repository;

import com.savbill.salescrmsbss.entity.LeadGeneralAudit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadGeneralAuditRepository extends JpaRepository<LeadGeneralAudit, Long> {
}
