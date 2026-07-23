package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.LeadAudit;

@Repository
public interface LeadAuditRepository extends JpaRepository<LeadAudit, Long>{

}
