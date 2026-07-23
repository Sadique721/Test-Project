package com.savbill.taskmanagement.core.modules.ServiceArea.model;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ServiceAreaDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private String latitude;
    private String longitude;
    
    private Long areaid;
    private Integer mvnoId;

    private List<Integer> pincodes;

    private Long cityid;

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    @Override
    public Long getBuId() {
        return null;
    }

}
