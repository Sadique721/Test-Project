package com.savbill.integrationsystem.deviceveri.repository;

import com.savbill.integrationsystem.deviceveri.domain.CityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepo extends JpaRepository<CityData, Long> {
}
