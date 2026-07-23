package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBindings;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface NetworkDeviceBindingsRepository extends JpaRepository<NetworkDeviceBindings, Long>, QuerydslPredicateExecutor<NetworkDeviceBindings> {
    List<NetworkDeviceBindings> findByDeviceId(Long id);
    List<NetworkDeviceBindings> findByParentDeviceId(Long id);
    void deleteByDeviceIdAndParentDeviceIdIn(Long id, Set<Long> parentId);
    NetworkDeviceBindings findByDeviceIdAndParentDeviceId(Long deviceId, Long parentDeviceId);
    NetworkDeviceBindings findByDeviceIdAndInBind(Long deviceId, String inPortName);
    NetworkDeviceBindings findByParentDeviceIdAndOutBind(Long parentDeviceId, String outPortName);
}
