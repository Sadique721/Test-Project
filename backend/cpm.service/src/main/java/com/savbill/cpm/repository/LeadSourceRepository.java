package com.savbill.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.lead.LeadSource;

@Repository
//@JaversSpringDataAuditable
public interface LeadSourceRepository extends JpaRepository<LeadSource, Long>{

}
