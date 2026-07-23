package com.diameter.dto;

import com.diameter.service.IBaseDto;
import lombok.Data;

@Data
public class Productplanmappingdto implements IBaseDto {

    private Long id;
    private Long planId;
    private Long productCategoryId;
    private String product_type;
    private Long productId;
    private String revisedCharge;
    private String ownershipType;
    private String name;
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

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
