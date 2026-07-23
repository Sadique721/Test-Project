package com.savbill.revenuemanagement.productmanagement.Product_Plan_Mapping.dto;


import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Productplanmappingdto implements IBaseDto {

    private Long id;
    private Long planId;
    private Long productCategoryId;
    private String product_type;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;
    private Long productId;
    private String revisedCharge;
    private String ownershipType;
    private  String name;
    private String productCategoryName;
    private String productName;
    private String planName;
    private Integer productQuantity;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}
