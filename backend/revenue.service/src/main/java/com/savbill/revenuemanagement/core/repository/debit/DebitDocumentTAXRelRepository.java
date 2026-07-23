package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentTAXRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitDocumentTAXRelRepository extends JpaRepository<DebitDocumentTAXRel, Integer> {
    @Query(value = "SELECT * FROM tbltdebitdocumenttaxrel WHERE debitdocumentid=:debitdocumentid",nativeQuery = true)
    List<DebitDocumentTAXRel> findAllByDebitdocumentid(@Param("debitdocumentid") Integer debitdocumentid);



    @Query("SELECT DISTINCT dd.customer.id, d.percentage FROM DebitDocumentTAXRel d " +
            "JOIN DebitDocument dd ON d.debitdocumentid = dd.id " +
            "WHERE dd.customer IS NOT NULL AND d.percentage IS NOT NULL " +
            "AND dd.paymentStatus NOT IN ('Fully Paid', 'Cancelled','Void') AND dd.customer.id IN :customerIds ")
    List<Object[]> findTaxPercentagesByCustomerIds(@Param("customerIds") List<Integer> customerIds);


    @Query("SELECT DISTINCT dd.customer.id, dd.docnumber FROM DebitDocumentTAXRel d " +
            "JOIN DebitDocument dd ON d.debitdocumentid = dd.id " +
            "WHERE dd.customer IS NOT NULL AND dd.paymentStatus NOT IN ('Fully Paid', 'Cancelled','Void')  and dd.id in :dubitdocId ")
    List<Object[]> findDebitdocIdByCustomerIds(@Param("dubitdocId") List<Integer> dubitdocId);
    }


