package com.savbill.integrationsystem.ReverseBusinessPromotion.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;

public interface ReverseBusinessPromotionFinalDataRepository extends JpaRepository<ReverseBusinessPromotionFinalData, Long> , QuerydslPredicateExecutor<ReverseBusinessPromotionFinalData> {
    ReverseBusinessPromotionFinalData findFirstByAddedDateAndIsPushedTrue(LocalDate addedDate);
}
