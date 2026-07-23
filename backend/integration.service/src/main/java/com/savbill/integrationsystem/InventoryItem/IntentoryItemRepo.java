package com.savbill.integrationsystem.InventoryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntentoryItemRepo extends JpaRepository<IntentoryItem,Long> {
}
