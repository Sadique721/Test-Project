package com.savbill.inventorymanagement.modules.PostpaidPlanCharge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface PostpaidPlanChargeRepo extends JpaRepository<PostpaidPlanCharge, Integer>, QuerydslPredicateExecutor<PostpaidPlanCharge> {
    List<PostpaidPlanCharge> findAllByPlanId(Integer plan_id);
}
