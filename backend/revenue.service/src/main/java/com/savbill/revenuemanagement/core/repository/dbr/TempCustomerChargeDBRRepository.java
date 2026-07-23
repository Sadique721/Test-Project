package com.savbill.revenuemanagement.core.repository.dbr;


import com.savbill.revenuemanagement.core.entity.DBR.TempCustomerChargeDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempCustomerChargeDBRRepository extends JpaRepository<TempCustomerChargeDBR, Long> {

    @Query(value ="SELECT * FROM tbltmpcustomerchargedbr t WHERE t.cprid IN :cprIds ", nativeQuery = true)
    List<TempCustomerChargeDBR> findAll(@Param("cprIds") List<Long> cprIds);
}