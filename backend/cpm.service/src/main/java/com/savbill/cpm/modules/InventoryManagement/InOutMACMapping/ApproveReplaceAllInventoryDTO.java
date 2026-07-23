package com.savbill.cpm.modules.InventoryManagement.InOutMACMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveReplaceAllInventoryDTO {

    private Long oldMacMappingId;

    private Long newMacMappingId;
    private Integer nextApprover;
}
