package com.savbill.inventorymanagement.modules.Postpaidplan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface PostpaidPlanRepo extends JpaRepository<PostpaidPlan, Integer>, QuerydslPredicateExecutor<PostpaidPlan> {
    @Query(value = "SELECT pp.name FROM tblmpostpaidplan pp WHERE pp.postpaidplanid = :postpaidplanid", nativeQuery = true)
    String findNameById(@Param("postpaidplanid") Integer postpaidplanid);
}

