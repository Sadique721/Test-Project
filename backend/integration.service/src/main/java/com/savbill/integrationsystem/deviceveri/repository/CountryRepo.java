package com.savbill.integrationsystem.deviceveri.repository;

import com.savbill.integrationsystem.deviceveri.domain.CountryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepo extends JpaRepository<CountryData, Long> {
}

