package com.savbill.revenuemanagement.core.entity.ladger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditDocTaxRelRepository extends JpaRepository<CreditDocTaxRel, Long> {
}
