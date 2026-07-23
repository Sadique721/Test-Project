package com.savbill.partnermanagement.modules.PriceGroup.repository;

import com.savbill.partnermanagement.modules.partner.entity.PriceBookSlabDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PriceBookSlabDetailsRepository extends JpaRepository<PriceBookSlabDetails,Long>, QuerydslPredicateExecutor<PriceBookSlabDetails> {
}
