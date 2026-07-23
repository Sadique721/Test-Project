package com.savbill.integrationsystem.billgen.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ChargeDataRepository extends JpaRepository<ChargeData, Integer>, QuerydslPredicateExecutor<ChargeData> {
}
