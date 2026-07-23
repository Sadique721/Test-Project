package com.savbill.partnermanagement.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitDocumentTAXRelRepository extends JpaRepository<DebitDocumentTAXRel, Integer> {

    List<DebitDocumentTAXRel> findAllByDebitdocumentid(Integer debitdocumentid);
}
