package com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustAccountProfileRepository extends JpaRepository<CustAccountProfile,Long>, QuerydslPredicateExecutor<CustAccountProfile>, JpaSpecificationExecutor<CustAccountProfile> {
    boolean existsByNameAndIsDeleteFalse(String name);
    Page<CustAccountProfile> findAllByMvnoIdAndIsDeleteFalse(Pageable pageable, Integer mvnoId);
    Page<CustAccountProfile> findAllByIsDeleteFalse(Pageable pageable);
    List<CustAccountProfile> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);

}
