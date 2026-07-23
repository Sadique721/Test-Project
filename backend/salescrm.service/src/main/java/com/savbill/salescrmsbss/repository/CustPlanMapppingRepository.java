package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustPlanMappping;
import com.savbill.salescrmsbss.entity.LeadMaster;

@Repository
public interface CustPlanMapppingRepository extends JpaRepository<CustPlanMappping, Integer>{

	
	@Query(name = "select * from TBLCUSTPACKAGEREL where lead_master_id=:leadId")
	List<CustPlanMappping> findByLeadMasterId(@Param("leadId") Long leadId);

	List<CustPlanMappping> findAllByLeadMasterAndPlanId(LeadMaster leadMaster, Integer planId);

	List<CustPlanMappping> findByPlanId(Integer planId);
}
