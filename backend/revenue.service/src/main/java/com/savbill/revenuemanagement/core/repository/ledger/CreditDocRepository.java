package com.savbill.revenuemanagement.core.repository.ledger;

import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@JaversSpringDataAuditable
public interface CreditDocRepository extends JpaRepository<CreditDocument, Integer>, QuerydslPredicateExecutor<CreditDocument> {
    @Query(nativeQuery = true, value = "SELECT nextvalcreditnote('creditnoteno')")
    String getFuction();


    @Query(nativeQuery = true, value = "SELECT nextvalpayment('paymentno')")
    String getPaymentFuction();

    @Query("SELECT  cred from CreditDocument cred where cred.id in :creditDocids and cred.status= :pending")
    boolean isPresent(List<Integer> creditDocids, String pending);

    @Query(value = "select (sum(case when cd.APPROVEDBYSTAFFID is not null and cd.STATUS != 'rejected' then cd.amount else 0 end)) as pendingAmt from tbltcreditdoc cd where (cd.invoiceid = :id AND cd.type = 'CREDITNOTE')", nativeQuery = true)
    Double findTotalPendingAmountByDebitDocIdforCN(Integer id);

    @Query(value = "SELECT  * from tbltcreditdoc t where t.CREDITDOCID in :creditDocIds",nativeQuery = true)
    List<CreditDocument> findAllByIdIn(@Param("creditDocIds") List<Integer> creditDocIdList);
    @Query(value = "select (sum(case when cd.APPROVEDBYSTAFFID is not null and cd.STATUS != 'rejected' then cd.amount else 0 end)) as pendingAmt from tbltcreditdoc cd where cd.invoiceid = :id", nativeQuery = true)
    Double findTotalPendingAmountByDebitDocId(Integer id);
    @Query("SELECT cred FROM CreditDocument cred WHERE cred.customer.id =:custId AND cred.status !='rejected' AND cred.paytype !='Withdrawal' AND (cred.amount - cred.adjustedAmount) > 0")
    Iterable<CreditDocument> findAllByCustomerIdAndStatus(Integer custId);

    @Query("select coalesce( sum(t2.amount ),0) from CreditDocument t2  where t2.id in (select t.creditDocId from CreditDebitDocMapping t where t.debtDocId=:debitDocId)  and t2.paymode=:payMode and t2.invoiceId != null and t2.invoiceId=:debitDocId and t2.status not in ('pending','rejected')")
    Double checkCreditNoteIsAllowedOrNot(@Param(value = "debitDocId") Integer debitDocId, @Param(value = "payMode") String payMode);

    List<CreditDocument> getAllByCustomer_IdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseAndStatusNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type,String status);
    List<CreditDocument> getAllByCustomer_IdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseAndStatusOrderByIdDesc(Integer custId, String payType, String type,String status);

    @Query("SELECT cred FROM CreditDocument cred WHERE cred.customer.id =:custId AND lower(cred.type) =:type")
    List<CreditDocument> findAllByCustomerAndTypeIgnoreCase(@Param(value = "custId") Integer custId,@Param(value = "type")  String type);//, final Pageable pageable);

    List<CreditDocument> findAllByCustomer(Customers customers);//, final Pageable pageable);

    List<CreditDocument> findAllByCustomerAndStatus(Customers customers,String status);
    List<CreditDocument> findAllByCustomerAndStatusInAndTypeNot(Customers customers,List<String> status , String paymentStatus);
   List<CreditDocument>  findByCustomerId(Integer custId);
    List<CreditDocument> findByCustomerIdAndStatusIn(Integer customerId, List<String> status);

    @Query(value = "select creditdocumentno from TBLTCREDITDOC where UPPER(type) LIKE UPPER('%CREDITNOTE%') AND CREDITDOCID IN(:list)",nativeQuery = true)
    List<String> findAllByIdInAndTypeCreditNote(List<Integer> list);

    @Query("select  new CreditDocument (c.amount,c.adjustedAmount,c.paymode,c.id,c.amount-c.adjustedAmount,c.referenceno,c.creditdocumentno,c.invoiceId,c.paymentreferenceno)  from CreditDocument c where c.customer.id = :customerId and c.amount-c.adjustedAmount > 0 and c.paytype != 'Withdrawal' and c.paytype != 'transfer' and c.status != 'pending' and c.status!= 'rejected' and c.status!='Fully Adjusted'")
    List<CreditDocument> getWithdrawPayments(@Param(value = "customerId") Integer customerId, Pageable pageable);

    @Query(value = "select sum(t.AMOUNT - t.adjustedamount)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype != 'Withdrawal'", nativeQuery = true)
    Double totalWithDrawAmount(Integer customerId);

    @Query(value = "select sum(t.AMOUNT)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype != 'Withdrawal'", nativeQuery = true)
    Double totalWithDrawAmountCaf(Integer customerId);

    @Query(value = "select coalesce(sum(t.AMOUNT - t.adjustedamount),0)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype = 'Withdrawal' and t.STATUS = 'pending'", nativeQuery = true)
    Double totalPendingAmount(Integer customerId);

    @Query(value = "select coalesce(sum(t.AMOUNT),0)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype = 'Withdrawal' and t.STATUS = 'pending'", nativeQuery = true)
    Double totalPendingAmountCaf(Integer customerId);

    List<CreditDocument> findAllByInvoiceIdIn(List<Integer> ids);

    @Query(value = "select status from TBLTCREDITDOC t where t.invoiceid in (:ids)", nativeQuery = true)
    List<String> findStatusByInvoiceIdIn(List<Integer> ids);

    List<CreditDocument> findAllByInvoiceIdAndType(Integer ids,String type);

    @Query(value = "select t.CREDITDOCID from TBLTCREDITDOC t where t.is_delete = false" , nativeQuery = true)
    List<Integer> findAllCreditDocID();

    @Query(value = "select t.CREDITDOCID from TBLTCREDITDOC t where t.is_delete = false and t.batchAssigned = true" , nativeQuery = true)
    List<Integer> findAllCreditDocIDAndBatchStatus();

    CreditDocument findByInvoiceIdAndStatus(Integer invoiceId,String status);

    List<CreditDocument> findAllByTrialDebitdocId(Integer invoiceId);
    List<CreditDocument> getByInvoiceId(Integer invoiceId);
    CreditDocument findByInvoiceIdAndCustomer(Integer invoiceId,Customers customers);

    @Query("SELECT cred FROM CreditDocument cred WHERE cred.customer.id = :custId AND cred.status IN :statuses AND referenceno =:referenceno")
    List<CreditDocument> findAllByCustomerAndStatusIn(@Param("custId") Integer custId, @Param("statuses") List<String> statuses,@Param("referenceno") String referenceno);

    CreditDocument findAllByReferenceno(String referenceNo);


    @Query("SELECT cred FROM CreditDocument cred WHERE cred.customer.id IN :customerIdList AND cred.paytype = :payType AND cred.status =:status AND cred.type=:type")
    List<CreditDocument> findAllByCustomerInAndPaytypeIgnoreCaseAndStatusIgnoreCaseAndTypeIgnoreCase(@Param("customerIdList") List<Integer> customerIdList,@Param("payType") String payType,@Param("status") String status,@Param("type") String type);

    @Query("select new CreditDocument(c.id,c.referenceno,c.reciptNo,c.remarks,c.paytype,c.status)   from CreditDocument c where " +
            " c.id = :id")
    CreditDocument findLightCreditDocumentById(@Param(value = "id") Integer id);

    @Query("select new CreditDocument(c.id,c.referenceno,c.reciptNo,c.remarks,c.paytype,c.status)   from CreditDocument c where " +
            " c.id IN :ids")
    List<CreditDocument> findLightCreditDocumentByIdIn(@Param(value = "ids") List<Integer> ids);

    @Query("SELECT new CreditDocument(" +
            "cd.id, cd.paymode, cd.paymentdate, cd.paydetails1, cd.paydetails2, cd.paydetails3, cd.paydetails4, " +
            "cd.amount, cd.status, cd.createdById, cd.createdByName, cd.remarks, cd.reciptNo, cd.customer, " +
            "cd.type, cd.approverid, cd.filename, cd.nextTeamHierarchyMappingId, cd.creditdocumentno, " +
            "cd.referenceno, cd.tdsamount, cd.abbsAmount, cd.adjustedAmount, " +
            "cd.bankManagement, cd.onlinesource, cd.mvnoId, cd.invoiceId, CASE WHEN LOWER(cd.paytype) = 'advance' THEN cd.type ELSE cd.paytype END, cd.paymentreferenceno, concat(cd.customer.firstname, ' ', cd.customer.lastname),cd.createdate) " +
            "FROM CreditDocument cd " +
            "WHERE cd.customer.id = :custId " +
            "AND LOWER(cd.paytype) NOT IN :payTypes " +
            "AND LOWER(cd.type) <> LOWER(:type) " +
            "AND LOWER(cd.status) <> LOWER(:status) " +
            "AND cd.isDelete=false "+
            "ORDER BY cd.id DESC")
    List<CreditDocument> findAllByCustomerIdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseAndStatusNotIgnoreCaseOrderByIdDescLightCreditdoc(
            @Param("custId") Integer custId,
            @Param("payTypes") List<String> payTypes,
            @Param("type") String type,
            @Param("status") String status
    );

//    @Query("SELECT cred FROM CreditDocument cred WHERE cred.customer.id IN :customerIdList AND cred.paytype = :payType AND cred.status IN :status AND cred.type=:type order by id asc")
//    List<CreditDocument> findAllByCustomerInAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(@Param("customerIdList") List<Integer> customerIdList,@Param("payType") String payType,@Param("status") List<String> status,@Param("type") String type);

    @Query("SELECT new com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment(cred.id, cred.amount, cred.adjustedAmount,cred.customer.id) " +
            "FROM CreditDocument cred " +
            "WHERE cred.customer.id IN :customerIdList " +
            "AND cred.paytype IN :payType " +
            "AND cred.status IN :status " +
            "AND cred.type IN :type " +
            "ORDER BY cred.id ASC")
    List<CreditDocumentDTOForAdjustment> findAllByCustomerInAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(@Param("customerIdList") List<Integer> customerIdList,
                                                             @Param("payType") List<String> payType,
                                                             @Param("status") List<String> status,
                                                             @Param("type") List<String> type);

    @Query("SELECT new com.savbill.revenuemanagement.core.entity.ladger.CreditDocumentDTOForAdjustment(cred.id, cred.amount, cred.adjustedAmount,cred.customer.id) " +
            "FROM CreditDocument cred " +
            "WHERE cred.customer.id = :customerId " +
            "AND cred.paytype IN :payType " +
            "AND cred.status IN :status " +
            "AND cred.type IN :type " +
            "ORDER BY cred.id ASC")
    List<CreditDocumentDTOForAdjustment> findAllByCustomerAndPaytypeIgnoreCaseAndStatusInAndTypeIgnoreCase(@Param("customerId") Integer customerId,
                                                                                                             @Param("payType") List<String> payType,
                                                                                                             @Param("status") List<String> status,
                                                                                                             @Param("type") List<String> type);


    @Query("SELECT c.id FROM CreditDocument c WHERE c.trialDebitdocId IN :trialDebitDocIds")
    List<Integer> findCreditDocumentIdsByTrialDebitDocIds(@Param("trialDebitDocIds") List<Integer> trialDebitDocIds);

    @Modifying
    @Transactional
    @Query("UPDATE CreditDocument c SET c.trialDebitdocId = :newId WHERE c.id IN :creditDocumentIds")
    void updateTrialDebitdocIds(@Param("creditDocumentIds") List<Integer> creditDocumentIds, @Param("newId") Integer newId);

    @Modifying
    @Transactional
    @Query("UPDATE CreditDocument c SET c.status = :status WHERE c.id IN :creditDocumentIds")
    void updateCreditDocStatus(@Param("creditDocumentIds") List<Integer> creditDocumentIds, @Param("status") String status);

    @Query("SELECT c.tdsamount, c.abbsAmount FROM CreditDocument c WHERE c.customer.id = :customerId")
    List<Object[]> findTdsAndAbbsAmountsByCustomerId(@Param("customerId") Integer customerId);

    @Query(value = "SELECT SUM(cd.AMOUNT) FROM tbltcreditdoc cd WHERE cd.CREDITDOCID IN (:creditDocIds)", nativeQuery = true)
    Double findTotalAmountByCreditDocIds(@Param("creditDocIds") List<Integer> creditDocIds);

    @Query("select new CreditDocument(c.amount, c.adjustedAmount, c.paymode, c.id, c.amount - c.adjustedAmount, c.referenceno, c.creditdocumentno, c.invoiceId, c.paymentreferenceno,c.withDrawCreditdocId) from CreditDocument c where c.customer.id = :customerId and c.paytype != 'Withdrawal' and c.paytype != 'transfer' and c.status != 'pending' and c.status!= 'rejected' and c.amount-c.adjustedAmount > 0")
    List<CreditDocument> getAllPaymentsForCustomer(@Param("customerId") Integer customerId, Pageable pageable);

    @Query(value = "SELECT  * from tbltcreditdoc t where t.CREDITDOCID in :creditDocIds  ORDER BY t.amount DESC",nativeQuery = true)
    List<CreditDocument> findAllByIdInOrderByAmount(@Param("creditDocIds") List<Integer> creditDocIdList);

    List<CreditDocument> findAllByCreditdocumentnoIn(List<String> creditDocumentNos);

    @Query("SELECT c FROM CreditDocument c WHERE c.isKraSynced = false AND c.status IN ('Fully Adjusted', 'Partialy Adjusted') AND c.paymode = 'Credit Note'")
    List<CreditDocument> findUnsyncedKraCreditNotes();
}
