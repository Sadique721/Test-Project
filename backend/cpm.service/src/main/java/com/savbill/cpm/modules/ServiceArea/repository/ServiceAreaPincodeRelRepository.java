package com.savbill.cpm.modules.ServiceArea.repository;

import com.savbill.cpm.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ServiceAreaPincodeRelRepository  extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {
}
