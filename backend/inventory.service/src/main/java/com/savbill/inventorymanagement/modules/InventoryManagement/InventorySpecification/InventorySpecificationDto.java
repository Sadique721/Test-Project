package com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventorySpecificationDto implements IBaseDto  {

    private Long id;
    private Long paramId;
    private String paramValue;
    private Long inwardId;
    private Long invenSpecId;
    private String paramName;
    private Boolean isMandatory;

    private Boolean isMultiValueParam;
    private List<String> paramMultiValues;
    private String paramValues;
    private String defaultValue;
    private SpecificationParametersDTO specificationParametersDTO ;

    @Transient
    private Long custSerMapId;


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

    public InventorySpecificationDto(String paramValue, String paramName, Boolean isMandatory, Long custSerMapId) {
        this.paramValue = paramValue;
        this.paramName = paramName;
        this.isMandatory = isMandatory;
        this.custSerMapId = custSerMapId;
    }
}
