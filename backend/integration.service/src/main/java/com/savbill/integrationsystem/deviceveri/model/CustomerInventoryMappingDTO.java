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
public class CustomerInventoryMappingDTO extends Auditable<Long> implements IBaseDto{
	private Long mappingId;
	private Long quantity;
	private Long customerId;
	private Long productId;
	private Long staffId;
	private LocalDateTime assignedDateTime;
	private Long mvnoId;
	private LocalDateTime createdate;
	private LocalDateTime lastmodifieddate;
	private String createbyname;
	private String updatebyname;
	private Long createdbystaffid;
	private Long lastmodifiedbystaffid;
	private Integer isDeleted;
	private String status;
	private LocalDateTime expiryDateTime;
	private Long nextApprover;
	private Long teamHierarchyMappingId;
	private Long previousApproveId;
	private Long inwardId;
	private Long externalItemId;
	private Long serviceId;
	private Long custpackId;
	private Long itemId;
	private Long itemassemblyid;
	private Long isInvoiceCreated;
	private String connectionNo;
	private String replacementReason;
	private Long planId;
	private Long mappingRefId;
	private String remark;
	private Long plangroupId;
	private Long offerPrice;
	private Long chargeId;
	private String billTo;
	private Long isInvoiceToOrg;
	private Long newAmount;
	private Long discount;
	private Long isRequiredApproval;
	private Long isFree;
	private Long paymentOwnerId;
	private Long billableCustId;
	private String ezybillStockId;
	private String pairstatus;
    
    @Override
    public Long getIdentityKey() {
        return mappingId;
    }

    @Override
    public Long getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
    	this.mvnoId = mvnoId;
    }
}
