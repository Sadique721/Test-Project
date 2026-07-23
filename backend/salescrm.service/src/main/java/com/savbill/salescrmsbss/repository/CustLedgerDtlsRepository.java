package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CustLedgerDtls;

@Repository
public interface CustLedgerDtlsRepository extends JpaRepository<CustLedgerDtls, Integer>{
	
	@Query(name = "select * from TBLTCUSTLEDGERDTLS where lead_master_id=:leadId")
	List<CustLedgerDtls> findByLeadMasterId(@Param("leadId") Long leadId);
}
