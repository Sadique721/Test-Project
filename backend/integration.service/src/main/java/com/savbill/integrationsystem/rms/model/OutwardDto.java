package com.savbill.integrationsystem.rms.model;


import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.savbill.integrationsystem.rms.entity.Inward;
import com.savbill.integrationsystem.rms.entity.ProductCategory;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
public class OutwardDto implements IBaseDto {

    private Long id;
    String outwardNumber;
    Long qty;
    String status;
    ProductDto productId;
    WareHouseDto wareHouseId;
    Long staffId;
    ProductCategory productCategory;
    //Customers customerId;
    private Long mvnoId;
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
    private Long outwardsInwardId;
    private Long requestInventoryId;

    private Long requestInventoryProductId;

    @Transient
    private String source;

    @Transient
    private String destination;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Long getMvnoId() {
        return this.mvnoId;
    }

    @Override
    public String toString() {
        return "Outward   toString Override :" + id;
    }
}