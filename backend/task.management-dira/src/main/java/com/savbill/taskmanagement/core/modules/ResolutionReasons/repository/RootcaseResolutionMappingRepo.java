package com.savbill.taskmanagement.core.modules.ResolutionReasons.repository;

import com.savbill.taskmanagement.core.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RootcaseResolutionMappingRepo  extends JpaRepository<RootCauseResolutionMapping,Long>, QuerydslPredicateExecutor<RootCauseResolutionMapping> {
}
