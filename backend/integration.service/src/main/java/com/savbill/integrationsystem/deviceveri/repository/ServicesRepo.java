package com.savbill.integrationsystem.deviceveri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.savbill.integrationsystem.deviceveri.domain.ServicesData;

import java.util.List;

@Repository
public interface ServicesRepo extends JpaRepository<ServicesData, Long> {
    List<ServicesData> findByServiceid(Long serviceId);
}
