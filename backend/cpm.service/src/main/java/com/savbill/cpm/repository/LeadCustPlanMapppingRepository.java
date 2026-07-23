package com.savbill.cpm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.lead.LeadCustPlanMappping;

@Repository
//@JaversSpringDataAuditable
public interface LeadCustPlanMapppingRepository extends JpaRepository<LeadCustPlanMappping, Integer>{

	@Query(name = "select * from TBLLEADCUSTPACKAGEREL where lead_master_id=:leadId")
	List<LeadCustPlanMappping> findByLeadMasterId(@Param("leadId") Long leadId);
}
