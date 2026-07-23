package com.savbill.cpm.modules.Broadcast.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class BroadcastPortsDTO implements IBaseDto {
    private Long id;
    private Integer portid;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private BroadcastDTO broadcastDTO;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
