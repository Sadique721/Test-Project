package com.savbill.radius.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.CustomerLocationMapping;

@Repository
public interface CustomerLocationMappingRepository extends JpaRepository< CustomerLocationMapping, Long>, QuerydslPredicateExecutor< CustomerLocationMapping> 
{
	List<CustomerLocationMapping> findByCustId(Long customerId);
}
