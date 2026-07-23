package com.savbill.cpm.modules.staffLedgerDetails.repository;


import com.savbill.cpm.modules.staffLedgerDetails.Service.StaffLedgerDetailsService;
import com.savbill.cpm.modules.staffLedgerDetails.entity.StaffLedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffLedgerDetailsRepository extends JpaRepository<StaffLedgerDetails,Long>, QuerydslPredicateExecutor<StaffLedgerDetails> {

    @Query(value = "select * from tblmstaffledgerdetails t where t.staff_id =:staff_id and t.id IS NOT NULL", nativeQuery = true)
    List<StaffLedgerDetailsService> findbyStaffId(@Param("staff_id") Integer id);

   List<StaffLedgerDetails> findAllByTransactionType(String transactiontype);

   StaffLedgerDetails findById(Integer id);

}
