package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;

import com.savbill.commonGateway.moules.TeamsManagement.TeamWarehouseMapping.WareHouseTeamsMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWarehouseTeamMappingSharedMessage {
    List<WareHouseTeamsMapping> wareHouseTeamsMappingList;
    private Integer operation;
    private Long warehouseId;
}
