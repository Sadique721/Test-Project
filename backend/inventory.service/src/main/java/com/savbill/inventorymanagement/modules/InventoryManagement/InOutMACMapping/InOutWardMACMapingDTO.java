package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
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

    private Integer mvnoId;

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
    private Long productId;
    private boolean hasMac;
    private boolean hasSerial;
    private String ownerShip;
    private Boolean inReplacementProcess;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
