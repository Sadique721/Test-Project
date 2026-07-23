package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.ChargeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepocitory extends JpaRepository<ChargeData, Integer> {
}
