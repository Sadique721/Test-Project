package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IPPoolDtlsDTO extends Auditable implements IBaseDto {

    private Long poolDetailsId;
    private Long poolId;
    private String ipAddress;
    private String status;
    private Boolean isDelete = false;
    private Long allocatedId;
    private LocalDateTime unblockTime;
    private Long blockByCustId;
    private String remarks;
    private Integer mvnoId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return poolDetailsId;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
