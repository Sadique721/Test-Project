package com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.domain.ProductWarehouseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductWarehouseMappingRepo extends JpaRepository<ProductWarehouseMapping, Long>, QuerydslPredicateExecutor<ProductWarehouseMapping> {

    ProductWarehouseMapping findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<ProductWarehouseMapping> findByWarehouseId(Long warehouseId);
}
