package com.savbill.radius.repository;

import com.savbill.radius.entity.PlanQosMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanQosMappingRepository extends JpaRepository<PlanQosMappingEntity ,Long> {
    @Query(value = "select * from tbltplanqosmapping where planid =:id",nativeQuery = true)
    List<PlanQosMappingEntity> findAllByPlanId (@Param("id") Long id);

}
