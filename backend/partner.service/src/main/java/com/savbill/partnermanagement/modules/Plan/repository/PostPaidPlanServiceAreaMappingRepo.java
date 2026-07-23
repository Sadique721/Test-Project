package com.savbill.partnermanagement.modules.Plan.repository;


import com.savbill.partnermanagement.modules.Plan.domain.PostPaidPlanServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostPaidPlanServiceAreaMappingRepo extends JpaRepository<PostPaidPlanServiceAreaMapping, Integer>
{
	List<PostPaidPlanServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByServiceId(Integer serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByPlanId(Integer planid);
}
