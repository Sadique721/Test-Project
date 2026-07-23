package com.savbill.revenuemanagement.core.repository.debit;

import com.savbill.revenuemanagement.InvoiceIntigration.SendInvoiceDTO;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitDocNumberMappingPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo;
import com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO;
import com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO;
import com.savbill.revenuemanagement.core.dto.invoice.DebitDocCustDto;
import com.savbill.revenuemanagement.core.dto.invoice.DebitDocumentCreditNoteView;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.BulkInvoiceDownloadProjection;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentTAXRel;
import com.savbill.revenuemanagement.core.service.prepaid.DebitDocCustDTO;
import com.savbill.revenuemanagement.server.DebitDocData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
//@JaversSpringDataAuditable
public interface DebitDocRepository extends JpaRepository<DebitDocument, Integer> {

    @Query("SELECT d.id AS debitDocId, d.docnumber AS docNumber, " +
            "d.customer.id AS customerId " +
            "FROM DebitDocument d " +
            "WHERE d.id IN :debitDocIds " +
            "AND d.customer.mvnoId = :mvnoId " +
            "AND d.billrunstatus = :billRunStatus")
    List<BulkInvoiceDownloadProjection> findBulkInvoiceDownloadData(
            @Param("debitDocIds") List<Integer> debitDocIds,
            @Param("mvnoId") Integer mvnoId,
            @Param("billRunStatus") String billRunStatus);

    @Query(value = "SELECT dd.krainvoice_id AS kraInvoiceId, " +
            "ddd.planid AS planId, " +
            "pp.POSTPAIDPLANID AS postpaidPlanId, " +
            "pp.NAME AS planName, " +
            "pp.offerprice AS offerPrice " +
            "FROM TBLTDEBITDOCUMENT dd " +
            "LEFT JOIN tbltdebitdocumentdetail ddd ON dd.debitdocumentid = ddd.debitdocumentid " +
            "LEFT JOIN TBLMPOSTPAIDPLAN pp ON pp.POSTPAIDPLANID = CAST(ddd.planid AS UNSIGNED) " +
            "WHERE dd.debitdocumentid = :debitDocumentId",
            nativeQuery = true)
    List<Object[]> getKraAndPlanDetails(@Param("debitDocumentId") Integer debitDocumentId);

    @Query(nativeQuery = true, value = "SELECT nextval('invoiceno')")
    String getInvoiceNo();

    @Query(nativeQuery = true, value = "SELECT nextval('invoiceno')")
    String getInvoiceNoForLCOPartner();

    @Query(nativeQuery = true, value = "SELECT nextvaltrial('invoicenotrial')")
    String getInvoiceNoForTrial();
//    @Query("select deb from DebitDocument deb where deb.id in : debitDocList and deb.billrunstatus='VOID' ")
//    List<DebitDocument>findAllByDebiidocidAndBillrunStatus(List<Integer>debitDocList);

    @Query(value = "SELECT * from TBLTDEBITDOCUMENT deb where deb.subscriberid=:id",nativeQuery = true)
    List<DebitDocument> findByCustomerId(Integer id);
    @Query("select deb from DebitDocument deb where deb.id  IN :debitDocList and deb.billrunstatus = :status ")
    List<DebitDocument> findAllByDebiidocidAndBillrunStatusNotEquals(@Param("debitDocList") List<Integer> debitDocList,@Param("status") String status);
    @Query("select new DebitDocumentTAXRel(t.taxlevel ,t.chargeid ,t.taxname ,t.percentage ,sum(t.amount)) from DebitDocumentTAXRel t where t.debitdocumentid = :debitDocId group by t.taxlevel ,t.chargeid ,t.taxname ,t.percentage")
    List<DebitDocumentTAXRel> getAllDebitDocTaxDetails(@Param(value = "debitDocId") Integer debitDocId);

    @Query("select m from DebitDocDetails  m where m.debitdocumentid = :debitDocId")
    List<DebitDocDetails> debitDocDetailsByDebitDocId(@Param(value = "debitDocId") Integer debitdocumentid);

    @Query("select m from DebitDocument m where  m.customer.id=:customerId")
    List<DebitDocument> pendingDebitDocumentList(@Param(value = "customerId") Integer customerid);

    @Query("select t.id from DebitDocument t where t.custpackrelid in (:custPackRelIds) and t.billrunstatus NOT IN ('Cancelled', 'Void')")
    List<Integer> findOldDebitDocIds(List<Integer> custPackRelIds);

    @Query("select t.id from DebitDocument t where t.custpackrelid in (:custPackRelIds) and t.isDirectChargeInvoice != true and t.billrunstatus NOT IN ('Cancelled', 'Void')")
    List<Integer> findOldDebitDocIdsWithoutDirectCharge(List<Integer> custPackRelIds);

    @Query( value = "select t from DebitDocument t where t.customer.id=:id and t.billrunstatus=('Generated') OR t.billrunstatus= ('Exported') ORDER BY t.id desc limit  1",nativeQuery = true)
    List<DebitDocument> findAllByCustIdAndBillRunStatus(Integer id);
    @Query( "SELECT deb FROM DebitDocument  deb WHERE deb.customer.id=:id and deb.billrunstatus!='VOID' and deb.billrunstatus!='CANCELLED' AND (deb.totalamount - COALESCE(deb.adjustedAmount, 0.0)) <> 0.0 ")
    List<DebitDocument>findAllByTotalamount(Integer id);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo(deb.id, deb.docnumber,deb.createdByName, deb.tax,deb.totalamount,deb.adjustedAmount,deb.subtotal,deb.billrunstatus, deb.customer.status) " +
            "FROM DebitDocument deb " +
            "WHERE deb.customer.id = :id " +
            "AND deb.billrunstatus != 'VOID' AND deb.billrunstatus != 'CANCELLED' " +
            "AND ABS(deb.totalamount - COALESCE(deb.adjustedAmount, 0.0)) > 0.01")
    List<DebitShowDocumentPojo> findAllInvoiceByTotalamount(Integer id);


    List<DebitDocument> findAllByIdIn(List<Integer> oldDebitIds);

    @Query("select t.id from DebitDocument t where t.id = :oldDebitIds and t.isDirectChargeInvoice != true and t.billrunstatus NOT IN ('Cancelled', 'Void')")
    Integer findAllByIdandStatus(Integer oldDebitIds);
    @Query("select deb from DebitDocument deb where deb.id in :debitDocList and deb.billrunstatus='VOID' ")
    List<DebitDocument>findAllByDebiidocidAndBillrunStatus(List<Integer>debitDocList);


    @Query(value = "select deb from DebitDocument deb where deb.id = :debitDocId and deb.customer.id = :custId ")
    DebitDocument findAllDebitDocSearhcResult(@Param("debitDocId") Integer debitDocId, @Param("custId") Integer custId);

    List<DebitDocument> findAllByIdInAndBillrunstatusIsNot(List<Integer> debitdocids, String status);
    @Query("select deb  from DebitDocument deb where deb.docnumber=:invoiceNumber")
    DebitDocument findOneByDebiDocNo(String invoiceNumber);

    boolean existsByIdInAndStatus(List<Integer> debitDocids, String status);

    List<DebitDocument> findByCustomerIdAndStartdateAfter(Integer custId, LocalDateTime date);

    @Query("SELECT d FROM DebitDocument d " + "WHERE d.customer.id = :custId " + "AND d.startdate > :date " + "AND d.billrunstatus <> 'Cancelled'")
    List<DebitDocument> findActiveDebitDocs(@Param("custId") Integer custId,@Param("date") LocalDateTime date);

    @Query(value = "SELECT * from TBLTDEBITDOCUMENT deb where deb.subscriberid=:custId  and deb.billrunstatus != 'VOID' ORDER BY deb.debitdocumentid desc limit 1",nativeQuery = true)
    List<DebitDocument> lastInvoice(Integer custId);

    List<DebitDocument> findAllByCustomer(Customers customer);
    List<DebitDocument> findAllByInventoryMappingId(Long inventoryMappingId);
    @Query(value = "SELECT deb.debitdocumentid from TBLTDEBITDOCUMENT deb where deb.subscriberid=:custId  and deb.billrunstatus = 'Generated' or deb.billrunstatus = 'Exported' ORDER BY deb.debitdocumentid desc limit 1",nativeQuery = true)
    Integer lastInvoiceForCancelAndRegen(Integer custId);


//    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(d.id) from DebitDocument d where d.customer.id IN :customerIds")
//    @Query(value = "Select new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(e.id) from DebitDocument e where e.customer.id in:customerIds")
//    List<CustomDebitDocumentDTO> findCustomDebitDocumentsByCustomerIds(@Param("customerIds") List<Integer> customerIds);


//    @Query(value = "Select e from DebitDocument e where e.customer.id in:customerIds")
//    List<DebitDocument> findAllByCustomerIds(@Param("customerIds") List<Integer> customerIds);

    //select * from savbillrevenuemanagement.tbltdebitdocument t where subscriberid in (select t2.custid  from savbillrevenuemanagement.tblcustomers t2 where t2.MVNOID = 2)

//    @Query(value = "select * from tbltdebitdocument t where subscriberid in (select t2.custid from savbillrevenuemanagement.tblcustomers t2 where t2.MVNOID =:mvnoId)",nativeQuery = true)
//    List<DebitDocument> findAllByMvnoId(Integer mvnoId);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(d.id, d.totalamount, cust.username, d.billdate, d.docnumber) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId")
    List<CustomDebitDocumentDTO> findAllByMvnoId(Integer mvnoId);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(d.id, d.totalamount, cust.username, d.billdate, d.docnumber) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billdate >=:billDate")
    List<CustomDebitDocumentDTO> findAllByMvnoIdAndBillDate(Integer mvnoId, LocalDateTime billDate);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(d.id, d.totalamount, cust.username, d.billdate, d.docnumber) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' ")
    List<CustomDebitDocumentDTO> findAllByMvnoIdAndStatusIsNotVoid(Integer mvnoId);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.CustomDebitDocumentDTO(d.id, d.totalamount, cust.username, d.billdate, d.docnumber) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and d.billdate >=:billDate")
    List<CustomDebitDocumentDTO> findAllByMvnoIdAndStatusIsNotVoidAndBillDate(Integer mvnoId, LocalDateTime billDate);


//    Get total amount from DebitDoc by mvno


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT SUM(d.totalamount)" +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId")
    Double getAmountByMvnoId(Integer mvnoId);

    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billdate >=:billDate")
    Double getAmountByMvnoIdAndBillDate(Integer mvnoId, LocalDateTime billDate);

    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID'")
    Double getAmountByMvnoIdAndStatusIsNotVoid(Integer mvnoId);

    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and d.billdate >=:billDate")
    Double getAmountByMvnoIdAndStatusIsNotVoidAndBillDate(Integer mvnoId, LocalDateTime billDate);

    // Get total amount by charge

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO(dd.chargetype, dd.totalamount) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId " +
            "GROUP BY dd.chargetype")
    List<DebitDocDetailDTO> getAmountFromDebitDocdetailByMvnoId(Integer mvnoId);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO(dd.chargetype, dd.totalamount) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billdate >=:billDate " +
            "GROUP BY dd.chargetype")
    List<DebitDocDetailDTO> getAmountFromDebitDocdetailByMvnoIdAndBillDate(Integer mvnoId, LocalDateTime billDate);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO(dd.chargetype, SUM(dd.totalamount)) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' " +
            "GROUP BY dd.chargetype")
    List<DebitDocDetailDTO> getAmountFromDebitDocdetailByMvnoIdAndStatusIsNotVoid(Integer mvnoId);

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO(dd.chargetype, SUM(dd.totalamount)) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and d.billdate >= :billDate " +
            "GROUP BY dd.chargetype")
    List<DebitDocDetailDTO> getAmountFromDebitDocdetailByMvnoIdAndStatusIsNotVoidAndBillDate(Integer mvnoId, LocalDateTime billDate);


    @Query(value = "SELECT dd.chargetype, SUM(dd.totalamount) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and d.billdate >= :billDate " +
            "GROUP BY dd.chargetype")
    List<Object[]> findChargetypeTotalAmount(@Param("mvnoId") Integer mvnoId, @Param("billDate") LocalDateTime billDate);


    boolean existsByCustpackrelidAndDiscountIsGreaterThan(Integer custpackrelid, double disAmt);

    /** SQL query
     * select dd.debitdocdetailid, dd.debitdocumentid, d.debitdocumentnumber, dd.startdate, dd.enddate, d.billdate, d.totalamount, dd.totalamount as chargeamount, d.subscriberid, cust.MVNOID, dd.chargetype
     * from tbltdebitdocumentdetail dd join tbltdebitdocument d on d.debitdocumentid = dd.debitdocumentid
     * join tblcustomers cust on d.subscriberid = cust.custid
     * where cust.MVNOID = 8 and d.billdate  <= "2024-04-15 23:59:59" and d.billdate  >= "2024-04-01 00:00:00" ;
     */

    @Query(value = "SELECT new com.savbill.revenuemanagement.core.dto.invoice.DebitDocDetailDTO(dd.chargetype, dd.totalamount, dd.debitdocdetailid, d.docnumber, cust.username, d.billdate) " +
            "FROM com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN Charge c on c.id = dd.chargeid " +
            "JOIN com.savbill.revenuemanagement.core.entity.customers.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and dd.mvnodebitdocumentid is null and d.startdate >= :fromDate and d.startdate <= :toDate and dd.totalamount > 0  and c.mvnoId = 1 and d.isDelete = 0")
    List<DebitDocDetailDTO> getDebitDocDTOByMvnoAndBillDate(Integer mvnoId, LocalDateTime fromDate, LocalDateTime toDate);



    @Query("SELECT MIN(d.billdate) FROM DebitDocument d WHERE d.id IN :ids")
    LocalDateTime findMinStartDateByDebitDocIds(@Param("ids") List<Integer> debitDocId);

    @Query("SELECT MAX(d.endate) FROM DebitDocument d WHERE d.id IN :ids")
    LocalDateTime findMaxEndDateByDebitDocIds(@Param("ids") List<Integer> debitDocId);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo(t1.id, \n" +
            "    t1.docnumber,\n" +
            "    t1.customer.id,\n" +
            "    t1.startdate,\n" +
            "    t1.endate,\n" +
            "    t1.totalamount ,\n" +
            "    t2.username)\n" +
            "FROM \n" +
            "    DebitDocument t1\n" +
            "JOIN \n" +
            "    Customers t2  ON t1.customer = t2.id \n" +
            "WHERE\n" +
            "    t1.id IN :custDebitDocIds" )
    Page<MvnoDebitDocDetailsPojo> findInvoiceDetailsofCusMvno(@Param("custDebitDocIds") List<Integer> custDebitDocIds, Pageable pageable);

    DebitDocument getByCustomerId(Integer customerId);

    @Query(value = "SELECT * FROM TBLTDEBITDOCUMENT t " +
            "WHERE t.debitdocumentid = " +
            "(SELECT t2.invoiceid FROM tbltcreditdoc t2 WHERE t2.referenceno = :referenceNo)",
            nativeQuery = true)
    DebitDocument findByReferenceNo(@Param("referenceNo") String referenceNo);

    @Query(value = "SELECT t.debitdocumentid as debitDocumentId,t.subscriberid as subscriberId FROM TBLTDEBITDOCUMENT t WHERE (debitdocumentnumber is null OR debitdocumentnumber='') and used_by_thread=0",nativeQuery = true)
    List<DebitDocData> findByDocnumberIsEmptyAndUsedByThreadIsFalse();

    @Query(value = "UPDATE TBLTDEBITDOCUMENT t SET t.used_by_thread=1 WHERE t.debitdocumentid in (:ids)",nativeQuery = true)
    void updateByDebitDocIds(@Param("ids") List<Integer> ids);

    @Modifying
    @Transactional
    @Query(value = "UPDATE TBLTDEBITDOCUMENT SET xmldocument=:xml WHERE debitdocumentid=:id ",nativeQuery = true)
    void updateXML(@Param("id") Integer id,@Param("xml") String xml);

    @Query("Select new com.savbill.revenuemanagement.core.dto.invoice.DebitDocCustDto(deb.id, deb.customer.id) " +
            "from DebitDocument deb " +
            "where deb.docnumber = :docnumber and " +
            "deb.customer.id = (SELECT m.custInvoiceRefId from Mvno m where m.clientId = :clientid)")
    DebitDocCustDto findDebitDocumentIdByDocNumberAndMvnoId(
            @Param("docnumber") String docnumber,
            @Param("clientid") String clientid);


    @Query(value = "SELECT t.debitdocumentid  FROM TBLTDEBITDOCUMENT t WHERE (is_delete=true OR billrunstatus='VOID') AND t.debitdocumentid IN (:debitDocumentIds)",nativeQuery = true)
    List<Integer> findDebitDocumentIdsByBillRunStatusIsVoidOrIsDeleteIsTrueAndDebitDocIdsIn(@Param("debitDocumentIds") List<Integer> debitDocumentIds);

    @Query("SELECT new com.savbill.revenuemanagement.InvoiceIntigration.SendInvoiceDTO(dd.id,dd.docnumber,dd.billdate,ddd.subtotal,dt.amount,dd.totalamount , mv.clientId , dd.customer.mvnoId , dd.customer.customerNid , dd.customer.customerVrn , dd.customer.pan , dd.customer.passportNo, dd.customer.drivingLicence , dd.customer.firstname , dd.customer.lastname , dd.customer.mobile) "+
        "FROM DebitDocument dd "+
        "JOIN DebitDocDetails ddd ON dd.id = ddd.debitdocumentid "+
        "LEFT JOIN DebitDocumentTAXRel dt ON dd.id = dt.debitdocumentid "+
            "LEFT JOIN dd.customer c " +
            "LEFT JOIN Mvno mv ON mv.id = CAST(c.mvnoId AS long) " +
        "WHERE dd.qrCode IS NULL and c.pan IS NOT NULL and dd.id IN :debitdocIds "+
        "ORDER BY dt.id ASC")
    List<SendInvoiceDTO> findDebitDocumentsWithTaxesAsList(@Param("debitdocIds") List<Integer> debitdocIds);

    @Query("select  new com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitDocNumberMappingPojo( t.id,t.docnumber,t.billrunstatus) from DebitDocument t where t.billrunstatus !='VOID'  and t.isDelete=false and t.paymentStatus!='Fully Paid' and t.customer.id in (:custIds)")
    List<DebitDocNumberMappingPojo> findDebitDocumentByCustId(List<Integer> custIds);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.DebitShowDocumentPojo(deb.id, deb.docnumber,deb.createdByName, deb.tax,deb.totalamount,deb.adjustedAmount,deb.subtotal,deb.billrunstatus, deb.customer.status) " +
            "FROM DebitDocument deb " +
            "WHERE deb.custpackrelid = :custpackrelid ")
    List<DebitShowDocumentPojo> findAllInvoiceByCustomerPlanMappingId(Integer custpackrelid);

    @Query("SELECT new com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment(d.id, d.totalamount, d.adjustedAmount,d.customer.id) " +
            "FROM DebitDocument d " +
            "WHERE d.customer.id IN :custIds " +
            "AND d.paymentStatus NOT IN ('Fully Paid', 'Cancelled') " +
            "AND (d.totalamount - d.adjustedAmount) >= 0")
    List<DebitDocumentDTOForAdjustment> getDebitDocForPaymentAdjustment(@Param("custIds") List<Integer> custIds);

    @Query("SELECT new com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentDTOForAdjustment(d.id, d.totalamount, d.adjustedAmount,d.customer.id) " +
            "FROM DebitDocument d " +
            "WHERE d.customer.id  = :custId " +
            "AND d.paymentStatus NOT IN ('Fully Paid', 'Cancelled') " +
            "AND d.billrunstatus NOT IN ('VOID') " +
            "AND (d.totalamount - d.adjustedAmount) >= 0")
    List<DebitDocumentDTOForAdjustment> getDebitDocForPaymentAdjustmentByCustid(@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query("UPDATE DebitDocument d SET d.docnumber = :number WHERE d.id = :debitDocId")
    void updateDebitDocumentNumber(@Param("debitDocId") Integer debitDocId, @Param("number") String number);

    @Modifying
    @Transactional
    @Query("UPDATE DebitDocument d SET d.billrunstatus = :status WHERE d.id = :debitDocId")
    void updateDebitDocumentBillRunStatus(@Param("debitDocId") Integer debitDocId, @Param("status") String status);

    @Query("SELECT d FROM DebitDocument d " +
            "WHERE d.customer.id = :custId " +
            "AND d.billrunstatus NOT IN :excludedStatuses " +
            "AND d.isDelete = false " +
            "AND d.paymentStatus IN :paymentStatuses")
    List<DebitDocument> findDebitDocumentsByCustId(
            @Param("custId") Integer custId,
            @Param("excludedStatuses") List<String> excludedStatuses,
            @Param("paymentStatuses") List<String> paymentStatuses);


    @Modifying
    @Transactional
    @Query("UPDATE DebitDocument d SET d.qrCode = :qrCode WHERE d.id = :debitDocId")
    void updateDebitDocumentQr(@Param("debitDocId") Integer  debitDocId, @Param("qrCode") String qrCode);

    @Query("Select  d from DebitDocument d where  d.custpackrelid = :custPlanmappigId")
    DebitDocument findByCprId(Integer custPlanmappigId);
    @Query( value = "select * from TBLTDEBITDOCUMENT t where t.billrunstatus not in (:status) ORDER BY t.debitdocumentid desc",nativeQuery = true)
    List<DebitDocument> findAllByBillRunStatusNotIn(List<String> status);

    @Query( value = "select debitdocumentid from TBLTDEBITDOCUMENT t where t.billrunstatus not in (:status) ORDER BY t.debitdocumentid desc",nativeQuery = true)
    List<Integer> findAllDebitDocIdByBillRunStatusNotIn(List<String> status);

    @Query( value = "select debitdocumentid from TBLTDEBITDOCUMENT t JOIN tblcustomers c ON t.subscriberid = c.custid where t.billrunstatus not in (:status) AND c.MVNOID = :mvnoId ORDER BY t.debitdocumentid desc",nativeQuery = true)
    List<Integer> findAllDebitDocIdByBillRunStatusNotInWithMvnoId(List<String> status, Integer mvnoId);

    @Query( value = "select * from TBLTDEBITDOCUMENT t where t.billrunstatus in (:status) ORDER BY t.debitdocumentid desc",nativeQuery = true)
    List<DebitDocument> findAllByBillRunStatusIn(List<String> status);

    @Query(value = "SELECT t.debitdocumentid, t.debitdocumentnumber, t.subscriberid FROM TBLTDEBITDOCUMENT t WHERE t.billrunstatus IN (:status) ORDER BY t.debitdocumentid DESC ", nativeQuery = true)
    List<Object[]> findDebitDocDetailsByBillRunStatusIn(List<String> status);

    @Query(value = "SELECT t.debitdocumentid, t.debitdocumentnumber, t.subscriberid FROM TBLTDEBITDOCUMENT t JOIN tblcustomers c ON t.subscriberid = c.custid WHERE t.billrunstatus IN (:status) AND c.MVNOID = :mvnoId ORDER BY t.debitdocumentid DESC ", nativeQuery = true)
    List<Object[]> findDebitDocDetailsByBillRunStatusInWithMvnoId(List<String> status, Integer mvnoId);


    @Query(value = "SELECT (t.totalamount - t.adjustedamount) FROM TBLTDEBITDOCUMENT t WHERE t.debitdocumentid < :debitDocId AND t.subscriberid = :custId ORDER BY t.debitdocumentid DESC LIMIT 1", nativeQuery = true)
    Double findPreviousInvoiceRemainingAmount(@Param("debitDocId") Integer debitDocId, @Param("custId") Integer custId);

    @Query(
            value = "SELECT t.plantype, t.validity " +
                    "FROM tblmpostpaidplan t " +
                    "WHERE t.POSTPAIDPLANID = (" +
                    "   SELECT r.planid " +
                    "   FROM tblcustpackagerel r " +
                    "   WHERE r.custpackageid = (" +
                    "       SELECT d.custpackrelid " +
                    "       FROM tbltdebitdocument d " +
                    "       WHERE d.debitdocumentid = :debitDocId))",
            nativeQuery = true)
    Object[] findPlanTypeByDebitDocumentId(@Param("debitDocId") Integer debitDocId);

    @Query(
            value = "SELECT t.startdate " +
                    "FROM tbltdebitdocument t " +
                    "WHERE t.subscriberid = :subscriberId " +
                    "ORDER BY t.debitdocumentid ASC LIMIT 1",
            nativeQuery = true)
    Timestamp findFirstDebitDocumentStartDate(@Param("subscriberId") Integer subscriberId);


    @Query(
            value = "SELECT t.billing_cycle " +
                    "FROM tbltcustchargehistory t " +
                    "WHERE t.cust_id = :custId " +
                    "AND t.plan_id = ( " +
                    "    SELECT c.planid " +
                    "    FROM tblcustpackagerel c " +
                    "    WHERE c.custpackageid = ( " +
                    "        SELECT d.custpackrelid " +
                    "        FROM tbltdebitdocument d " +
                    "        WHERE d.debitdocumentid = :debitDocumentId " +
                    "    ) " +
                    ") " +
                    "ORDER BY t.billing_cycle ASC LIMIT 1",
            nativeQuery = true)
    Integer findLowestBillingCycle(@Param("custId") Integer custId, @Param("debitDocumentId") Integer debitDocumentId);


    @Query(value = "SELECT t.plangroup FROM tblmpostpaidplan t " +
            "WHERE t.POSTPAIDPLANID = (" +
            "SELECT t1.planid FROM tblcustpackagerel t1 " +
            "WHERE t1.custpackageid = (" +
            "SELECT t2.custpackrelid FROM tbltdebitdocument t2 " +
            "WHERE t2.debitdocumentid = :debitDocId))", nativeQuery = true)
    String findPlanGroupByDebitDocumentId(@Param("debitDocId") Integer debitDocId);

    @Query(value = "SELECT t.offerprice FROM tblmpostpaidplan t " +
            "WHERE t.POSTPAIDPLANID = (" +
            "SELECT t1.planid FROM tblcustpackagerel t1 " +
            "WHERE t1.custpackageid = (" +
            "SELECT t2.custpackrelid FROM tbltdebitdocument t2 " +
            "WHERE t2.debitdocumentid = :debitDocId))", nativeQuery = true)
    Optional<Double> findOfferPriceByDebitDocumentId(@Param("debitDocId") Integer debitDocId);

    @Query(value = "SELECT t.NAME FROM tblmpostpaidplan t " +
            "WHERE t.POSTPAIDPLANID = (" +
            "SELECT t1.planid FROM tblcustpackagerel t1 " +
            "WHERE t1.custpackageid = (" +
            "SELECT t2.custpackrelid FROM tbltdebitdocument t2 " +
            "WHERE t2.debitdocumentid = :debitDocId))", nativeQuery = true)
    Optional<String> findPlanNameByDebitDocumentId(@Param("debitDocId") Integer debitDocId);

    @Modifying
    @Transactional
    @Query("UPDATE DebitDocument d SET d.document = :document WHERE d.id = :debitDocId")
    void updateDebitDocumentXmlDocument(@Param("debitDocId") Integer debitDocId, @Param("document") String document);

    @Query("SELECT new com.savbill.revenuemanagement.core.service.prepaid.DebitDocCustDTO(" +
            "d.id , d.custpackrelid, d.customer.username, d.totalamount) " +
            "FROM DebitDocument d WHERE d.id = :invoiceId")
    DebitDocCustDTO findDebitDocById(@Param("invoiceId") Integer invoiceId);



    @Query("SELECT d.docnumber FROM DebitDocument d WHERE d.id = :id")
    String findDocnumberById(@Param("id") Integer id);


    @Query("SELECT d.totalamount FROM DebitDocument d WHERE d.id = :id")
    Double findTotalAmountById(@Param("id") Integer id);

    @Query("SELECT d FROM DebitDocument d " +
            "WHERE d.customer.id = :subscriberId " +
            "AND d.paymentStatus != 'Paid' " +
            "AND d.isDelete = false " +
            "AND d.billrunstatus NOT IN ('VOID', 'CANCELLED')")
    List<DebitDocument> findUnpaidInvoicesBySubscriberId(@Param("subscriberId") Integer subscriberId, Pageable pageable);

    List<DebitDocument> findByCustomer_Id(Integer custId);

    @Query("SELECT d.id AS id, d.createdByName AS createdByName, d.docnumber AS docnumber, d.tax AS tax, " +
            "d.totalamount AS totalamount, d.adjustedAmount AS adjustedAmount, d.status AS status, " +
            "d.endate AS endate, d.custpackrelid AS custpackrelid, d.startdate AS startdate, " +
            "d.subtotal AS subtotal, d.discount AS discount, d.isDirectChargeInvoice AS isDirectChargeInvoice " +
            "FROM DebitDocument d " +
            "WHERE d.customer.id = :customerid " +
            "AND d.billrunstatus NOT IN ('Cancelled','VOID')" +
            "AND d.custpackrelid NOT IN (SELECT c.id FROM CustPlanMappping c WHERE c.custPlanStatus = 'STOP') " +
            "ORDER BY d.id DESC")
    List<DebitDocumentCreditNoteView> findAllByCustomerForCreditNoteView(@Param("customerid") Integer customerid);


    @Query("SELECT d FROM DebitDocument d WHERE d.isKraSynced = false AND d.billrunstatus NOT IN ('VOID')")
    List<DebitDocument> findUnsyncedKraInvoices();

}
