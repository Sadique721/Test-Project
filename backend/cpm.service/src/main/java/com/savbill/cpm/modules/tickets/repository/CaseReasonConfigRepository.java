package com.savbill.cpm.modules.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.modules.tickets.domain.CaseReasonConfig;

@Repository
public interface CaseReasonConfigRepository extends JpaRepository<CaseReasonConfig, Long> {

//	List<CaseReasonConfig> findAllByCaseReason_ReasonIdAndIsDeleted(Long caseReason_Id,boolean isDeleted);
//
//	List<CaseReasonConfig> findAllByServiceArea_IdAndCaseReason_ReasonId(Long serviceAreaId, Long caseReasonId);
	
}
