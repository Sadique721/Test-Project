package com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryMappingDto implements IBaseDto {
    private Long id;

    Long qty;

    Long productId;

    //    Integer customerId;
    String ownerType;
    Long ownerId;

    Integer staffId;

    Long inwardId;

    LocalDateTime assignedDateTime;

    private Boolean isDeleted = false;

    private Integer mvnoId;

    String approvalStatus;

    LocalDateTime expiryDateTime;

    private String inwardNumber;
    private String productName;
    private String customerName;
    private boolean hasMac;
    private boolean hasSerial;
    private boolean hasTrackable;
    private boolean hasPort;
    private Integer nextApproverId;
    private Integer teamHierarchyMappingId;
    private String assigneeName;
    private List<InOutWardMACMapping> inOutWardMACMapping;
    private Integer previousApproveId;
    private String approvalRemark;
    private String popName;
    private String serviceAreaName;
    private Integer createdById;

    private String deviceType;

    private String deviceName;

    private Integer totalInPort;

    private Integer availableInPort;

    private Integer totalOutPort;

    private Integer availableOutPort;

    private Integer totalPort;

    private Integer availablePort;

    private Integer macMappingId;

    private String replacementReason;

    private String latitude;
    private String longitude;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }
}
