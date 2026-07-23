package com.savbill.cpm.modules.payments.repository;

import com.savbill.cpm.modules.subscriber.Domain.ServiceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAuditRepository extends JpaRepository<ServiceAudit, Long> , QuerydslPredicateExecutor<ServiceAudit> {

    @Query(value = "select * from tbltserviceaudit t where t.custservicemappingid  =:custpackid" , nativeQuery = true)
    List<ServiceAudit> findResonIdByCpr(@Param("custpackid") Integer custpackid);

//    @Query(value ="select * from tbltserviceaudit t where t.custservicemappingid =:custpackid" ,
//            countQuery = "select count(*) from tbltserviceaudit t where t.custservicemappingid = :servicemappingId",
//            nativeQuery = true)
//    Page<ServiceAudit> findAll(PageRequest pageRequest,@Param("custpackid")Integer servicemappingId);
}


