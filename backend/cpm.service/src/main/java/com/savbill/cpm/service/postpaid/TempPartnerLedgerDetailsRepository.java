package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.model.postpaid.TempPartnerLedgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TempPartnerLedgerDetailsRepository extends JpaRepository<TempPartnerLedgerDetail,Long>, QuerydslPredicateExecutor<TempPartnerLedgerDetail>
{
    List<TempPartnerLedgerDetail> findAllByPartner_Id(Integer id);

    @Query(value = "SELECT * FROM tbltmppartnerledgerdetails t WHERE  t.invoice_id=:invoice_id",nativeQuery = true)
    List<TempPartnerLedgerDetail> findAllByInvoiceId(@Param("invoice_id") Integer invoice_id);
}
