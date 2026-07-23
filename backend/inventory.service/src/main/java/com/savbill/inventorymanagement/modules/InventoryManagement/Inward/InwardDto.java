package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;


import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecification;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<SpecificationParametersDTO> specificationParametersDTOList;
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
    private String description;
    private Long assignNonSerializedItemQty;
    private Long requestInventoryId;
    List<InventorySpecification> inventorySpecificationList;
    private LocalDate startDateTime;
    private LocalDate expiryDateTime;
    private Integer oemWarrantyRemainingDays;
    private String oemWarrantyStatus;

    private List<Item> itemList;
    private Long groupId;
    private Boolean isGroup;

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

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
