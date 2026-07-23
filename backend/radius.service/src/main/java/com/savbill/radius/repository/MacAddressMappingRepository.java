package com.savbill.radius.repository;

import com.savbill.radius.entity.MacAddressMapping;
import com.savbill.radius.entity.MacAddressMappingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MacAddressMappingRepository extends JpaRepository<MacAddressMapping, Long>, QuerydslPredicateExecutor<MacAddressMapping> {
    List<MacAddressMapping> findMacAddressMappingByCustomerId(Long customerId);

    void save(List<MacAddressMappingDto> macAddressMappingDto);

    List<MacAddressMapping> findByCustomerId(Long customerId);


    List<MacAddressMapping> findByMacAddressIdIn(List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM tbltmacaddressmapping WHERE macaddressid in :macAddressId", nativeQuery = true)
    void deleteBymacAddressId(@Param("macAddressId") List macAddressId);


    MacAddressMapping findByCustomerIdAndCustsermappingid(Long custId, Integer custsermappingid);

    @Query("SELECT m FROM MacAddressMapping m " +
            "LEFT JOIN LiveUser l ON CONCAT(m.customerId, '') = l.custid " +
            "AND m.macAddress = l.callingStationId " +
            "WHERE l.custid IS NULL")
    List<MacAddressMapping> findMappingsNotInLiveUser();

    @Query("SELECT m FROM MacAddressMapping m " +
            "LEFT JOIN LiveUser l ON CONCAT(m.customerId, '') = l.custid " +
            "AND m.macAddress = l.callingStationId " +
            "WHERE l.custid IS NULL " +
            "AND m.macRetentionDate < CURRENT_TIMESTAMP")
    List<MacAddressMapping> findMappingsNotInLiveUserWithPastRetentionDate();

    @Query("SELECT c.macRetentionDate FROM MacAddressMapping c " +
            "WHERE c.customerId = :custid " +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, c.macRetentionDate, CURRENT_TIMESTAMP)) ASC")
    List<Timestamp> findNearestMacRetentionDateByCustomerId(@Param("custid") Long custid);

    List<MacAddressMapping> findByMacAddress(String macAddress);

    @Query("SELECT m.macAddress FROM MacAddressMapping m where m.customerId =:custId")
    List<String> findMacByCustomerId(Long custId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MacAddressMapping m WHERE m.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);

    @Modifying
    @javax.transaction.Transactional
    @Query(value = "DELETE FROM tbltmacaddressmapping WHERE custid IN :custIds AND macaddress IN :macs", nativeQuery = true)
    void deleteByCustomerIdAndMacIn(@Param("custIds") List<Long> custIds, @Param("macs") List<String> macs);




}
