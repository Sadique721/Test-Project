package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.LocalDate;
import java.util.List;

@Data
public class ProductDto implements IBaseDto {

    private Long id;
    String name;
    String description;
    String status;
    Integer mvnoId;
    private Boolean isDeleted = false;
    private Integer expiryTime;
    private String expiryTimeUnit;
    private Integer refurburshiedProductCharge;
    ProductCategory productCategory;
//    private Long productCategory;
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
    private String vendorName;
    private Long newPrice;
    private Long refurburshiedPrice;
    private Long refurburshiedProductTax;
    private Long newProductTax;
    private String refurburshiedProductTaxName;
    private String newProductTaxName;
    private Long actualpricenewProduct;
    private Long actualpricerefurbishedProduct;
    private Boolean hasOEMConsider;
    private Boolean hasAssetConsider;
    private List<SpecificationParametersDTO> specificationParametersDTOList;
    private String filename;
    private String uniquename;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate licenseDate;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

//    @JsonIgnore
    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
