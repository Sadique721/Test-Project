package com.savbill.partnermanagement.modules.MasterManagement.BranchServiceMapping;

import lombok.Data;

@Data
public class BranchServiceMappingPojo {
    private Integer id;

    private Long branchId;
    private Integer serviceId;
    private Double revenueShareper;

    private Boolean isDeleted = false;

}
