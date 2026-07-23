package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseParentServiceAreaMapRepo extends JpaRepository<WareHouseParentServiceAreaMapping, Long>, QuerydslPredicateExecutor<WareHouseParentServiceAreaMapping> {
    List<WareHouseParentServiceAreaMapping> findAllByWarehouseId(Long id);
    @Query(value = "select parentserviceareaid from tbltwarehousemanagmentparentservicearearel t where t.warehouse_id=:id", nativeQuery = true)
    List<Integer> findAllByWarehouseId1(@Param("id") Long id);
}
