package com.savbill.taskmanagement.core.modules.Branch.model;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.savbill.taskmanagement.core.modules.BranchService.model.BranchServiceMappingEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Long getBuId() {
        return null;
    }
}
