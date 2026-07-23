package com.savbill.partnermanagement.modules.MasterManagement.ServiceAreaPincodeMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ServiceAreaPincodeRelRepository extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {
}
