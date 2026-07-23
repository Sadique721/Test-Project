package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.TicketAssignStaffMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface TicketAssignStaffMappingRepo extends JpaRepository<TicketAssignStaffMapping, Long>, QuerydslPredicateExecutor<TicketAssignStaffMapping> {

    List<TicketAssignStaffMapping> findAllByTicketIdIn(List<Long> caseId);
}
