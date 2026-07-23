package com.savbill.partnermanagement.rabbitmq.master;


import com.savbill.partnermanagement.modules.MasterManagement.BranchServiceMapping.BranchServiceMappingEntity;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SaveBranchSharedDataMessage {


    private Long id;

    private String name;

    private String status;


    private String branch_code;


    private Set<ServiceArea> serviceAreaNameList = new HashSet<>();


    private Boolean isDeleted = false;


    private Integer mvnoId;

    private Boolean revenue_sharing;

    private Double sharing_percentage;


    private String dunningDays;


    List<BranchServiceMappingEntity> branchServiceMappingEntityList;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;


}
