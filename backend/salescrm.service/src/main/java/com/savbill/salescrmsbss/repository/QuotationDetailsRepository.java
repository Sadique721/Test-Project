package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.QuotationDetails;

@JaversSpringDataAuditable
@Repository
public interface QuotationDetailsRepository
		extends JpaRepository<QuotationDetails, Long>, QuerydslPredicateExecutor<QuotationDetails> {

	List<QuotationDetails> findAllByLeadId(Long leadId);

}
