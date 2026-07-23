package com.savbill.taskmanagement.core.modules.tasks.repository;


import com.savbill.taskmanagement.core.modules.tasks.domain.EtrAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketETRAuditRepository extends JpaRepository<EtrAudit,Integer>, QuerydslPredicateExecutor<EtrAudit> {

    List<EtrAudit> findAllByCaseId(Integer caseId);
}
