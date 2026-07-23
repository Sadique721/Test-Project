package com.savbill.cpm.modules.tickets.repository;

import com.savbill.cpm.modules.tickets.domain.TicketSubCategoryTatMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketSubCategoryTatMappingRepo extends JpaRepository<TicketSubCategoryTatMapping,Long>, QuerydslPredicateExecutor<TicketSubCategoryTatMapping> {
    TicketSubCategoryTatMapping findByTicketReasonSubCategoryIdAndOrderid(Long ticketReasonCategoryId, Long orderNumber);
}
