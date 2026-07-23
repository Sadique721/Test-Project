package com.savbill.cpm.core.dto;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditDocRepositoryCustom {
    Page<CreditDocumentProjectionDTO> findAllProjected(BooleanExpression predicate, Pageable pageable);
}
