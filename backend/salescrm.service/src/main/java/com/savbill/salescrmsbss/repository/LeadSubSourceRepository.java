package com.savbill.salescrmsbss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.LeadSubSource;

@Repository
public interface LeadSubSourceRepository extends JpaRepository<LeadSubSource, Long>{

	@Query(name = "select * from TBLTLEADSUBSOURCE where lead_source_id=:leadSourceId")
	List<LeadSubSource> findByLeadSourceId(@Param("leadSourceId") Long leadSourceId);
}
