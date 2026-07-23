package com.savbill.integrationsystem.deviceveri.repository;

import com.savbill.integrationsystem.deviceveri.domain.CustomerLedgerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLedgerRepo extends JpaRepository<CustomerLedgerData, Long> {
}
