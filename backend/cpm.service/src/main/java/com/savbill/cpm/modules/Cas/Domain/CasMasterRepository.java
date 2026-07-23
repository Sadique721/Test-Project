package com.savbill.cpm.modules.Cas.Domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CasMasterRepository extends JpaRepository<CasMaster, Long>, QuerydslPredicateExecutor<CasMaster> {

}