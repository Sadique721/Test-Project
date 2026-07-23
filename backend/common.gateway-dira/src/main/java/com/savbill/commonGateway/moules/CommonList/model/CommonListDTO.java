package com.savbill.commonGateway.moules.CommonList.model;


import com.savbill.commonGateway.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class CommonListDTO implements IBaseDto {
    private Long id;
    private String text;
    private String value;
    private String type;
    private String status;
    private List<CommonListDTO> subTypeList;
    private Integer displayId;
    private String displayName;
    private boolean hasMandatory;

    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
