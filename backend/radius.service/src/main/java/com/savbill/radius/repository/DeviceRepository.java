package com.savbill.radius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>,QuerydslPredicateExecutor<Device>
{
	Optional<Device> findByDeviceProfileName(String name);
	Optional<Device> findByDeviceProfileNameAndMvnoId(String name, Integer mvnoId);

}
