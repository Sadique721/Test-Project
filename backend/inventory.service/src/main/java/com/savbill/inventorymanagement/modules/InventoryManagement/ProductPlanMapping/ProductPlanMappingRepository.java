package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPlanMappingRepository extends JpaRepository<Productplanmapping,Long>, QuerydslPredicateExecutor<Productplanmapping> {

    @Query(value = "select * from tbltproductplanmapping where plan_id =:id",nativeQuery = true)
    List<Productplanmapping> getallfromplanid(@Param("id") Long id);

    @Query(value = "select * from tbltproductplanmapping where id =:id",nativeQuery = true)
    List<Productplanmapping> findAllById(@Param("id") Long id);

    Productplanmapping findTopByOrderByIdDesc();

    List<Productplanmapping> findAllByPlanId(Long id);

    @Query(value = "SELECT ppm.product_quantity FROM tbltproductplanmapping ppm WHERE ppm.id = :id",nativeQuery = true)
    Integer findProductQuantityById(@Param("id") Long id);
}
