package com.savbill.partnermanagement.modules.MasterManagement.BranchServiceAreaRel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchServiceAreaMappingRepository extends JpaRepository<BranchServiceAreaMapping, Long>, QuerydslPredicateExecutor<BranchServiceAreaMapping> {
    List<BranchServiceAreaMapping> findAllByBranchId(Integer branchId);
}
