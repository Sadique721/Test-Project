package com.savbill.cpm.modules.CustomerDBR.repository;

import com.savbill.cpm.modules.CustomerDBR.domain.TempCustomerDBR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TempCustomerDBRRepository extends JpaRepository<TempCustomerDBR, Long>, QuerydslPredicateExecutor<TempCustomerDBR>
{

}

