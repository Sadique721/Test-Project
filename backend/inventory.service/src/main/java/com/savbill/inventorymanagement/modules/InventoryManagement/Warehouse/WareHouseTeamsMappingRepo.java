package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseTeamsMappingRepo extends JpaRepository<WareHouseTeamsMapping,Long> {

    List<WareHouseTeamsMapping> findAllByTeamIdIn(List<Long> teamIdList);
    List<WareHouseTeamsMapping> findAllByWarehouseId(Long warehouseId);

    @Query(value = "select t.team_id from tbltwarehousemanagmentteamsmapping t where t.warehouse_id = :id", nativeQuery = true)
    List<Long> findTeamIdsByWarehouseId(@Param("id") Long id);

}
