package com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory;

import java.util.List;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PlanGroupMappingRepository extends JpaRepository<PlanGroupMapping, Integer> {

    @Query(value = "select * from tblmplangroupmapping t where t.plangroupid=:planGroupId and t.is_deleted=false and t.MVNOID=:mvnoId", nativeQuery = true)
    List<PlanGroupMapping> findPlanGroupMappingByPlanGroupId(@Param("planGroupId")Integer planGroupId, @Param("mvnoId")Integer mvnoId);

    @Query(value = "select plangroupmappingid from tblmplangroupmapping t where t.plangroupid=:planGroupId and t.is_deleted=false and t.POSTPAIDPLANID=:planId", nativeQuery = true)
    Integer findPlanGroupMappingByPlanGroupIdAndPlanId(@Param("planGroupId")Integer planGroupId, @Param("planId")Integer planId);

    List<PlanGroupMapping> findByPlanGroupMappingIdIn(List<Integer> planGroupMappingsIds);
}
