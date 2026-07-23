package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import lombok.Data;

@Data
public class SearchInOutMacMapping {
    private Long productId;
    private Integer ownerId;
    private String ownerType;
    private PaginationRequestDTO paginationRequestDTO;
}
