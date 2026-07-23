package com.savbill.radius.repository;

import com.savbill.radius.entity.RadiusProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RadiusProfileRepository extends JpaRepository<RadiusProfile, Long>, QuerydslPredicateExecutor<RadiusProfile> {
    List<RadiusProfile> findByNameContaining(String name);

    Optional<RadiusProfile> findByName(String name);

    @Query(value = "select * from tblmradiusprofile rf where rf.name=:name", nativeQuery = true)
    List<Object[]> checkForDuplicateReadiusProfile(@Param("name") String name);

    Optional<RadiusProfile> findByRadiusProfileId(Long id);


//	Integer countByCoaDMProfileCoaDMProfileId(Long coaDMProfileId);

    @Query(value = "select * from tblmradiusprofile c where c.proxyserverid=:proxyserverid and c.status='Active'", nativeQuery = true)
    List<Object[]> checkForProxyServerIp(@Param("proxyserverid") Long proxyserverid);

    Integer countByMappingMasterMappingMasterId(Long dbMappingMastersId);

    List<RadiusProfile> findByRequestTypeOrderByPriorityDesc(String requestType);

    List<RadiusProfile> findAllByRequestTypeAndMvnoIdAndStatusOrderByPriorityDesc(String requestType, Integer mvnoId, String status);

    List<RadiusProfile> findAllByRequestTypeAndMvnoId(String requestType, Integer mvnoId);

}
