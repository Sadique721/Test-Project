package com.savbill.revenuemanagement.core.repository.partner;

import com.savbill.revenuemanagement.core.entity.partner.PartnerLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerLedgerRepository extends JpaRepository<PartnerLedger,Long> {
    PartnerLedger findByPartner_Id(Integer id);
}