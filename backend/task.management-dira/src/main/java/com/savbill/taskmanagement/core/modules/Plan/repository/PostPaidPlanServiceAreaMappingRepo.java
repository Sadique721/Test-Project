package com.savbill.taskmanagement.core.modules.Plan.repository;

import com.savbill.taskmanagement.core.modules.Plan.domain.PostPaidPlanServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostPaidPlanServiceAreaMappingRepo extends JpaRepository<PostPaidPlanServiceAreaMapping, Integer>, QuerydslPredicateExecutor<PostPaidPlanServiceAreaMapping>
{
	List<PostPaidPlanServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByServiceId(Integer serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByPlanId(Integer planid);
}
