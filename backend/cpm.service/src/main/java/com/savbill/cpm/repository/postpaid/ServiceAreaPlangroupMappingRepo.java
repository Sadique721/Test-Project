package com.savbill.cpm.repository.postpaid;

import com.savbill.cpm.model.postpaid.PlanGroup;
import com.savbill.cpm.model.postpaid.ServiceAreaPlanGroupMapping;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface ServiceAreaPlangroupMappingRepo extends JpaRepository<ServiceAreaPlanGroupMapping, Integer>, QuerydslPredicateExecutor<ServiceAreaPlanGroupMapping> {

    List<ServiceAreaPlanGroupMapping> findByServiceArea(ServiceArea serviceAreaId);

    List<ServiceAreaPlanGroupMapping> findAllByPlanGroup(PlanGroup planGroup);

    List<ServiceAreaPlanGroupMapping> findByPlanGroupAndServiceAreaIn(PlanGroup planGroup,List<ServiceArea> serviceAreaList );
    List<ServiceAreaPlanGroupMapping> findAllByServiceArea_IdIn(List<Long> serviceArea_id);

}
