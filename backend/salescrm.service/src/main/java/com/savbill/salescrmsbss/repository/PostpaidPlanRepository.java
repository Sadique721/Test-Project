package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.PostpaidPlan;

@Repository
public interface PostpaidPlanRepository extends JpaRepository<PostpaidPlan, Integer>{

    PostpaidPlan findByApiGatewayPlanId(Long id);
}
