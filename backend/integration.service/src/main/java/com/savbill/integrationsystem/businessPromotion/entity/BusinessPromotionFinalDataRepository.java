package com.savbill.integrationsystem.businessPromotion.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;

public interface BusinessPromotionFinalDataRepository extends JpaRepository<BusinessPromotionFinalData, Long>, QuerydslPredicateExecutor<BusinessPromotionFinalData> {
    BusinessPromotionFinalData findFirstByAddedDateAndIsPushedTrue(LocalDate addedDate);
}
