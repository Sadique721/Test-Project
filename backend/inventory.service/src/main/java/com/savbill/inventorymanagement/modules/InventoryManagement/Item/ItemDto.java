package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemDto implements IBaseDto {
    private Long id;
    private String name;
    private String macAddress;
    private String serialNumber;
    private Integer mvnoId;
//    private String status;
    private String condition;
    private Boolean isDeleted = false;

    private Long currentInwardId;
    private String currentInwardType;
    private Long productId;
    private Long ownerId;
    private String ownerType;
    private String warranty;
    private Integer warrantyPeriod;
    private String currentInwardNumber;
    private String ownerName;
    private String productName;

    private String itemStatus;
    private String ownershipType;
    private String remarks;
    private Long externalItemId;

    private String remainingDays;
    private String filename;
    private Long itemConditionId;

    private LocalDateTime expireDate;
    private Long invenSpecId;
    private String assetId;
    private LocalDate oemStartDate;
    private LocalDate oemEndDate;
    private Integer oemWarrantyRemainingDays;
    private String oemWarrantyStatus;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
