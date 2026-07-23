package com.savbill.integrationsystem.rms.repository;

import com.savbill.integrationsystem.rms.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepo extends JpaRepository<Vendor,Long> {
}
