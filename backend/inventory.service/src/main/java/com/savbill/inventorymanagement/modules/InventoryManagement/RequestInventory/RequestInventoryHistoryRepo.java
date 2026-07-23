package com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface RequestInventoryHistoryRepo extends JpaRepository<RequestInventoryHistory,Long>, QuerydslPredicateExecutor<RequestInventoryHistory> {
}
