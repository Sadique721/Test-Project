package com.savbill.radius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.CustomerQosPolicyMapping;


@Repository
public interface CustomerQosPolicyMappingRepository extends JpaRepository<CustomerQosPolicyMapping, Long>,QuerydslPredicateExecutor<CustomerQosPolicyMapping> {

}
