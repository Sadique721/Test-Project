package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.PostpaidPlanCharge;

import java.util.List;

@Repository
public interface PostpaidPlanChargeRepository extends JpaRepository<PostpaidPlanCharge, Integer>{

	List<PostpaidPlanCharge> findByPlan_Id(Integer planId);

    PostpaidPlanCharge findByApiGatewayPlanChargeId(Long longValue);

    List<PostpaidPlanCharge> findByPlan_IdAndCharge_Id(Integer planId, Integer chargeId);
}
