package com.savbill.cpm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.lead.LeadMaster;

@Repository
//@JaversSpringDataAuditable
public interface LeadMasterRepository extends JpaRepository<LeadMaster, Long>, QuerydslPredicateExecutor<LeadMaster> {
    @Query(value = "select   t.leadSource.leadSourceName  from LeadMaster t where  t.id=:id ")
    String getLeadSourceNameFromLeadId(Long id);

}
