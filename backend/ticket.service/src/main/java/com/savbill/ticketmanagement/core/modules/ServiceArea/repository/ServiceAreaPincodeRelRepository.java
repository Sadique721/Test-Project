package com.savbill.ticketmanagement.core.modules.ServiceArea.repository;


import com.savbill.ticketmanagement.core.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAreaPincodeRelRepository  extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {
}
