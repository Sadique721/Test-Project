package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialDebitDocumentDetailRepository extends JpaRepository<TrialDebitDocumentDetail,Integer> {
    List<TrialDebitDocumentDetail> findAllByDebitdocumentid(Integer debitdocId);


}
