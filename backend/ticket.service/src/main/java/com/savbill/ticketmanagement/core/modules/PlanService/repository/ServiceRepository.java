package com.savbill.ticketmanagement.core.modules.PlanService.repository;


import com.savbill.ticketmanagement.core.modules.PlanService.domain.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {

    List<Services> findServicesByIdIn(List<Long> serviceIds);


    @Query("SELECT service.serviceName FROM Services service where service.id in :serviceIds")
    List<String> getServiceNameByIdIn(@Param("serviceIds") List<Long> serviceIds);
}
