package com.savbill.partnermanagement.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TrialDebitDocumentTAXRelRepository  extends JpaRepository<TrialDebitDocumentTAXRel, Integer> {

    List<TrialDebitDocumentTAXRel> findAllByTrialdebitdocumentid(Integer debitdocumentid);

}
