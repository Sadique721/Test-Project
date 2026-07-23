package com.diameter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.diameter.model.CustVoiceDetails;

@Repository
public interface CustVoiceDetailsRepository extends JpaRepository<CustVoiceDetails, Integer>{
	
	@Query("SELECT c FROM CustVoiceDetails c WHERE c.custPlanMappping.id = :custPackageId")
	List<CustVoiceDetails> getByCustPackageId(@Param("custPackageId") Long custPackageId);
	
}
