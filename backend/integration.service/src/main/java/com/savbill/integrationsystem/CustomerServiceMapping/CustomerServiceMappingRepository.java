package com.savbill.integrationsystem.CustomerServiceMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerServiceMappingRepository extends JpaRepository<CustomerServiceMapping , Integer> {
}
