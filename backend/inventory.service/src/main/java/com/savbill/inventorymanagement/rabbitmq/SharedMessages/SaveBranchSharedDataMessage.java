package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.MasterManagement.BranchServiceMapping.BranchServiceMappingEntity;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveBranchSharedDataMessage {


    private Long id;

    private String name;

    private String status;


    private String branch_code;


    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();


    private Boolean isDeleted = false;


    private Integer mvnoId;

    private Boolean revenue_sharing;

    private Double sharing_percentage;


    private String dunningDays;


    List<BranchServiceMappingEntity > branchServiceMappingEntityList;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
