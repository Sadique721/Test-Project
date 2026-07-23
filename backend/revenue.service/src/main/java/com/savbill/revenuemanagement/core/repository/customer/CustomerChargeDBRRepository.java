package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerChargeDBRRepository extends JpaRepository<CustomerChargeDBR, Long>{

    List<CustomerChargeDBR> findAllByServiceIdIn(List<Long> serviceIds);

    boolean existsByCprid(Long cprid);


//    @Query(value = "SELECT CustomerChargeDBR FROM CustomerChargeDBR c where c.cprid in :custPackIds and c.invoiceId = :documentId and c.startdate >= :from and c.startdate <= :to")
    List<CustomerChargeDBR> findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(List<Long> custPackIds, Long documentId, LocalDate from, LocalDate to);

    @Query(value = "SELECT * FROM tblcustomerdbr " +
            "WHERE cprid IN (:cprIds) " +
            "AND invoiceid = :invoiceId " +
            "AND start_date BETWEEN :currentDate AND :endDate",
            nativeQuery = true)
    List<CustomerChargeDBR> findAllByCpridInAndInvoiceIdAndStartdateBetween(
            @Param("cprIds") List<Long> cprIds,
            @Param("invoiceId") Long invoiceId,
            @Param("currentDate") LocalDate currentDate,
            @Param("endDate") LocalDate endDate);

    //@Query(value = "SELECT CustomerChargeDBR FROM CustomerChargeDBR c where c.invoiceId = :documentId and c.startdate >= :from and c.startdate <= :to and cprid IS NOT NULL")
    List<CustomerChargeDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long documentId, LocalDate from, LocalDate to);


    @Query(value = "SELECT * FROM tblcustomerchargedbr c where c.invoiceid = :documentId and c.start_date >= :from and c.start_date <= :to and c.cprid IS NOT NULL",nativeQuery = true)
    List<CustomerChargeDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual001(@Param("documentId") Long documentId,@Param("from") LocalDate from,@Param("to") LocalDate to);

    @Query(value = "SELECT * FROM tblcustomerchargedbr c where c.invoiceid = :documentId and c.start_date >= :from and c.start_date <= :to and c.is_direct_charge=false and c.cprid IS NOT NULL",nativeQuery = true)
    List<CustomerChargeDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual002(@Param("documentId") Long documentId,@Param("from") LocalDate from,@Param("to") LocalDate to);


//    @Query(value = "SELECT CustomerChargeDBR FROM CustomerChargeDBR c where c.cprid = :cprId and c.startdate >= :from and c.startdate <= :to")
    List<CustomerChargeDBR> findAllByCpridAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long cprId, LocalDate from, LocalDate to);

    List<CustomerChargeDBR> findAllByInvoiceId(Long invoiceId);

    List<CustomerChargeDBR>  findAllByCpridInAndStartdateAfter(List<Long> custPackIds,LocalDate from);

    List<CustomerChargeDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCustInvMappingIdIn(Long documentId, LocalDate from, LocalDate to,List<Long> idlist);

    Object findAllByInvoiceIdAndStartdateBetween(Integer invoiceId,LocalDate from, LocalDate to);

    List<CustomerChargeDBR> findAllByCprid(Long cprId);
}
