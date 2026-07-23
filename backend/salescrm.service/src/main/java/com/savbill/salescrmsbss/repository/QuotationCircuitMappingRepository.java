package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.QuotationCircuitMapping;
import com.savbill.salescrmsbss.entity.QuotationDetails;

@Repository
public interface QuotationCircuitMappingRepository extends JpaRepository<QuotationCircuitMapping, Long> {
	
	List<QuotationCircuitMapping> findAllByQuotationDetails(QuotationDetails quotation);

}
