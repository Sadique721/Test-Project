package com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface RequestInventoryRepo extends JpaRepository<RequestInventory,Long>, QuerydslPredicateExecutor<RequestInventory> {

    RequestInventory findTopByOrderByIdDesc();

    List<RequestInventory> findAllByCreatedById(Integer id);

    List<RequestInventory> findAllByRequestNameId(Long requestNameId);

    List<RequestInventory> findAllByRequestToWarehouseId(Long requestToWarehouseId);

}
