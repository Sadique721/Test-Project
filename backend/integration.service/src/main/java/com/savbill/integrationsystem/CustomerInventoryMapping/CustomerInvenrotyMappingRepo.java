package com.savbill.integrationsystem.CustomerInventoryMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerInvenrotyMappingRepo extends JpaRepository<CustomerInventoryMappingEntity,Long> {
}
