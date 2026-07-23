package com.savbill.revenuemanagement.rabbitmq.messages.inventory;

import com.savbill.revenuemanagement.core.entity.inventory.Product;
import com.savbill.revenuemanagement.core.entity.inventory.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMessage {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer mvnoId;
    private Integer totalInPorts;
    private Integer availableInPorts;
    private Integer totalOutPorts;
    private Integer availableOutPorts;
    private String productId;
    private String navLedgerId;
    private Boolean isDeleted = false;
    private Boolean isUpdate = false;
    private Integer refurburshiedProductCharge;
    private ProductCategory productCategory;
    private Integer expiryTime;
    private String expiryTimeUnit;
    private Integer newProductCharge;
    private Double refurburshiedProductRefAmountInWarranty;
    private Double refurburshiedProductRefAmountPostWarranty;
    private Double newProductRefAmountInWarranty;
    private Double newProductRefAmountPostWarranty;
    private Long caseId;
    private Long vendorId;
    private Long actualpricenewProduct;
    private Long actualpricerefurbishedProduct;

    public ProductMessage(Product product) {
        this.id = product.getId();
        this.isUpdate = product.getIsUpdate();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.mvnoId = product.getMvnoId();
        this.totalInPorts = product.getTotalInPorts();
        this.availableInPorts = product.getAvailableInPorts();
        this.totalOutPorts = product.getTotalOutPorts();
        this.availableOutPorts = product.getAvailableOutPorts();
        this.productId = product.getRmsProductId();
        this.navLedgerId = product.getNavLedgerId();
        this.isDeleted = product.getIsDeleted();
        this.refurburshiedProductCharge = product.getRefurburshiedProductCharge();
//        this.productCategory = product.getProductCategory();
        this.expiryTime = product.getExpiryTime();
        this.expiryTimeUnit = product.getExpiryTimeUnit();
        this.newProductCharge = product.getNewProductCharge();
        this.refurburshiedProductRefAmountInWarranty = product.getRefurburshiedProductRefAmountInWarranty();
        this.refurburshiedProductRefAmountPostWarranty = product.getRefurburshiedProductRefAmountPostWarranty();
        this.newProductRefAmountInWarranty = product.getNewProductRefAmountInWarranty();
        this.newProductRefAmountPostWarranty = product.getNewProductRefAmountPostWarranty();
        this.caseId = product.getCaseId();
        this.vendorId = product.getVendorId();
        this.actualpricenewProduct = product.getActualpricenewProduct();
        this.actualpricerefurbishedProduct = product.getActualpricerefurbishedProduct();
    }
}
