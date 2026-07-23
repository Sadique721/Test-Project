package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import com.savbill.ticketmanagement.core.modules.BranchService.model.BranchServiceMappingEntity;
import com.savbill.ticketmanagement.core.modules.ServiceArea.domain.ServiceArea;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@JsonIgnore
    private Set<ServiceArea> serviceAreaNameList = new HashSet<>();


    private Boolean isDeleted;


    private Integer mvnoId;

    private Boolean revenue_sharing;

    private Double sharing_percentage;


    private String dunningDays;


    List<BranchServiceMappingEntity> branchServiceMappingEntityList;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
    @JsonIgnore
    private  String isBindWithPlan;
}
