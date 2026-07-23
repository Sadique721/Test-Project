package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.PlanServiceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlangroupRepocitory extends JpaRepository<PlanServiceData, Integer> {
}
