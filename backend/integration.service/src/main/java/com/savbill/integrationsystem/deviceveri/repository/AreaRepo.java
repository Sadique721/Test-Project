package com.savbill.integrationsystem.deviceveri.repository;

import com.savbill.integrationsystem.deviceveri.domain.AreaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepo extends JpaRepository<AreaData, Long> {
}
