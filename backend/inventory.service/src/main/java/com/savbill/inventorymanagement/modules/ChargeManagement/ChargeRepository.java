package com.savbill.inventorymanagement.modules.ChargeManagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer>, QuerydslPredicateExecutor<Charge> {
    List<Charge> findAllByChargetypeAndIsDeleteIsFalse(String chargeTypeCustomerDirect);

    @Query("select t from Charge t where t.isDelete=false")
    List<Charge> findAll();

    @Query(value = "select * from tblmcharges as t where t.is_delete = false"
            , nativeQuery = true
            , countQuery = "select count(*) from tblmcharges as t where t.is_delete = false")
    Page<Charge> findAll(Pageable pageable);

    @Query("update Charge b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    Charge findByName(String name);

    @Query(value = "SELECT c.taxid FROM tblmcharges c WHERE c.chargeid = :chargeId", nativeQuery = true)
    Integer findTaxIdByChargeId(@Param("chargeId") Integer chargeId);
}
