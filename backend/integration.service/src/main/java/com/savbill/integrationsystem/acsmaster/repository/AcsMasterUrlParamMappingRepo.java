package com.savbill.integrationsystem.acsmaster.repository;

import com.savbill.integrationsystem.acsmaster.entity.AcsMasterUrlParamMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcsMasterUrlParamMappingRepo extends JpaRepository<AcsMasterUrlParamMapping, Long>
{

}
