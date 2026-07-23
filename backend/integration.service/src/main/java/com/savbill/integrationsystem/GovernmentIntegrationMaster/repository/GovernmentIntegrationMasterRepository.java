package com.savbill.integrationsystem.GovernmentIntegrationMaster.repository;

import com.savbill.integrationsystem.GovernmentIntegrationMaster.entity.GovernmentIntegrationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GovernmentIntegrationMasterRepository extends JpaRepository<GovernmentIntegrationMaster, Long>, QuerydslPredicateExecutor<GovernmentIntegrationMaster> {

    @Query(value = "select count(*) from tblmgovernmentintegrationmaster t where t.id =:id and t.isdelete = false", nativeQuery = true)
    Integer deleteVerifyForGovIntegrateMaster(@Param("id") Long id);

    GovernmentIntegrationMaster getGovernmentIntegrationMasterByIdAndMvnoIdAndIsdeleteFalse(Long id, Long mvnoId);

    GovernmentIntegrationMaster getGovernmentIntegrationMasterByMvnoIdAndIsdeleteFalse(Long mvnoId);
}
