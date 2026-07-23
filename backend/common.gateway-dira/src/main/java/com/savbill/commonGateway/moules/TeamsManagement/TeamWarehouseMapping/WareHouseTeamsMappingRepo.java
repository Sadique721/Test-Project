package com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseTeamsMappingRepo extends JpaRepository<WareHouseTeamsMapping,Long> {

    List<WareHouseTeamsMapping> findAllByTeamIdIn(List<Long> teamIdList);
    List<WareHouseTeamsMapping> findAllByWarehouseId(Long warehouseId);
    Integer countByTeamId(Long teamId);
}
