package com.savbill.inventorymanagement.modules.Services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    @Query(value = "SELECT s.servicename FROM tblmservices s WHERE s.serviceid = :serviceid", nativeQuery = true)
    String findServiceNameById(@Param("serviceid") Long serviceid);
}
