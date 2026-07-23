package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.model.common.Auditable;
import lombok.Data;

@Data
public class BranchServiceAreaMappingPojo extends Auditable {
    private Long id;

    private Integer branchId;

    private Integer serviceareaId;

}
