package com.diameter.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diameter.model.CustSmsDetails;

@Repository
public interface CustSmsDetailsRepository extends JpaRepository<CustSmsDetails, Integer>{
	
	@Query("SELECT c FROM CustSmsDetails c WHERE c.custPlanMappping.id = :custPackageId")
	List<CustSmsDetails> getByCustPackageId(@Param("custPackageId") Long custPackageId);

}
