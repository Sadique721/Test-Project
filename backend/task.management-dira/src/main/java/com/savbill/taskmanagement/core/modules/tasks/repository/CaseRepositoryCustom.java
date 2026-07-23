package com.savbill.taskmanagement.core.modules.tasks.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseRepositoryCustom{
    Page<CaseListDTO> findCaseList(BooleanExpression predicate, Pageable pageable);
}
