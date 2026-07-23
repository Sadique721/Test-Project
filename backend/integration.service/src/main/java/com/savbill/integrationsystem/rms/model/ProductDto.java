package com.savbill.integrationsystem.rms.model;


import com.savbill.integrationsystem.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ProductDto implements IBaseDto {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Long mvnoId;
    private Boolean isDeleted = false;
    private Integer expiryTime;
    private String expiryTimeUnit;
    private Integer refurburshiedProductCharge;
    private ProductCategoryDto productCategory;
    private Integer availableInPorts;
    private Integer totalInPorts;
    private Integer availableOutPorts;
    private Integer totalOutPorts;
    private String productId;
    private String navLedgerId;
    private Integer newProductCharge;
    private Double refurburshiedProductRefAmountInWarranty;
    private Double refurburshiedProductRefAmountPostWarranty;
    private Double newProductRefAmountInWarranty;
    private Double newProductRefAmountPostWarranty;
    private Double newProductAmount;
    private Double refurburshiedProductAmount;
    private Long caseId;
    private Long vendorId;
    private Long newPrice;
    private Long refurburshiedPrice;
    private Long refurburshiedProductTax;
    private Long newProductTax;
    private String refurburshiedProductTaxName;
    private String newProductTaxName;
    private Long actualpricenewProduct;
    private Long actualpricerefurbishedProduct;



    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Long getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Long mvnoId) {
        this.mvnoId = mvnoId;
    }
}
