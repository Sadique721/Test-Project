package com.savbill.commonGateway.moules.MasterManagement.LocationMaster;

import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceAreaLocationMappingRepository extends JpaRepository<ServiceAreaLocationMapping,Long> {
    boolean existsByServiceAreaIdAndLocationId(Long serviceAreaId, Long locationId);
    List<ServiceAreaLocationMapping> findLocationIdsByServiceAreaId(Long serviceAreaId);

}
