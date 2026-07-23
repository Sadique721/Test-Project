package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class IPPoolDTO extends Auditable implements IBaseDto {
    private Long poolId;
    private String poolName;
    private String displayName;
    private String poolType;
    private String ipRange;
    private String poolCategory;
    private String netMask;
    private String networkIp;
    private String broadcastIp;
    private String firstHost;
    private String lastHost;
    private Integer totalHost;
    private Boolean isDelete = false;
    private Boolean isStaticIpPool = false;
    private Boolean defaultPoolFlag = false;
    private String status;
    private String remark;
    private Integer mvnoId;

    private Integer displayId;
    private String displayPoolName;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return poolId;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
