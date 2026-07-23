package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.ProfomaDebitDocumentDetail;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface ProfomaDebitDocDetailsRepository extends JpaRepository<ProfomaDebitDocumentDetail , Integer> {
    List<ProfomaDebitDocumentDetail> findAllByDebitdocumentid(Integer debitdocId);
}
