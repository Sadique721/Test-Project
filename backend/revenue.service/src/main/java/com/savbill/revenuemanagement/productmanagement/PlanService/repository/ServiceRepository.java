package com.savbill.revenuemanagement.productmanagement.PlanService.repository;


import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {

//    List<Services> findServicesByIdIn(List<Long> serviceIds);
//    Services findServicesByIdIn(Long serviceIds);

Services findServicesByServiceName(String service);


    @Query(value = "select servicename from tblmservices where serviceid = :id", nativeQuery = true)
    String findserviceNameByServiceId(@Param("id")Long id);

    @Query(value = "select serviceid from tblmservices where servicename = :servicename and MVNOID=:mvnoId", nativeQuery = true)
    Integer findServiceNameByServiceIdAndMvnoId(@Param("servicename")String servicename,@Param("mvnoId")Long mvnoId);
}
