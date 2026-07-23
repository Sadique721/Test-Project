package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustMacMappping;

@Repository
public interface CustMacMapppingRepository extends JpaRepository<CustMacMappping, Integer>{

	@Query(name = "select * from tblcustmacmapping where lead_master_id=:leadId")
	List<CustMacMappping> findByLeadMasterId(@Param("leadId") Long leadId);
}
