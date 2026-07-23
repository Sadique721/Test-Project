package com.savbill.radius.repository;

import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.entity.VLANValidationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VlanValidationMappingRepository extends JpaRepository<VLANValidationMapping, Long>, QuerydslPredicateExecutor<VLANManagement> {
    @Query(value = "SELECT VLANID FROM savbillradius.tblmvlanvalidationmapping WHERE :value REGEXP REGEX",
            nativeQuery = true)
    String findByRegexMatch(@Param("value") String value);

    @Transactional
    void deleteByVlanId(String vlanId);

    @Transactional
    @Modifying
    @Query(value = "CALL sp_insert_vlan_mapping(:vlanid, :circuitType, :nasPortId2, :nasPortId4)", nativeQuery = true)
    void callInsertVlanMapping(
            @Param("vlanid") String vlanId,
            @Param("circuitType") String circuitType,
            @Param("nasPortId2") String nasPortId2,
            @Param("nasPortId4") String nasPortId4
    );

    @Transactional
    @Modifying
    @Query(value = "CALL insert_vlan_mappings_from_management()", nativeQuery = true)
    void callInsertVlanMappingsProcedure();

}
