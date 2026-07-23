package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBindings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NetworkdeviceBindRepository extends JpaRepository<NetworkDeviceBind, Long>, QuerydslPredicateExecutor<NetworkDeviceBindings> {

    List<NetworkDeviceBind> findByCurrentDeviceId(Long id);
    NetworkDeviceBind findTopByOrderByIdDesc();
    List<NetworkDeviceBind> findByOtherDeviceId(Long id);
    List<NetworkDeviceBind> findByMappingId(Integer id);

    @Query(value = "SELECT * FROM tbltnetworkdevicebind WHERE currentdeviceid = :currentdeviceid AND LOWER(portType) = 'in'", nativeQuery = true)
    List<NetworkDeviceBind> findBYCurrentDeviceIdAndInPortType(@Param("currentdeviceid") Long currentdeviceid);

    @Query(value = "SELECT * FROM tbltnetworkdevicebind WHERE currentdeviceid = :currentdeviceid AND LOWER(portType) = 'out'", nativeQuery = true)
    List<NetworkDeviceBind> findByCurrentDeviceIdAndOutPortType(@Param("currentdeviceid") Long currentdeviceid);

    @Query(value = "SELECT * FROM tbltnetworkdevicebind t " +
            "WHERE t.otherdeviceid IN (:deviceIds) " +
            "OR t.currentdeviceid IN (:deviceIds)",
            nativeQuery = true)
    List<NetworkDeviceBind> findByDeviceIds(@Param("deviceIds") List<Long> deviceIds);

    @Query(value = "SELECT * FROM tbltnetworkdevicebind WHERE otherdeviceid in (:otherdeviceid) AND LOWER(portType) = 'out'", nativeQuery = true)
    List<NetworkDeviceBind> findByOtherDeviceIdsAndOutPortType(@Param("otherdeviceid") List<Long> otherdeviceid);

    @Query(value = "SELECT COUNT(*) FROM tbltnetworkdevicebind " +
            "WHERE currentdeviceid = :currentDeviceId " +
            "AND LOWER(portType) = 'out' " +
            "AND otherdeviceid = :otherDeviceId",
            nativeQuery = true)
    Integer countOutPortBindings(
            @Param("currentDeviceId") Long currentDeviceId,
            @Param("otherDeviceId") Long otherDeviceId
    );

    @Query(value = "SELECT otherDeviceId FROM tbltnetworkdevicebind " +
            "WHERE currentdeviceid = :currentDeviceId " +
            "AND LOWER(portType) = 'out' " +
            "AND LOWER(currentDeviceType) = 'olt'",
            nativeQuery = true)
    List<Long> findDnSplitterByOLTId(@Param("currentDeviceId") Long currentDeviceId);

    @Query(value = "SELECT otherDeviceId FROM tbltnetworkdevicebind " +
            "WHERE currentdeviceid = :currentDeviceId " +
            "AND LOWER(portType) = 'out' " +
            "AND LOWER(currentDeviceType) = 'splitter'",
            nativeQuery = true)
    List<Long> findSNSplitterByDNSpliterId(@Param("currentDeviceId") Long currentDeviceId);
}
