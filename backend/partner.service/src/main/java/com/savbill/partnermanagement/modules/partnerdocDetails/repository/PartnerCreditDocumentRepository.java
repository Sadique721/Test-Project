package com.savbill.partnermanagement.modules.partnerdocDetails.repository;


import com.savbill.partnermanagement.modules.partnerdocDetails.model.PartnerCreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PartnerCreditDocumentRepository extends JpaRepository<PartnerCreditDocument, Integer>, QuerydslPredicateExecutor<PartnerCreditDocument> {

    List<PartnerCreditDocument> getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type);
}
