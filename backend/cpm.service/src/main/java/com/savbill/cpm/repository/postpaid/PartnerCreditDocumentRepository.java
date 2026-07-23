package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.PartnerCreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PartnerCreditDocumentRepository extends JpaRepository<PartnerCreditDocument, Integer>, QuerydslPredicateExecutor<PartnerCreditDocument> {

    List<PartnerCreditDocument> getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type);
}
