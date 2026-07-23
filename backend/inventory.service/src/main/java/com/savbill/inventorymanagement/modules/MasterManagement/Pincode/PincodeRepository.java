package com.savbill.inventorymanagement.modules.MasterManagement.Pincode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PincodeRepository extends JpaRepository<Pincode, Long>, QuerydslPredicateExecutor<Pincode>  {

    List<Pincode> findAllByIsDeletedIsFalseAndStatusAndIdIn(String status, List<Long> id);
    List<Pincode> findAllByIsDeletedIsFalseAndStatusAndIdInAndMvnoIdIn(String status, List<Long> id, List<Integer> mvnoId);
    @Query("SELECT p.pincode FROM Pincode p WHERE p.id = :id")
    String findPincodeById(@Param("id") Long id);
}
