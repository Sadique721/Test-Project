package com.savbill.taskmanagement.core.modules.BusinessVerticals.Respository;


import com.savbill.taskmanagement.core.modules.BusinessVerticals.domain.BusinessVerticalsMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessVerticalsMappingRepository extends JpaRepository<BusinessVerticalsMapping, Long>, QuerydslPredicateExecutor<BusinessVerticalsMapping> {
    List<BusinessVerticalsMapping> findByRegionId(Long id);

//    @Query(value = "select * from tbltbusinessverticalsmapping t where t.region_id=:regionId",nativeQuery = true)
//    BusinessVerticalsMapping findByRegionId(@Param("regionId")Long regionId);
}
