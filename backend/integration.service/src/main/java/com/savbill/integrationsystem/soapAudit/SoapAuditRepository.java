package com.savbill.integrationsystem.soapAudit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoapAuditRepository extends JpaRepository<SoapAudit,Long> {
}
