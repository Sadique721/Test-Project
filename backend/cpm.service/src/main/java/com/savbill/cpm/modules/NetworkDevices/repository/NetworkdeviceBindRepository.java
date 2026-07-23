package com.savbill.cpm.modules.NetworkDevices.repository;

import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDeviceBindings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface NetworkdeviceBindRepository extends JpaRepository<NetworkDeviceBind, Long>, QuerydslPredicateExecutor<NetworkDeviceBindings> {

//    List<NetworkDeviceBind> findByDeviceId(Long id);

    List<NetworkDeviceBind> findByCurrentDeviceId(Long id);

    NetworkDeviceBind findTopByOrderByIdDesc();

}
