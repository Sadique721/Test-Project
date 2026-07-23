package com.savbill.integrationsystem.rms.model;

import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InOutWardMACMapingDTO implements IBaseDto {


    private Long id;
    private Long inwardId;
    private Long outwardId;
    String status;
    String macAddress;
    private Boolean isDeleted = false;
    private Long custInventoryMappingId;
    String serialNumber;
    private Long mvnoId;
    private Integer currentApproverId;
    private Integer previousApproverId;
    private Integer teamHierarchyMappingId;
    private Long inwardIdOfOutward;
    private Integer isForwarded = 0;
    private String remark;
    private Long externalItemId;
    private Long itemId;
    private Long inventoryMappingId;
    private Long bulkConsumptionId;
    private String itemRemaingDays;
    private Integer isReturned = 0;
    private Long nonSerializedItemId;
    private String condition;
    private String productName;
    private boolean hasMac;
    private boolean hasSerial;
    private String ownerShip;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Long getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }
}

