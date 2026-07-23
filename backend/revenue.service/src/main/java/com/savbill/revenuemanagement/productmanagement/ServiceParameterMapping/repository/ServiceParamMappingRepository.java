package com.savbill.revenuemanagement.productmanagement.ServiceParameterMapping.repository;


import com.savbill.revenuemanagement.productmanagement.ServiceParameterMapping.domain.ServiceParamMapping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface ServiceParamMappingRepository extends JpaRepository<ServiceParamMapping,Long> {
    List<ServiceParamMapping> findByServiceid(Long serviceId);

    ServiceParamMapping findByServiceidAndServiceParamId(Long serviceId, Long serviceParamId);
}
