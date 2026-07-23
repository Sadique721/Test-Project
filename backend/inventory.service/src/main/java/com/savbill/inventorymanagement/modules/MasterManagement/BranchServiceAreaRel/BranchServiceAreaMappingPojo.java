package com.savbill.inventorymanagement.modules.MasterManagement.BranchServiceAreaRel;

import com.savbill.inventorymanagement.core.data.Auditable;
import lombok.Data;

@Data
public class BranchServiceAreaMappingPojo extends Auditable {
    private Long id;

    private Integer branchId;

    private Integer serviceareaId;

}
