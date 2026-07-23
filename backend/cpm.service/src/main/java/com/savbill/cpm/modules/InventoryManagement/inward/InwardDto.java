package com.savbill.cpm.modules.InventoryManagement.inward;


import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.modules.InventoryManagement.outward.Outward;
import com.savbill.cpm.modules.InventoryManagement.product.Product;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
public class InwardDto implements IBaseDto {

    Long id ;
    String inwardNumber;
    Product productId;
    Long qty;
    Long usedQty;
    Long unusedQty;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime inwardDateTime;
//    WareHouse wareHouseId;
    String type;
    String status;
    Integer mvnoId;
    private Boolean isDeleted = false;
    private String sourceType;
    private Long sourceId;
    private String destinationType;
    private Long destinationId;
    private Long inTransitQty;
    private Long serviceAreaId;
    private Outward outwardId;
    private Long outTransitQty;
    private Long rejectedQty;
    private String approvalStatus;
    private String categoryType;
    private String rmsInwardId;
    private String navInwardId;
    private Long totalMacSerial;
    private String createdBy;
    private String approvalRemark;
    private Long assignNonSerializedItemQty;
    private Long requestInventoryId;

    @Transient
    private String source;

    @Transient
    private String destination;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
