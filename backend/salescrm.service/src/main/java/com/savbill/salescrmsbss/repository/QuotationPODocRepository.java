package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.QuotationPODoc;

@JaversSpringDataAuditable
@Repository
public interface QuotationPODocRepository extends JpaRepository<QuotationPODoc, Long>{

	List<QuotationPODoc> findAllByQuotationDetailId(Long quotationDetailId);

}

