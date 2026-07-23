package com.savbill.integrationsystem.billgen.repository;

import com.savbill.integrationsystem.billgen.entity.TaxData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxRepository extends JpaRepository<TaxData, Integer> {
}
