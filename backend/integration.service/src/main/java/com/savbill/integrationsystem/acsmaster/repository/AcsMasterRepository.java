package com.savbill.integrationsystem.acsmaster.repository;

import com.savbill.integrationsystem.acsmaster.entity.AcsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AcsMasterRepository extends JpaRepository<AcsMaster, Long>, QuerydslPredicateExecutor<AcsMaster> {
    AcsMaster getAcsMasterByVendorIdAndMvnoId(Long vendorId,Long mvnoId);
    AcsMaster getAcsMasterByIdAndMvnoIdAndIsdeleteFalse(Long id,Long mvnoId);
}
