package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PopServiceAreaMappingRepo extends JpaRepository<PopServiceAreaMapping,Long> {
        List<PopServiceAreaMapping> findAllByServiceAreaIdIn(List<Integer> serviceAreaId);
        @Query("SELECT p.popId FROM PopServiceAreaMapping p WHERE p.serviceAreaId IN :serviceAreaIds")
        List<Long> findPopIdsByServiceAreaIdIn(@Param("serviceAreaIds") List<Integer> serviceAreaIds);
        List<PopServiceAreaMapping> findAllByPopId(Long popId);
}
