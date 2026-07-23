package com.savbill.integrationsystem.Services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicesRepository extends JpaRepository<Services , Integer> {
    @Query(value = "select servicename from tblmservices where serviceid in :serviceIds",nativeQuery = true)
    List<String> findByServiceIds(@Param("serviceIds") List<Long> serviceIds);
}
