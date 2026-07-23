package com.savbill.radius.repository;

import com.savbill.radius.entity.CustomerTimeBasePolicyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTimBasePolicyMappingRepository extends JpaRepository<CustomerTimeBasePolicyMapping, Long>, QuerydslPredicateExecutor<CustomerTimeBasePolicyMapping> {
    List<CustomerTimeBasePolicyMapping> findCustomerTimeBasePolicyMappingByCustomerId(Long customerId);

}
