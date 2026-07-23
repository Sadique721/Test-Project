package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.DebitDocumentStaffAssignMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitDocStaffAssignRepo extends JpaRepository<DebitDocumentStaffAssignMapping, Long>, QuerydslPredicateExecutor<DebitDocumentStaffAssignMapping> {
    List<DebitDocumentStaffAssignMapping> findAllByDebitDocId(Integer debitDocId);

    void deleteAllByDebitDocId(Integer debitDocId);
}
