package com.savbill.partnermanagement.modules.partner.repository;




import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroupMappingChargeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanGroupMappingChargeRelRepo extends JpaRepository<PlanGroupMappingChargeRel,Long> {

    @Query(value = "select price from tbltplangroupmappingchargerel  where planid = :planId AND chargeid=:chargeId AND plan_group_mappingid=:planGroupMappingId",nativeQuery = true)
    List<Double> findByPlanIdAndChargeIdAndPlanGroupMappingId(Integer planId, Integer chargeId, Integer planGroupMappingId);

    List<PlanGroupMappingChargeRel> findByIdIn(List<Long> ids);
}

