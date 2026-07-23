package com.savbill.ticketmanagement.core.modules.tickets.repository;


import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseReasonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseReasonConfigRepository extends JpaRepository<CaseReasonConfig, Long> {

//	List<CaseReasonConfig> findAllByCaseReason_ReasonIdAndIsDeleted(Long caseReason_Id,boolean isDeleted);
//
//	List<CaseReasonConfig> findAllByServiceArea_IdAndCaseReason_ReasonId(Long serviceAreaId, Long caseReasonId);
	
}
