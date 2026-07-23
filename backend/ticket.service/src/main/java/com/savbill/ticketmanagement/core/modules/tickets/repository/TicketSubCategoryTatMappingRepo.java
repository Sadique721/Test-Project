package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryTatMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface TicketSubCategoryTatMappingRepo extends JpaRepository<TicketSubCategoryTatMapping,Long>, QuerydslPredicateExecutor<TicketSubCategoryTatMapping> {
    TicketSubCategoryTatMapping findByTicketReasonSubCategoryIdAndOrderid(Long ticketReasonCategoryId, Long orderNumber);

    List<TicketSubCategoryTatMapping> findByTicketReasonSubCategoryId(Long ticketReasonCategoryId);
}
