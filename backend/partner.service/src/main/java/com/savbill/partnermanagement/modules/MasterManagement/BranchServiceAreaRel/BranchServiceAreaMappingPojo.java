package com.savbill.partnermanagement.modules.MasterManagement.BranchServiceAreaRel;

import com.savbill.partnermanagement.core.data.Auditable;
import lombok.Data;

@Data
public class BranchServiceAreaMappingPojo extends Auditable {
    private Long id;

    private Integer branchId;

    private Integer serviceareaId;

}
