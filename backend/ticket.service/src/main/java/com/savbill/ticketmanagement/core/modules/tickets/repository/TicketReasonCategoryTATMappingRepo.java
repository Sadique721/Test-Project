package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketReasonCategoryTATMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
@JaversSpringDataAuditable
@Repository
public interface TicketReasonCategoryTATMappingRepo  extends JpaRepository<TicketReasonCategoryTATMapping, Long>, QuerydslPredicateExecutor<TicketReasonCategoryTATMapping> {

    TicketReasonCategoryTATMapping findByTicketReasonCategoryIdAndOrderNumber(Long ticketReasonCategoryId, Long orderNumber);
}
