package com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.Respository;


import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticalsMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface BusinessVerticalsMappingRepository extends JpaRepository<BusinessVerticalsMapping, Long>, QuerydslPredicateExecutor<BusinessVerticalsMapping> {
    List<BusinessVerticalsMapping> findByRegionId(Long id);

//    @Query(value = "select * from tbltbusinessverticalsmapping t where t.region_id=:regionId",nativeQuery = true)
//    BusinessVerticalsMapping findByRegionId(@Param("regionId")Long regionId);
}
