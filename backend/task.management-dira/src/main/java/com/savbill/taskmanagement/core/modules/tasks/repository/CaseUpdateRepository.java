package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CaseUpdateRepository extends JpaRepository<CaseUpdate, Long> {

    @Query("SELECT cu FROM CaseUpdate cu WHERE cu.ticket.caseId = :caseId AND cu.ticket.customers.id = :customerId")
    List<CaseUpdate> findAllByCaseAndCustomerId(@Param("caseId") Long caseId, @Param("customerId") Integer customerId);

    @Query("SELECT cu FROM CaseUpdate cu WHERE cu.ticket.caseId = :caseId")
    List<CaseUpdate> findAllByCase(Integer caseId);
}
