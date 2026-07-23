package com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface PasswordRepository extends JpaRepository<PasswordPolicy, Long>, QuerydslPredicateExecutor<PasswordPolicy>, JpaSpecificationExecutor<PasswordPolicy> {

    List<PasswordPolicy> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);

    Page<PasswordPolicy> findAllByIsDeleteFalse(Pageable pageable);

    Page<PasswordPolicy> findAllByMvnoIdAndIsDeleteFalse(Pageable pageable, Integer mvnoId);

    boolean existsByNameAndIsDeleteFalse(String name);

    Optional<PasswordPolicy> findById(Long passwordPolicyId);

    @Query("SELECT p.id FROM PasswordPolicy p WHERE p.mvnoId = :mvnoId")
    Optional<Long> findPasswordPolicyIdByMvnoId(Long mvnoId);

}

