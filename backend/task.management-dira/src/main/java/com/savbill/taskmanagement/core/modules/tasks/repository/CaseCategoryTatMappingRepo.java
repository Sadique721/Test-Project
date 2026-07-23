package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategoryTatMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository
public interface CaseCategoryTatMappingRepo extends JpaRepository<CaseCategoryTatMapping,Long>, QuerydslPredicateExecutor<CaseCategoryTatMapping> {
    //CaseCategoryTatMapping findByTicketReasonSubCategoryIdAndOrderid(Long ticketReasonCategoryId, Long orderNumber);

    //List<CaseCategoryTatMapping> findByTicketReasonSubCategoryId(Long ticketReasonCategoryId);
}
