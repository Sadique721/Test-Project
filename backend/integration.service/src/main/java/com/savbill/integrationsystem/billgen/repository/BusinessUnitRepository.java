package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {
}
