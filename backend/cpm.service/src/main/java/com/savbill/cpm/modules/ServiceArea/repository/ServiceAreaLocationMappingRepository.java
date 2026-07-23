package com.savbill.cpm.modules.ServiceArea.repository;

import com.savbill.cpm.modules.LocationMaster.domain.ServiceAreaLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceAreaLocationMappingRepository extends JpaRepository<ServiceAreaLocationMapping,Long> {

    boolean existsByServiceAreaIdAndLocationId(Long serviceAreaId, Long locationId);

    void deleteByServiceAreaId(Long serviceAreaId);

    List<ServiceAreaLocationMapping> findByserviceAreaId(Long serviceAreaId);

}
