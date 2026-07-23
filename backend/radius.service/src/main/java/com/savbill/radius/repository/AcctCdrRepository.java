package com.savbill.radius.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.AcctCdr;

@Repository
public interface AcctCdrRepository extends JpaRepository<AcctCdr, Long>, QuerydslPredicateExecutor<AcctCdr> {

    @Query("SELECT a FROM AcctCdr a WHERE (:userName is null or a.userName LIKE %:userName%) and (:framedIp is null or a.framedIpAddress LIKE %:framedIp%)")
    List<AcctCdr> findByUserName(@Param("userName") String userName,@Param("framedIp") String framedIp);

    @Query(value = "SELECT * FROM tbltacctcdr a WHERE  a.createdate <= :toDate AND a.createDate  >= :fromDate ",nativeQuery = true)
    List<AcctCdr> getDailyConsumeDataOfLastSevenDays(@Param("fromDate") Date fromDate,@Param("toDate") Date toDate);
    
//    //@Query("SELECT * FROM AcctCdr WHERE createdate <= fromDate")
//    List<AcctCdr> findByFromDate(@Param("fromDate") Timestamp fromDate);
//    
//    //@Query("SELECT * FROM AcctCdr WHERE createdate >= toDate")
//    List<AcctCdr> findByToDate(@Param("toDate") Timestamp toDate);

    //@Query("SELECT * FROM AcctCdr WHERE createdate <= fromDate AND createdate >= toDate")
//    List<AcctCdr> findByFromAndToDate(@Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
}
