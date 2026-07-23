package com.savbill.revenuemanagement.core.entity.inventory;

import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.ProductMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmproduct")
@EntityListeners(AuditableListener.class)
public class Product extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

//    @Column(name = "unit")
//    private String unit;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;


    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "total_in_ports")
    private Integer totalInPorts;
    @Column(name = "available_in_ports")
    private Integer availableInPorts;
    @Column(name = "total_out_ports")
    private Integer totalOutPorts;
    @Column(name = "available_out_ports")
    private Integer availableOutPorts;

    @Column(name = "rms_product_id")
    private String rmsProductId;

    @Column(name = "nav_ledger_id")
    private String navLedgerId;


    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "refurb_prod_charge_id")
    private Integer refurburshiedProductCharge;

//    @OneToOne(targetEntity = ProductCategory.class)
//    @JoinColumn(referencedColumnName = "product_id",name ="pc_id" )
//    private ProductCategory productCategory;

//    @Column(name = "product_id")
//    private Long productCategoryId;

    private Integer expiryTime;

    private String expiryTimeUnit;

    @Column(name = "new_prod_charge_id")
    private Integer newProductCharge;

    @Column(name = "refurb_pra_in_wrty")
    private Double refurburshiedProductRefAmountInWarranty;

    @Column(name = "refurb_pra_post_wrty")
    private Double refurburshiedProductRefAmountPostWarranty;

    @Column(name = "new_pra_in_wrty")
    private Double newProductRefAmountInWarranty;

    @Column(name = "new_pra_post_wrty")
    private Double newProductRefAmountPostWarranty;


    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "vendorid")
    private Long vendorId;

    @Column(name="actualpricenewproduct")
    private Long actualpricenewProduct;
    @Column(name="actualpricerefurbishedproduct")
    private Long actualpricerefurbishedProduct;

    @Transient
    private Double newProductAmount;

    @Transient
    private Double refurburshiedProductAmount;

    @Transient
    private Long newPrice;

    @Transient
    private Long refurburshiedPrice;

    @Transient
    private Long refurburshiedProductTax;

    @Transient
    private Long newProductTax;

    @Transient
    private String refurburshiedProductTaxName;

    @Transient
    private String newProductTaxName;

    @Transient
    private Boolean isUpdate;


    public Product(ProductMessage productMessage) {
        this.id = productMessage.getId();//Long.valueOf(productMessage.getProductId());
        this.name = productMessage.getName();
        this.description = productMessage.getDescription();
        this.status = productMessage.getStatus();
        this.mvnoId = productMessage.getMvnoId();
        this.totalInPorts = productMessage.getTotalInPorts();
        this.availableInPorts = productMessage.getAvailableInPorts();
        this.totalOutPorts = productMessage.getTotalOutPorts();
        this.availableOutPorts = productMessage.getAvailableOutPorts();
        this.rmsProductId = productMessage.getProductId();
        this.navLedgerId = productMessage.getNavLedgerId();
        this.isDeleted = productMessage.getIsDeleted();
        this.refurburshiedProductCharge = productMessage.getRefurburshiedProductCharge();
//        this.productCategory = productMessage.getProductCategory();
        this.expiryTime = productMessage.getExpiryTime();
        this.expiryTimeUnit = productMessage.getExpiryTimeUnit();
        this.newProductCharge = productMessage.getNewProductCharge();
        this.refurburshiedProductRefAmountInWarranty = productMessage.getRefurburshiedProductRefAmountInWarranty();
        this.refurburshiedProductRefAmountPostWarranty = productMessage.getRefurburshiedProductRefAmountPostWarranty();
        this.newProductRefAmountInWarranty = productMessage.getNewProductRefAmountInWarranty();
        this.newProductRefAmountPostWarranty = productMessage.getNewProductRefAmountPostWarranty();
        this.caseId = productMessage.getCaseId();
        this.vendorId = productMessage.getVendorId();
        this.actualpricenewProduct = productMessage.getActualpricenewProduct();
        this.actualpricerefurbishedProduct = getActualpricerefurbishedProduct();
        this.isUpdate = productMessage.getIsUpdate();
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }
}
