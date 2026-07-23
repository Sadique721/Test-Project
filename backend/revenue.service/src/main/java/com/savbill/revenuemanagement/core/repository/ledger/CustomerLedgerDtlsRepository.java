package com.savbill.revenuemanagement.core.repository.ledger;

import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerLedgerDtlsRepository extends JpaRepository<CustomerLedgerDtls, Integer> {
    @Query("select led from CustomerLedgerDtls led where led.customer.id = :id and led.debitdocid = :debitdocId order by led.id DESC")
    Optional<CustomerLedgerDtls> findBYCustomerId(Integer id,Integer debitdocId);

    List<CustomerLedgerDtls> findByCustomerIdAndIsDelete(Integer custId, boolean b);

    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totaldebit from tbltcustledgerdetails as t where t.CUSTID=:custId and date(t.CREATEDATE) <:lastDate", nativeQuery = true)
    Double findOpeningAmount(@Param("lastDate") LocalDate lastDate, @Param("custId") Integer custId);

    @Query(value = "select * from tbltcustledgerdetails t where date(t.CREATEDATE) between :startDate AND :endDate AND t.CUSTID=:custId AND t.is_delete=:is_delete order by t.CREATEDATE asc", nativeQuery = true)
    List<CustomerLedgerDtls> findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id, @Param("is_delete") boolean isDelete);

    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where is_void = false AND t.is_delete = false and (t.CUSTID=:custId and date(t.CREATEDATE) BETWEEN :startDate AND :endDate) order by t.CREATEDATE asc", nativeQuery = true)
    Double findClsoingAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id);

    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where t.CUSTID=:custId ", nativeQuery = true)
    Double findWalletAmt(@Param("custId") Integer custId);

//    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where is_void = false  AND t.is_delete = false and t.CUSTID=:custId order by t.CREATEDATE asc ", nativeQuery = true)
//    Double findClsoingAmountById(@Param("custId") Integer custId);

    @Query(value = "select ( COALESCE(sum(case when t.transtype='DR' then t.amount else 0 end),0)- COALESCE(sum(case when t.transtype='CR' then t.amount else 0 end),0))as totalAmount from tbltcustledgerdetails as t where is_void = false  AND t.is_delete = false and t.CUSTID=:custId order by t.CREATEDATE asc ", nativeQuery = true)
    Double findClsoingAmountById(@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query("UPDATE CustomerLedgerDtls c SET c.isDelete = true WHERE c.customer.id = :customerId AND c.debitdocid = :debitdocId")
    int markAsDeletedByCustomerIdAndDebitDocId(@Param("customerId")Integer customerId,@Param("debitdocId") Integer debitdocId);

    @Query(value = "SELECT ( " +
            "SUM(CASE WHEN t.transtype = 'CR' THEN t.amount ELSE 0 END) - " +
            "SUM(CASE WHEN t.transtype = 'DR' THEN t.amount ELSE 0 END) " +
            ") AS totalAmount " +
            "FROM tbltcustledgerdetails AS t " +
            "WHERE t.is_void = FALSE " +
            "AND t.is_delete = FALSE " +
            "AND (t.from_id = :childId OR t.to_id = :childId) " +
            "ORDER BY t.CREATEDATE ASC", nativeQuery = true)
    Double findwalletAmountById(@Param("childId") Integer childId);

    @Query(value = "SELECT * FROM tbltcustledgerdetails AS t " +
            "WHERE t.is_void = FALSE " +
            "AND t.is_delete = FALSE " +
            "AND (t.from_id = :childId OR t.to_id = :childId) " +
            "ORDER BY t.CUSTLEDGERDTLSID ASC",
            countQuery = "SELECT COUNT(*) FROM tbltcustledgerdetails AS t " +
                    "WHERE t.is_void = FALSE " +
                    "AND t.is_delete = FALSE " +
                    "AND (t.from_id = :childId OR t.to_id = :childId)",
            nativeQuery = true)
    Page<CustomerLedgerDtls> findChildLedgerById(@Param("childId") Integer childId, Pageable pageable);

    @Query(value = " SELECT * FROM TBLTCUSTLEDGERDETAILS WHERE (from_id = :childId OR to_id = :childId) AND is_delete = false AND (is_void IS NULL OR is_void = false) ORDER BY CUSTLEDGERDTLSID ASC", nativeQuery = true)
    List<CustomerLedgerDtls> findAllLedgerByChildId(@Param("childId") Integer childId);


}
