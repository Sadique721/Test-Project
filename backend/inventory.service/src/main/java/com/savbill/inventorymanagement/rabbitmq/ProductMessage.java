package com.savbill.inventorymanagement.rabbitmq;

import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
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
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.mvnoId = product.getMvnoId();
        this.totalInPorts = product.getTotalInPorts();
        this.availableInPorts = product.getAvailableInPorts();
        this.totalOutPorts = product.getTotalOutPorts();
        this.availableOutPorts = product.getAvailableOutPorts();
        this.productId = product.getProductId();
        this.navLedgerId = product.getNavLedgerId();
        this.isDeleted = product.getIsDeleted();
        if (product.getRefurburshiedProductCharge() != null) {
            this.refurburshiedProductCharge = product.getRefurburshiedProductCharge().getId() != null ? product.getRefurburshiedProductCharge().getId() : null;
        }
        this.productCategory = product.getProductCategory();
        this.expiryTime = product.getExpiryTime();
        this.expiryTimeUnit = product.getExpiryTimeUnit();
        if (product.getNewProductCharge() != null) {
            this.newProductCharge = product.getNewProductCharge().getId() != null ? product.getNewProductCharge().getId() : null;
        }
        this.refurburshiedProductRefAmountInWarranty = product.getRefurburshiedProductRefAmountInWarranty();
        this.refurburshiedProductRefAmountPostWarranty = product.getRefurburshiedProductRefAmountPostWarranty();
        this.newProductRefAmountInWarranty = product.getNewProductRefAmountInWarranty();
        this.newProductRefAmountPostWarranty = product.getNewProductRefAmountPostWarranty();
        this.caseId = product.getCaseId();
        this.vendorId = product.getVendor().getId();
        this.actualpricenewProduct = product.getActualpricenewProduct();
        this.actualpricerefurbishedProduct = product.getActualpricerefurbishedProduct();
    }
}
