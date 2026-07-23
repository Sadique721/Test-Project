package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.NetworkDevices;

@Repository
public interface NetworkDevicesRepository extends JpaRepository<NetworkDevices, Long>{

}
