package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.CreditDocument;

@Repository
public interface CreditDocumentRepository extends JpaRepository<CreditDocument, Integer>{

	@Query(name = "select * from TBLTCREDITDOC where lead_master_id=:leadId")
	List<CreditDocument> findByLeadMasterId(@Param("leadId") Long leadId);
}
