package com.savbill.commonGateway.moules.MasterManagement.Branch.model;


import com.savbill.commonGateway.core.data.Auditable;
import lombok.Data;

@Data
public class BranchServiceAreaMappingPojo extends Auditable {
    private Long id;

    private Integer branchId;

    private Integer serviceareaId;

}
