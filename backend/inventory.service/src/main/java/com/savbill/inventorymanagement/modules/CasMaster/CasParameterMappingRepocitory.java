package com.savbill.inventorymanagement.modules.CasMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CasParameterMappingRepocitory extends JpaRepository<CasParameterMapping,Long>, QuerydslPredicateExecutor<CasParameterMapping> {
}
