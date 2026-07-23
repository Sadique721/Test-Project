package com.savbill.ticketmanagement.core.modules.tickets.repository;

import com.savbill.ticketmanagement.core.modules.tickets.domain.TicketSubCategoryReasonCategoryMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface TicketSubCategoryReasonCategoryMappingRepository extends JpaRepository<TicketSubCategoryReasonCategoryMapping ,Long> , QuerydslPredicateExecutor<TicketSubCategoryReasonCategoryMapping> {

    List<TicketSubCategoryReasonCategoryMapping> findByTicketReasonCategoryId(Long ticketReasonCategoryId);
    List<TicketSubCategoryReasonCategoryMapping> findAllByTicketReasonCategoryIdInAndTicketReasonSubCategoryIdIsNotNull(@Param("ticketReasonCategoryId") List<Long> ticketReasonCategoryId);
}
