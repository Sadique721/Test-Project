package com.savbill.inventorymanagement.modules.MasterManagement.Branch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, QuerydslPredicateExecutor<Branch> {
    List<Branch> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(String status, List<Integer> mvnoId, List<Long> id);
    List<Branch> findAllByIsDeletedIsFalseAndStatusAndIdIn(String status, List<Long> id);
    @Query("SELECT b.name FROM Branch b WHERE b.id = :id")
    String findNameById(@Param("id") Long id);
}
