package com.savbill.inventorymanagement.modules.PlanService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//@JaversSpringDataAuditable
@Repository
public interface PlanServiceRepository extends JpaRepository<PlanService, Integer>  , QuerydslPredicateExecutor<PlanService> {
    @Query(value = "SELECT ps.is_dtv FROM tblmservices ps WHERE ps.serviceid = :serviceId", nativeQuery = true)
    Boolean findIsDTVById(@Param("serviceId") Long serviceId);

    @Query(value = "SELECT ps.servicename FROM tblmservices ps WHERE ps.serviceid = :serviceId", nativeQuery = true)
    Optional<String> findServiceNameById(@Param("serviceId") Long serviceId);
}
