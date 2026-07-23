package com.savbill.revenuemanagement.core.entity.ladger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditDocChargeRelRepository extends JpaRepository<CreditDocChargeRel, Long> {
    List<CreditDocChargeRel> findAllByCreditdocid(Integer creditDocumentId);
}
