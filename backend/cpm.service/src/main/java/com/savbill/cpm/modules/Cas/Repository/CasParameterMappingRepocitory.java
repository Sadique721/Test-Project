package com.savbill.cpm.modules.Cas.Repository;

import com.savbill.cpm.modules.Cas.Domain.CasParameterMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CasParameterMappingRepocitory extends JpaRepository<CasParameterMapping,Long>, QuerydslPredicateExecutor<CasParameterMapping> {
}
