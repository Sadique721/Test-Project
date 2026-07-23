package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.BillGenFinalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BillGenFinalDataRepo extends JpaRepository<BillGenFinalData, Integer>, QuerydslPredicateExecutor<BillGenFinalData> {

    //    @Query("select b from BillGenFinalData b where  b.date between (:startDate) and (:endDate) group by (:columnNames) ")
//    Page<BillGenFinalData> getAggregation(@Param(value = "columnNames") String columnNames, @Param(value = "startDate") LocalDate STARTDATE, @Param(value = "endDate") LocalDate ENDDATE);
    BillGenFinalData findFirstByAddedDateAndIsPushedTrue(LocalDate localDate);
}
