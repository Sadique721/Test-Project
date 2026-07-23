package com.savbill.integrationsystem.rms.model;


import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.savbill.integrationsystem.rms.entity.Product;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime inwardDateTime;
    String type;
    String status;
    Long mvnoId;
    private Boolean isDeleted = false;
    private String sourceType;
    private Long sourceId;
    private String destinationType;
    private Long destinationId;
    private Long inTransitQty;
    private Long serviceAreaId;
    private OutwardDto outwardId;
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
    public Long getMvnoId() {
        return mvnoId;
    }
}