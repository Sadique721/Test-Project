package com.savbill.integrationsystem.navmaster.repository;

import com.savbill.integrationsystem.navmaster.entity.NAVMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NAVMasterRepo extends JpaRepository<NAVMaster, Long> , QuerydslPredicateExecutor<NAVMaster> {
}
