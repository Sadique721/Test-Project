package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.savbill.salescrmsbss.entity.ServiceParamMapping;

import java.util.List;
public interface ServiceParamMappingRepository extends JpaRepository<ServiceParamMapping,Long> {
    List<ServiceParamMapping> findByServiceid(Long serviceId);

    ServiceParamMapping findByServiceidAndServiceParamId(Long serviceId, Long serviceParamId);
}
