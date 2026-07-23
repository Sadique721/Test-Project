package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.OLTPortDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OltPortRepository extends JpaRepository<OLTPortDetails,Long> {
}
