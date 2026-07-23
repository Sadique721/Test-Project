package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.ServiceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAreaInRepo extends JpaRepository<ServiceArea, Long> {
}
