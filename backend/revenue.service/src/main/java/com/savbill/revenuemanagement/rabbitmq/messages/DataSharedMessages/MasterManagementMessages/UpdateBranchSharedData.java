package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;

import com.savbill.revenuemanagement.mastermanagement.Branch.domain.BranchServiceMappingEntity;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateBranchSharedData {

    private Long id;

    private String name;

    private String status;


    private String branch_code;


    private Set<ServiceArea> serviceAreaNameList = new HashSet<>();


    private Boolean isDeleted;


    private Integer mvnoId;

    private Boolean revenue_sharing;

    private Double sharing_percentage;


    private String dunningDays;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

    List<BranchServiceMappingEntity> branchServiceMappingEntityList;
}
