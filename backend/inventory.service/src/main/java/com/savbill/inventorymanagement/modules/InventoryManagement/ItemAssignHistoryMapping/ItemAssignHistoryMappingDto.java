package com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemAssignHistoryMappingDto {
    private Long id;
    private Long itemId;
    private String ownerType;
    private Long ownerId;
    private Long specificationHistoryId;
    private String currentAssignee;
    private String currentParamValue;
    private LocalDateTime createdDate;

}
