package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductParameterMappingRepo extends JpaRepository<ProductParameterDefaultValueMapping, Long>, QuerydslPredicateExecutor<ProductParameterDefaultValueMapping> {
    @Query(value = "select default_value from tbltproductparammapping t where t.product_id =:productId and t.param_id =:paramId", nativeQuery = true)
    String getByProductIdAndParamId(@Param("productId") Long productId, @Param("paramId") Long paramId);

    @Query(value = "select * from tbltproductparammapping t where t.product_id =:productId and t.param_id =:paramId", nativeQuery = true)
    List<ProductParameterDefaultValueMapping> getProductMappingByProductIdAndParamId(@Param("productId") Long productId, @Param("paramId") Long paramId);
}
