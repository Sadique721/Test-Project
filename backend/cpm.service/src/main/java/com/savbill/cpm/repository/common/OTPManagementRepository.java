package com.savbill.cpm.repository.common;

import com.savbill.cpm.model.common.OTPManagement;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

@JaversSpringDataAuditable
public interface OTPManagementRepository extends JpaRepository<OTPManagement,Long>, QuerydslPredicateExecutor<OTPManagement> {
}
