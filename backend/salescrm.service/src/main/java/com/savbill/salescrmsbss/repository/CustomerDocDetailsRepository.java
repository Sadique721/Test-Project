package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustomerDocDetails;

@Repository
public interface CustomerDocDetailsRepository extends JpaRepository<CustomerDocDetails, Long>{

	@Query(name = "select * from tblcustdocdetails where lead_master_id=:leadId")
	List<CustomerDocDetails> findByLeadMasterId(@Param("leadId") Long leadId);
}
