package com.savbill.integrationsystem.integrationMenu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThirdPartyIntegrationMenuRepository extends JpaRepository<ThirdPartyIntegrationMenu,Long>, JpaSpecificationExecutor<ThirdPartyIntegrationMenu> {

    boolean existsByNameAndIsDeleteFalse(String name);
    Page<ThirdPartyIntegrationMenu> findAllByMvnoIdAndIsDeleteFalse(Pageable pageable, Long mvnoId);
    Page<ThirdPartyIntegrationMenu> findAllByIsDeleteFalse(Pageable pageable);
    @Query(nativeQuery = true, value = "SELECT * FROM tblmthirdpartymenu t WHERE t.status = :status and t.is_delete = false and t.mvno_id IS NOT NULL")
    List<ThirdPartyIntegrationMenu> findAllByStatusAndIsDeleteFalse(@Param("status") String status);
    @Query(nativeQuery = true, value = "SELECT * FROM tblmthirdpartymenu t WHERE t.event_name = :eventName AND t.client_name = :clientName AND t.mvno_id IS NULL")
    List<ThirdPartyIntegrationMenu> findAllByEventNameAndClientNameAndMvnoIdIsNull(
            @Param("eventName") String eventName,
            @Param("clientName") String clientName
    );

    @Query(nativeQuery = true, value = "SELECT * FROM tblmthirdpartymenu t WHERE t.event_name = :eventName AND t.client_name = :clientName AND t.mvno_id = :mvnoId AND t.is_delete=0")
    List<ThirdPartyIntegrationMenu> findAllByEventNameAndClientNameAndMvnoId(
            @Param("eventName") String eventName,
            @Param("clientName") String clientName,
            @Param("mvnoId") Long mvnoId
    );

    @Query(nativeQuery = true, value = "SELECT * FROM tblmthirdpartymenu t WHERE t.event_name = :eventName AND t.mvno_id = :mvnoId AND t.is_delete=0 AND t.status = 'Active'")
    List<ThirdPartyIntegrationMenu> findAllByEventNameAndMvnoId(
            @Param("eventName") String eventName,
            @Param("mvnoId") Long mvnoId
    );




}
