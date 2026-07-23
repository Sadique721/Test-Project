package com.savbill.integrationsystem.ReverseBusinessPromotion.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReverseBusinessPromotionRawDataRepository extends JpaRepository<ReverseBusinessPromotionRawData, Long>, QuerydslPredicateExecutor<ReverseBusinessPromotionRawData> {
}
