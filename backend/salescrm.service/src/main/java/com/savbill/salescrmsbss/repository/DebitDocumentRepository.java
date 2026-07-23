package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.DebitDocument;

@Repository
public interface DebitDocumentRepository extends JpaRepository<DebitDocument, Integer>{

	@Query(name = "select * from TBLTDEBITDOCUMENT where lead_master_id=:leadId")
	List<DebitDocument> findByLeadMasterId(@Param("leadId") Long leadId);
}
