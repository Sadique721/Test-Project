package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository;


import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaPincodeRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceAreaPincodeRelRepository  extends JpaRepository<ServiceAreaPincodeRel, Long>, QuerydslPredicateExecutor<ServiceAreaPincodeRel> {

    List<ServiceAreaPincodeRel> findByPincodeData(Pincode pincode);

    @Query("Select t.pincodeData.id from ServiceAreaPincodeRel t where t.serviceArea.id =:serviceAreaId")
    List<Long> getPincodeIdsFromServiceAreaId(@Param("serviceAreaId") Long serviceAreaId);

    @Query(value = "select t.serviceareaid from tbltserviceareapincoderel t  join tblmservicearea t2 on t.serviceareaid =t2.service_area_id where t.pincodeid =:pincodeid  and t2.MVNOID =:MVNOID limit 1",nativeQuery = true)
    Integer getServiceAreaIdFromPincodeId(@Param("pincodeid") Long pincodeid, @Param("MVNOID") Integer MVNOID);

    @Query("SELECT r.serviceArea.id, r.pincodeData.id " +
            "FROM ServiceAreaPincodeRel r " +
            "WHERE r.serviceArea.id IN :serviceAreaIds")
    List<Object[]> findPincodeIdsByServiceAreaIds(@Param("serviceAreaIds") List<Long> serviceAreaIds);

}
