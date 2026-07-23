package com.savbill.cpm.modules.ippool.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IPAllocationDTO extends Auditable implements IBaseDto {

    private Long id;
    private Long custId;
    private Boolean isDelete = false;
    private LocalDateTime terminatedDate;
    private Boolean isSystemUpdated;
    private String terminationReason;
    private Long poolDetailsId;
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
