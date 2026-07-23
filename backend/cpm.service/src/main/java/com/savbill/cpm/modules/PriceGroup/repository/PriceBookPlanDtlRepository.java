package com.savbill.cpm.modules.PriceGroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.cpm.modules.PriceGroup.domain.PriceBookPlanDetail;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PriceBookPlanDtlRepository extends JpaRepository<PriceBookPlanDetail,Long>, QuerydslPredicateExecutor<PriceBookPlanDetail> {

}
