package com.savbill.taskmanagement.core.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.TicketServicemapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface TicketServiceMappingRepo extends JpaRepository<TicketServicemapping,Long>, QuerydslPredicateExecutor<TicketServicemapping> {


    List<TicketServicemapping> findAllByTicketid(Long caseId);
}
