package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import lombok.Data;

@Data
public class InwardApprovalDTO {
    Long id ;
    Long productId;
    private String approvalStatus;
    private String approvalRemark;
    Integer mvnoId;
}
