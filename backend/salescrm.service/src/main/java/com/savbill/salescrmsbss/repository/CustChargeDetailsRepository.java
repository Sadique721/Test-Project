package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustChargeDetails;

@JaversSpringDataAuditable
@Repository
public interface CustChargeDetailsRepository extends JpaRepository<CustChargeDetails, Integer>{

	@Query(name = "select * from tblcustchargedtls where lead_master_id=:leadId")
	List<CustChargeDetails> findByLeadMasterId(@Param("leadId") Long leadId);
}
