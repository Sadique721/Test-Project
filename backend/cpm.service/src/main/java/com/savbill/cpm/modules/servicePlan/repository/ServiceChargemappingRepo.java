package com.savbill.cpm.modules.servicePlan.repository;

import com.savbill.cpm.model.postpaid.ServiceChargeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargemappingRepo extends JpaRepository<ServiceChargeMapping, Long >, QuerydslPredicateExecutor<ServiceChargeMapping>
{

}
