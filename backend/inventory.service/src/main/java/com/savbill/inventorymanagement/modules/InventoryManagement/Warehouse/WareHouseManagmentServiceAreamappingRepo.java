package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseManagmentServiceAreamappingRepo extends JpaRepository<WareHouseServiceAreaMapping,Long> {

    List<WareHouseServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);

    @Query("SELECT w.warehouseId FROM WareHouseServiceAreaMapping w WHERE w.serviceId IN :serviceAreaIds")
    List<Long> findWarehouseIdsByServiceIdIn(@Param("serviceAreaIds") List<Integer> serviceAreaIds);

    List<WareHouseServiceAreaMapping> findAllByWarehouseId(Long id);

    @Query(value = "select serviceareaid from tbltwarehousemanagmentservicearearel t where t.warehouse_id=:id", nativeQuery = true)
    List<Integer> findAllByWarehouseId1(@Param("id") Long id);

    @Query(value = "SELECT sa.name FROM tblmservicearea sa " +
            "JOIN tbltwarehousemanagmentservicearearel t " +
            "ON sa.service_area_id = t.serviceareaid WHERE t.warehouse_id = :id " +
            "LIMIT 1", nativeQuery = true)
    String findServiceAreaNamesByWarehouseId(@Param("id") Long id);

    @Query(value = "SELECT map.warehouse_id FROM tbltwarehousemanagmentservicearearel map WHERE map.serviceareaid in (:serviceAreaIds)", nativeQuery = true)
    List<Long> findWarehouseIdsBySAIds(@Param("serviceAreaIds") List<Integer> serviceAreaIds);

}
