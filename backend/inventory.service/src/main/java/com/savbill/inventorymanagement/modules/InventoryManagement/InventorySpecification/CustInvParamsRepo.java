package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

@JaversSpringDataAuditable
public interface CustInvParamsRepo  extends JpaRepository<CustInvParams,Long>, QuerydslPredicateExecutor<CustInvParams> {

//    List<CustInvParams> findAllByCustSerMapId(Long custSerMapId);

    List<CustInvParams> findAllByCustInvId(Long custInvMapId);

    @Query(value = "SELECT * FROM tblmcustinventoryparams WHERE cust_inv_id = :custInvMapId", nativeQuery = true)
    List<CustInvParams> findAllByCustomerInventoryId(@Param("custInvMapId") Long custInvMapId);

    @Query(value = "SELECT * FROM tblmcustinventoryparams WHERE cust_serv_id = :custSerMapId", nativeQuery = true)
    List<CustInvParams> findAllByCustSerMapId(@Param("custSerMapId") Long custSerMapId);
}
