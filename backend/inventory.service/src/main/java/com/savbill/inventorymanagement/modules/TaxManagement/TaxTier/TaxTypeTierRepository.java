package com.savbill.inventorymanagement.modules.TaxManagement.TaxTier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface TaxTypeTierRepository extends JpaRepository<TaxTypeTier, Integer> {
}
