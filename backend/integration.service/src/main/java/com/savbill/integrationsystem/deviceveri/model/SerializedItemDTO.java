package com.savbill.integrationsystem.deviceveri.model;

import java.time.LocalDateTime;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class SerializedItemDTO extends Auditable<Long> implements IBaseDto{
    private Long id;
    private String name;
    private String mac;
    private String serialNumber;
    private String itemCondition;
    private Long mvnoId;
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String createbyname;
    private String updatebyname;
    private Long createdStaffId;
    private Long LastModifiedStaffId;
    private Integer isDeleted;
    private Long productId;
    private Long currentInwardId;
    private String currentInwardType;
    private String warranty;
    private Long warrantyPeriod;
    private Long owner_id;
    private String ownerType;
    private String itemStatus;
    private String ownershipType;
    private Long externalItemId;
    private String remainingDays;
    private String intransiantWarrenty;
    private String remarks;
    private Long intransiantOwnership;
    private LocalDateTime expiryDate;
    private LocalDateTime intransiantExpiryDate;
    
	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
