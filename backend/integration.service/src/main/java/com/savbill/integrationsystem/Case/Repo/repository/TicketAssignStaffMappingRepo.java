package com.savbill.integrationsystem.Case.Repo.repository;

import com.savbill.integrationsystem.Case.TicketAssignStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAssignStaffMappingRepo extends JpaRepository<TicketAssignStaffMapping, Long>, QuerydslPredicateExecutor<TicketAssignStaffMapping> {

}
