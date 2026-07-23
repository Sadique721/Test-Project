package com.savbill.revenuemanagement.core.repository.partner;


import com.savbill.revenuemanagement.core.entity.partner.TempPartnerLedgerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TempPartnerLedgerDetailsRepository extends JpaRepository<TempPartnerLedgerDetail,Long>
{
    List<TempPartnerLedgerDetail> findAllByPartner_Id(Integer id);
    @Query(value = "SELECT * FROM tbltmppartnerledgerdetails WHERE  debit_doc_id=:invoice_id AND is_deleted=false",nativeQuery = true)
    List<TempPartnerLedgerDetail> findAllByInvoiceId(@Param("invoice_id") Integer invoice_id);
}
