package com.savbill.integrationsystem.NewNMSIntegration.repository;

import com.savbill.integrationsystem.NewNMSIntegration.entity.IntegrationParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationParametersRepository extends JpaRepository<IntegrationParameters, Long> {

    @Query(value = "SELECT * FROM tbltnmsintegrationparameters p WHERE p.integration_id = :id", nativeQuery = true)
    List<IntegrationParameters> findByIntegrationId(@Param("id") Long id);

}