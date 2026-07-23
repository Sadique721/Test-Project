package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUnitRepo extends JpaRepository<BusinessUnit, Long> {
}
