package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.TrialDebitDocShowPojo;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrailDebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitProjection;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface TrialDebitDocRepository extends JpaRepository<TrialDebitDocument,Integer> {
    @Query(value = "select deb from TrialDebitDocument deb where deb.id in : debitDocList and deb.billrunstatus='VOID' or deb.billrunstatus='VOID' ORDER BY deb.Id desc LIMIT 1",nativeQuery = true)
    List<TrialDebitDocument> findByCustomerId(Integer id, String generated, String exported);

    List<TrialDebitDocument> findByBillrunid(Integer valueOf);

    List<TrialDebitDocument> findByCustomerId(Integer custId);

    @Query(value = "select * from tblttrialdebitdocument t where t.billrunstatus not in ('Cancelled','VOID') and t.is_delete =0 and t.subscriberid =:custId",nativeQuery = true)
    List<TrialDebitDocument> findByCustomerIdBasedOnBillrunStatusAndIsDeleteFalse(Integer custId);
    TrialDebitDocument getByCustomerId(Integer customerId);

    List<TrialDebitDocument> findAllByInventoryMappingId(Long inventoryMappingId);

    @Query(value = "select deb from TrialDebitDocument deb where deb.id = :debitDocId and deb.customer.id = :custId ")
    TrialDebitDocument findAllDebitDocSearhcResult(@Param("debitDocId") Integer debitDocId, @Param("custId") Integer custId);


    @Query("select t from TrialDebitDocument t where t.custpackrelid in (:custPackRelIds) and t.billrunstatus NOT IN ('Cancelled', 'Void')")
    List<TrialDebitDocument> findOldDebitDocIds(List<Integer> custPackRelIds);

    @Query(value = "SELECT * FROM tblttrialdebitdocument t " +
            "WHERE t.trialdebitdocumentid = " +
            "(SELECT t2.trialdebitdocid FROM tbltcreditdoc t2 WHERE t2.referenceno = :referenceNo)",
            nativeQuery = true)
    TrialDebitDocument findByReferenceNo(@Param("referenceNo") String referenceNo);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.TrialDebitDocShowPojo(deb.id, deb.docnumber,deb.createdByName, deb.tax,deb.totalamount,deb.adjustedAmount,deb.subtotal,deb.billrunstatus) " +
            "FROM TrialDebitDocument deb " +
            "WHERE deb.custpackrelid = :custpackrelid ")
    List<TrialDebitDocShowPojo> findAllInvoiceByCustomerPlanMappingId(Integer custpackrelid);

    @Query("SELECT d FROM TrialDebitDocument d " +
            "WHERE d.customer.id = :custId " +
            "AND d.billrunstatus NOT IN :excludedStatuses " +
            "AND d.isDelete = false " +
            "AND (d.paymentStatus IN :paymentStatuses OR d.paymentStatus IS NULL)")
    List<TrialDebitDocument> findDebitDocumentsByCustId(
            @Param("custId") Integer custId,
            @Param("excludedStatuses") List<String> excludedStatuses, @Param("paymentStatuses") List<String> paymentStatuses);

    @Query("SELECT t.id, t.adjustedAmount FROM TrialDebitDocument t WHERE t.customer.id = :customerId")
    List<Object[]> findIdsAndAdjustedAmountsByCustomerId(@Param("customerId") Integer customerId);

    @Modifying
    @Transactional
    @Query("UPDATE TrialDebitDocument t SET t.adjustedAmount = NULL WHERE t.id IN :trialDebitDocIds")
    void updateAdjustedAmountsToNull(@Param("trialDebitDocIds") List<Integer> trialDebitDocIds);


    @Query("SELECT new com.savbill.revenuemanagement.core.entity.debitdoc.TrailDebitDocumentDTOForAdjustment(d.id, d.totalamount, COALESCE(d.adjustedAmount, 0.0),d.customer.id) " +
            "FROM TrialDebitDocument d " +
            "WHERE d.customer.id IN :custIds " +
            "AND (d.paymentStatus NOT IN ('Fully Paid', 'Cancelled')  OR d.paymentStatus IS NULL) "
            )
    List<TrailDebitDocumentDTOForAdjustment> getTrailDebitDocForPaymentAdjustment(@Param("custIds") List<Integer> custIds);

    @Query("SELECT new com.savbill.revenuemanagement.core.entity.debitdoc.TrailDebitDocumentDTOForAdjustment(d.id, d.totalamount, COALESCE(d.adjustedAmount, 0.0),d.customer.id) " +
            "FROM TrialDebitDocument d " +
            "WHERE d.customer.id = :custId " +
            "AND (d.paymentStatus NOT IN ('Fully Paid', 'Cancelled')  OR d.paymentStatus IS NULL) "
    )
    List<TrailDebitDocumentDTOForAdjustment> getTrailDebitDocForPaymentAdjustmentByCustid(@Param("custId") Integer custId);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo(deb.id, deb.docnumber,deb.createdByName, deb.tax,deb.totalamount,deb.adjustedAmount,deb.subtotal,deb.billrunstatus, deb.customer.status) " +
            "FROM TrialDebitDocument deb " +
            "WHERE deb.customer.id = :id " +
            "AND deb.billrunstatus != 'VOID' AND deb.billrunstatus != 'CANCELLED' " +
            "AND ABS(deb.totalamount - COALESCE(deb.adjustedAmount, 0.0)) > 0.01")
    List<DebitShowDocumentPojo> findAllInvoiceByTotalamount(Integer id);

    @Query(value = "SELECT t.plangroup FROM tblmpostpaidplan t " +
            "WHERE t.POSTPAIDPLANID = (" +
            "SELECT t1.planid FROM tblcustpackagerel t1 " +
            "WHERE t1.custpackageid = (" +
            "SELECT t2.custpackrelid FROM TBLTTRIALDEBITDOCUMENT t2 " +
            "WHERE t2.trialdebitdocumentid = :TraildebitDocId))", nativeQuery = true)
    String findPlanGroupByTrailDebitDocumentId(@Param("TraildebitDocId") Integer TraildebitDocId);

    @Query(
            value = "SELECT t.plantype, t.validity " +
                    "FROM tblmpostpaidplan t " +
                    "WHERE t.POSTPAIDPLANID = (" +
                    "   SELECT r.planid " +
                    "   FROM tblcustpackagerel r " +
                    "   WHERE r.custpackageid = (" +
                    "       SELECT d.custpackrelid " +
                    "       FROM TBLTTRIALDEBITDOCUMENT d " +
                    "       WHERE d.trialdebitdocumentid = :TraildebitDocId))",
            nativeQuery = true)
    Object[] findPlanTypeByDebitDocumentId(@Param("TraildebitDocId") Integer TraildebitDocId);

    @Query(
            value = "SELECT t.billing_cycle " +
                    "FROM tbltcustchargehistory t " +
                    "WHERE t.cust_id = :custId " +
                    "AND t.plan_id = ( " +
                    "    SELECT c.planid " +
                    "    FROM tblcustpackagerel c " +
                    "    WHERE c.custpackageid = ( " +
                    "        SELECT d.custpackrelid " +
                    "        FROM TBLTTRIALDEBITDOCUMENT d " +
                    "        WHERE d.trialdebitdocumentid = :TraildebitDocId " +
                    "    ) " +
                    ") " +
                    "ORDER BY t.billing_cycle ASC LIMIT 1",
            nativeQuery = true)
    Integer findLowestBillingCycle(@Param("custId") Integer custId, @Param("TraildebitDocId") Integer TraildebitDocId);

    @Query(value = "SELECT distinct t.trialdebitdocumentid AS trialDebitDocumentId, t.totalamount AS totalAmount FROM tblttrialdebitdocument t INNER JOIN tbltcreditdebitmapping m ON t.trialdebitdocumentid = m.trialdebitdocumentid WHERE m.creditdocid = :creditDocId", nativeQuery = true)
    List<TrialDebitProjection> findTrialDebitDocsWithAmount(@Param("creditDocId") Integer creditDocId);
}
