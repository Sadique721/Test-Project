package com.savbill.radius.repository;

import com.savbill.radius.entity.PostpaidPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostpaidPlanRepository extends JpaRepository<PostpaidPlan, Integer>, QuerydslPredicateExecutor<PostpaidPlan> {

    @Query("select t.maxconcurrentsession from PostpaidPlan t where t.id IN :planIds order by t.maxconcurrentsession desc")
    List<String> getMaxconcurrentSessionByPlanIds(@Param("planIds") Set<Integer> planIds);

    PostpaidPlan findAllByNameContainsIgnoreCase(String name);

    PostpaidPlan findByNameIgnoreCase(String name);
    PostpaidPlan findAllByNameEqualsIgnoreCase(String name);
    PostpaidPlan findAllByNameEqualsIgnoreCaseAndIsDeleteFalse(String name);

    @Query("SELECT p.name, p.planGroup FROM PostpaidPlan p WHERE p.id = :planId")
    Object[] findPostpaidPlanById(@Param("planId") Integer planId);

}
