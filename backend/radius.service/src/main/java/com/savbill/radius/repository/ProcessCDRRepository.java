package com.savbill.radius.repository;

import com.savbill.radius.entity.ProcessCDR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessCDRRepository extends JpaRepository<ProcessCDR, Integer>, QuerydslPredicateExecutor<ProcessCDR> {

}
