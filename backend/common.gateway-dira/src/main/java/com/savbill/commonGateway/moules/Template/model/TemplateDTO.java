package com.savbill.commonGateway.moules.Template.model;

import com.savbill.commonGateway.core.dto.IBaseDto;
import lombok.Data;

@Data
public class TemplateDTO implements IBaseDto {

    private Long id;
    private String name;
    private String type;
    private String status;
    private String file;
    private Integer mvnoId;
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
