package com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductCategoryDto implements IBaseDto {
    private Long id;
    String name;
    String unit;
    String type;
    String status;
    private List<SpecificationParametersDTO> specificationParametersDTOList = new ArrayList<>();
    private Integer mvnoId;
    private Boolean isDeleted = false;
    private boolean hasMac;
    private boolean hasSerial;
    private String productId;
    private boolean hasTrackable;
    private boolean hasPort;
    private boolean hasCas=false;
    private String dtvCategory;
    private String deviceType;
    private Boolean isUpgradeWithExistingProductItem=false;

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
