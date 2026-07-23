package com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory;

import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.ServiceAreaPlanGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAreaPlangroupMappingRepo extends JpaRepository<ServiceAreaPlanGroupMapping, Integer> {

    List<ServiceAreaPlanGroupMapping> findByIdIn(List<Long> serviceAreaPlanGroupMappings);
}
