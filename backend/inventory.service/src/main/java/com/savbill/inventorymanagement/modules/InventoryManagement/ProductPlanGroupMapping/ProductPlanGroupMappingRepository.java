package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductPlanGroupMappingRepository extends JpaRepository<ProductPlanGroupMapping,Long>, QuerydslPredicateExecutor<ProductPlanGroupMapping> {
    List<ProductPlanGroupMapping> findAllByPlanIdAndPlanGroupId(Long planId, Long planGroupId);

    List<ProductPlanGroupMapping> findAllByPlanGroupId(Long planGroupId);

}
