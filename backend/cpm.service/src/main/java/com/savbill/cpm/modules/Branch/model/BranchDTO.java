package com.savbill.cpm.modules.Branch.model;

import java.util.ArrayList;
import java.util.List;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.savbill.cpm.modules.BranchService.model.BranchServiceMappingEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class BranchDTO extends Auditable implements IBaseDto {
	
	private Long id;

    private String name;

    private String status;

    private Boolean isDeleted = false;

    private String branch_code;
    
    private Integer mvnoId;
    
    private List<Long> serviceAreaIdsList;

    private List<String> serviceAreaNameList = new ArrayList<>();

    private Boolean revenue_sharing;

    private Double sharing_percentage;

    private String  dunningDays;

    private Integer displayId;
    private String displayName;
    List<BranchServiceMappingEntity> branchServiceMappingEntityList;

	@JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId(){
        return mvnoId;
    }
}
