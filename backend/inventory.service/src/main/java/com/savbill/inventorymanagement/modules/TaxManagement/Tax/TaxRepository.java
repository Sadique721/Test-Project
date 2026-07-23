package com.savbill.inventorymanagement.modules.TaxManagement.Tax;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@JaversSpringDataAuditable
@Repository
public interface TaxRepository extends JpaRepository<Tax, Integer> {
}
