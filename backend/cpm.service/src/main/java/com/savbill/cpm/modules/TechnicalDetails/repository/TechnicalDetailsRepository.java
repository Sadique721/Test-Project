package com.savbill.cpm.modules.TechnicalDetails.repository;

import com.savbill.cpm.modules.TechnicalDetails.domain.TechnicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalDetailsRepository extends JpaRepository<TechnicalDetails,Long>, QuerydslPredicateExecutor<TechnicalDetails> {

}
