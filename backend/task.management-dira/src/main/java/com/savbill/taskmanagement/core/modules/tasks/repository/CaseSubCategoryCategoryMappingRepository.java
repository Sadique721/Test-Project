package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategoryCategoryMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CaseSubCategoryCategoryMappingRepository extends JpaRepository<CaseSubCategoryCategoryMapping,Long> , QuerydslPredicateExecutor<CaseSubCategoryCategoryMapping> {

    List<CaseSubCategoryCategoryMapping> findAllByCaseCategoryId(Long caseSubCategoryId);
}
