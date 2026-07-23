package com.savbill.integrationsystem.CustomerPackage.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerPackageRepository extends JpaRepository<CustomerPackage ,Long> {



}
