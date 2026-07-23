package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class OutwardDto implements IBaseDto {


    private Long id;
    String outwardNumber;
    Long qty;
    String status;
    Product productId;
//    WareHouse wareHouseId;
    Long staffId;
    ProductCategory productCategory;
    Customers customerId;
    private Integer mvnoId;

    LocalDateTime outwardDateTime;
    private Boolean isDeleted = false;
    Inward inwardId;
    Long usedQty;
    Long unusedQty;
    private  transient String productName;
    private  transient String wareHouseName;
    private  transient String inwardNumber;
    private transient String unit;
    private String sourceType;
    private Long sourceId;
    private String destinationType;
    private Long destinationId;
    private Long inTransitQty;
    private Long serviceAreaId;
    private Long outTransitQty;
    private Long rejectedQty;
    private String approvalStatus;
    private String categoryType;
    private String rmsOutwardId;
    private String navOutwardId;
    private String type;
    private String createdBy;
    private String approvalRemark;
    private String description;
    private Long outwardsInwardId;
    private Long requestInventoryId;

    private Long requestInventoryProductId;

    private Long selectedItems;

   private boolean isGroup;


   private Long groupId;


    private String fileName;

    @Transient
    private String source;

    @Transient
    private String destination;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }

    @Override
    public String toString() {
        return "Outward   toString Override :" + id;
    }
}
