package com.savbill.cpm.modules.CustomerDBR.repository;

import com.savbill.cpm.modules.CustomerDBR.domain.CustomerDBR;
import com.savbill.cpm.modules.CustomerDBR.model.CustomMonthlyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomMonthlyRevenueRepository extends JpaRepository<CustomMonthlyRevenue, Long>, QuerydslPredicateExecutor<CustomMonthlyRevenue> {

    @Query(value = " select * from tblcustomerdbr t " + "where start_date between :startdate and :endate", nativeQuery = true)
    List<CustomerDBR> getdbrdeatails(@Param(value = "startdate") LocalDate startdate,
                                     @Param(value = "endate") LocalDate endate);
}
