package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseTeamsMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWarehouseTeamMappingSharedMessage {
    List<WareHouseTeamsMapping> wareHouseTeamsMappingList;
    private Integer operation;
    private Long warehouseId;
}
