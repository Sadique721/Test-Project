package com.savbill.cpm.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.cpm.model.postpaid.DiscountMapping;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface DiscountMappingRepository extends JpaRepository<DiscountMapping, Integer> , QuerydslPredicateExecutor<DiscountMapping> {
    List<DiscountMapping> findByDiscountId(Integer discountId);
}
