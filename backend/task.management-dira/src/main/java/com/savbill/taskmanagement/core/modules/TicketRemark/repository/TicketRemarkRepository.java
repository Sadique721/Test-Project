package com.savbill.taskmanagement.core.modules.TicketRemark.repository;

import com.savbill.taskmanagement.core.modules.TicketRemark.domain.TicketRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRemarkRepository extends JpaRepository<TicketRemark, Long>, QuerydslPredicateExecutor<TicketRemark> {

}
