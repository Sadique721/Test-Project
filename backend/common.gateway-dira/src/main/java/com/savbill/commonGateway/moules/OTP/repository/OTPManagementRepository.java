package com.savbill.commonGateway.moules.OTP.repository;

import com.savbill.commonGateway.moules.OTP.domain.OTPManagement;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

@JaversSpringDataAuditable
@Repository
public interface OTPManagementRepository extends JpaRepository<OTPManagement,Long>, QuerydslPredicateExecutor<OTPManagement> {


    OTPManagement findByProfileNameAndMvnoId(String profileName, Integer mvnoId);

    OTPManagement findByMvnoId(Integer mvnoId);

}
