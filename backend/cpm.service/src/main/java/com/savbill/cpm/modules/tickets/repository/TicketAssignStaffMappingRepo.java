package com.savbill.cpm.modules.tickets.repository;

import com.savbill.cpm.modules.tickets.domain.TicketAssignStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAssignStaffMappingRepo extends JpaRepository<TicketAssignStaffMapping, Long>, QuerydslPredicateExecutor<TicketAssignStaffMapping> {

}
