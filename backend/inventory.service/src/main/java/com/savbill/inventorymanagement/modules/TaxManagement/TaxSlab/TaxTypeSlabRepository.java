package com.savbill.inventorymanagement.modules.TaxManagement.TaxSlab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface TaxTypeSlabRepository extends JpaRepository<TaxTypeSlab, Integer> {
}
