package com.savbill.cpm.modules.CreditTransactionMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditTansactionMappingRepository  extends JpaRepository<CreditTransactionMapping , Integer> {

}
