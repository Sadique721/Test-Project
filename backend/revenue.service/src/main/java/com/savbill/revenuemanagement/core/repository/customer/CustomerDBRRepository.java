package com.savbill.revenuemanagement.core.repository.customer;


import com.savbill.revenuemanagement.core.dto.dbr.AggregateCount;
import com.savbill.revenuemanagement.core.dto.invoice.CustomerDBRPartial;
import com.savbill.revenuemanagement.core.entity.customers.CustomerDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerDBRRepository extends JpaRepository<CustomerDBR, Long> {

//    @Query(value = "SELECT CustomerDBR FROM CustomerDBR c where c.cprid in :custPackIds and c.invoiceId = :documentId and c.startdate >= :from and c.startdate <= :to")
    List<CustomerDBR> findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual( List<Long> custPackIds, Long documentId, LocalDate from, LocalDate to);

//    @Query(value = "SELECT CustomerDBR FROM CustomerDBR c where c.invoiceId = :documentId and c.startdate >= :from and c.startdate <= :to")
    List<CustomerDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long documentId, LocalDate from, LocalDate to);

    List<CustomerDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(Long documentId, LocalDate from, LocalDate to);

    @Query(value = "SELECT * FROM tblcustomerdbr c where c.invoiceid = :documentId and c.start_date >= :from and c.start_date <= :to and c.cprid IS NOT NULL",nativeQuery = true)
    List<CustomerDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull001(@Param("documentId") Long documentId,@Param("from") LocalDate from,@Param("to") LocalDate to);

    @Query(value = "SELECT * FROM tblcustomerdbr c where c.invoiceid = :documentId and c.start_date >= :from and c.start_date <= :to and c.is_direct_charge=false and c.cprid IS NOT NULL",nativeQuery = true)
    List<CustomerDBR> findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull002(@Param("documentId") Long documentId,@Param("from") LocalDate from,@Param("to") LocalDate to);



    //    @Query(value = "SELECT CustomerDBR FROM CustomerDBR c where c.cprid = :cprId and c.startdate >= :from and c.startdate <= :to")
    List<CustomerDBR> findAllByCpridAndStartdateGreaterThanEqualAndStartdateLessThanEqual(Long cprId, LocalDate from, LocalDate to);

    List<CustomerDBR> findAllByInvoiceId(Long invoiceId);
    List<CustomerDBR>findAllByInvoiceIdAndStartdateBetween(Long invoiceId,LocalDate from,LocalDate to );

    @Query(value = "select *  from tblcustomerdbr t " +
            "where (start_date between :startdate and :endate) and custid =:custid", nativeQuery = true)
    List<CustomerDBR> getbyCustid(@Param(value = "startdate") LocalDate startdate,
                                  @Param(value = "endate") LocalDate endate,
                                  @Param(value = "custid") Long custid);

    @Query(value = "select *  from tblcustomerdbr t " +
            "where (start_date=:startdate) and custid =:custid order by start_date", nativeQuery = true)
    List<CustomerDBR> getbyCustid(@Param(value = "startdate") LocalDate startdate,
                                  @Param(value = "custid") Long custid);

    @Query(value = "select *  from tblcustomerdbr t " +
            "where (start_date<=:startdate) and custid =:custid order by start_date", nativeQuery = true)
    List<CustomerDBR> getbyCustid1(@Param(value = "startdate") LocalDate startdate,
                                  @Param(value = "custid") Long custid);

    List<CustomerDBR> findAllByCpridInAndStartdateAfter(List<Long> custPackIds,LocalDate from);

    @Query(value = "SELECT * FROM tblcustomerdbr  WHERE custid=:custId",nativeQuery = true)
    List<CustomerDBR> getAllByCustomerId(@Param(value = "custId") Integer custId);


    @Query(value = "SELECT mvnoid as mvnoId, buid as buId, service_area as serviceAreaId FROM tblcustomerdbr  WHERE start_date=:startDate GROUP BY mvnoid, buid, service_area",nativeQuery = true)
    List<AggregateCount> getAllByAggregateByDate(@Param(value = "startDate") LocalDate startDate);

    @Query("SELECT c FROM CustomerDBR c WHERE c.invoiceId IN :invoiceIds AND c.startdate BETWEEN :startDate AND :endDate AND c.cprid IS NOT NULL")
    List<CustomerDBR> findAllByInvoiceIdsAndStartDateRange(@Param("invoiceIds") List<Long> invoiceIds,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT d.invoiceId as debitDocId, d.startdate as startdate, d.pendingamt as pendingamt, d.dbr as dbr " +
            "FROM CustomerDBR d " +
            "WHERE d.invoiceId IN :docIds AND d.startdate <= :today")
    List<CustomerDBRPartial> findAllByDocIdsAndDate(@Param("docIds") List<Long> docIds,
                                                    @Param("today") LocalDate today);

}
