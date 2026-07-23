package com.savbill.commonGateway.moules.MasterManagement.Branch.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.savbill.commonGateway.moules.MasterManagement.BranchService.model.BranchServiceMappingEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Integer buId;
    
    private List<Long> serviceAreaIdsList;

    private List<String> serviceAreaNameList = new ArrayList<>();

    private Boolean revenue_sharing;

    private Double sharing_percentage;

    private String  dunningDays;

    private Integer displayId;
    private String displayName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    List<BranchServiceMappingEntity> branchServiceMappingEntityList;

    public BranchDTO(Long id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
    
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
