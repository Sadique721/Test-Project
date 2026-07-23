package com.savbill.revenuemanagement.core.Mvno.repository;

import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MvnoRepository extends JpaRepository<Mvno, Long>, QuerydslPredicateExecutor<Mvno> {

    Page<Mvno> findAll(Pageable pageable);

    @Query("select t.username from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);
    @Query("select t.mvnoPaymentDueDays from Mvno t where t.custInvoiceRefId=:id")
    Integer findDuedaysByMvnoId(Integer id);

    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void updatesMvnoidIsp(@Param("oldMvnoid") long oldMvnoid, @Param("newMvnoid") long newMvnoid);

    @Query("select t.fullName from Mvno t where t.custInvoiceRefId=:id")
    String findFullnameBYmvnoCustId(Integer id);

    @Query("select t.clientId from Mvno t where t.custInvoiceRefId=:id")
    String findClientIdByMvnoCustId(Integer id);

    @Query("select t.id from Mvno t")
    List<Integer> findAllMvnoIds();

    @Query("select t.ispCommissionPercentage from Mvno t where t.custInvoiceRefId=:id")
    Double findIspCommissionPercentageByMvnoId(Integer id);

    @Query("select t.custInvoiceRefId from Mvno t where t.custInvoiceRefId is not null")
    List<Integer> findCustInvoiceRefNumber();
}
