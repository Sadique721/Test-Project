package com.savbill.radius.repository;

import com.savbill.radius.entity.VLANManagement;
import com.savbill.radius.helper.BulkVlanResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VlanManagementRepository extends JpaRepository<VLANManagement, Long>, QuerydslPredicateExecutor<VLANManagement> {
    int deleteByVlanIdIn(List<Long> ids);

    List<VLANManagement> findVlanNameByVlanNameIn(Set<String> strings);

    @Query("SELECT MAX(v.vlanId) FROM VLANManagement v")
    Optional<Long> findLastId();

    VLANManagement findByVlanName(String vlanName);

    List<VLANManagement> findAllByMvnoId(Integer vlanName);

    @Query("SELECT new com.savbill.radius.helper.BulkVlanResponseDto(v.vlanName, v.nasType, v.circuitType, v.nasIdentifier, v.nasPortId1, " +
            "v.nasPortId2, v.nasPortId3, v.nasPortId4, v.nasPortId5, v.callingStationId, v.contextName, v.filterId, v.forwardPolicy, " +
            "v.httpRedirectProfileName, v.rateLimitRate, v.rateLimitBurst, v.qosPolicingPolicyName, v.qosMeteringPolicyName, v.pppoeUrl, " +
            "v.pppDnsPrimary, v.pppDnsSecondary, v.pppNbnsPrimary, v.sessionTimeOut, v.idleTimeOut, v.framedIpAddress, v.rbDhcpMaxLeases, " +
            "v.ipAddressPoolName, v.natProfileName, v.rbInterfaceName, v.httpRedirectUrl, v.framedIpv6Prefix, v.delegatedIpv6Prefix, " +
            "v.framedInterfaceId, v.framedIpv6Pool, v.ipv6Option, v.ipv6Dns, v.delegatedMaxPrefix, v.delegatedIpv6Pool, v.subProfile, " +
            "v.priority, v.mvnoId, v.RADIUS_ATTRIBUTE_GROUP_ID) FROM VLANManagement v WHERE v.mvnoId = :mvnoId")
    List<BulkVlanResponseDto> findAllByMvno(Integer mvnoId);

    @Query("SELECT v.vlanName FROM VLANManagement v WHERE v.vlanName IN :vlanNames")
    Set<String> findExistingVlanNames(@Param("vlanNames") Set<String> vlanNames);

    @Query("SELECT DISTINCT v FROM VLANManagement v LEFT JOIN FETCH v.mappingList")
    List<VLANManagement> findAllWithMappings();

}
