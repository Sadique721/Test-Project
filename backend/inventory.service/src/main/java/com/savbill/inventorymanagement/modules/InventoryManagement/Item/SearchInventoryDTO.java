package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchInventoryDTO {
    private Integer page;
    private Integer pageSize;
    private List<GenericSearchModel> filters = new ArrayList<>();
    private Long entityId;
    private Long productId;
    private Long ownerId;
    private String ownerType;
    private String entityType;
}
