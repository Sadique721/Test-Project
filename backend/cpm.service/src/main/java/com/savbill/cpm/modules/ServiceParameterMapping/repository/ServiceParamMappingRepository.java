package com.savbill.cpm.modules.ServiceParameterMapping.repository;

import com.savbill.cpm.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
public interface ServiceParamMappingRepository extends JpaRepository<ServiceParamMapping,Long> , QuerydslPredicateExecutor<ServiceParamMapping> {
    List<ServiceParamMapping> findByServiceid(Long serviceId);

    ServiceParamMapping findByServiceidAndServiceParamId(Long serviceId, Long serviceParamId);
}
