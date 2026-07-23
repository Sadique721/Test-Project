package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement.Vendor;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmproduct")
@EntityListeners(AuditableListener.class)
public class Product extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    @DiffIgnore
    private String description;

    @Column(name = "status")
    private String status;


    @Column(name = "mvno_id", updatable = false)
    @DiffIgnore
    private Integer mvnoId;

    @Column(name = "total_in_ports")
    @DiffIgnore
    private Integer totalInPorts;
    @Column(name = "available_in_ports")
    @DiffIgnore
    private Integer availableInPorts;
    @Column(name = "total_out_ports")
    @DiffIgnore
    private Integer totalOutPorts;
    @Column(name = "available_out_ports")
    @DiffIgnore
    private Integer availableOutPorts;

    @Column(name = "rms_product_id")
    @DiffIgnore
    private String productId;

    @Column(name = "nav_ledger_id")
    @DiffIgnore
    private String navLedgerId;

    @Column(name = "isoemconsider")
    private boolean hasOEMConsider ;

    @Column(name = "isassetconsider")
    private Boolean hasAssetConsider;

    public Product(Long id) {
        this.id = id;
    }

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @OneToOne(targetEntity = ProductCategory.class)
    @JoinColumn(referencedColumnName = "product_id",name ="pc_id" )
    private ProductCategory productCategory;

    private Integer expiryTime;

    private String expiryTimeUnit;

    @Column(name = "refurb_pra_in_wrty")
    private Double refurburshiedProductRefAmountInWarranty;

    @Column(name = "refurb_pra_post_wrty")
    private Double refurburshiedProductRefAmountPostWarranty;

    @Column(name = "new_pra_in_wrty")
    private Double newProductRefAmountInWarranty;

    @Column(name = "new_pra_post_wrty")
    private Double newProductRefAmountPostWarranty;


    @Column(name = "case_id")
    @DiffIgnore
    private Long caseId;

    @OneToOne(targetEntity = Vendor.class)
    @JoinColumn(referencedColumnName = "vendor_id", name = "vendorid")
    @DiffIgnore
    private Vendor vendor;

    @OneToOne(targetEntity = Charge.class)
    @DiffIgnore
    @JoinColumn(referencedColumnName = "chargeid", name = "new_prod_charge_id")
    private Charge newProductCharge;

    @OneToOne(targetEntity = Charge.class)
    @DiffIgnore
    @JoinColumn(referencedColumnName = "chargeid", name = "refurb_prod_charge_id")
    private Charge refurburshiedProductCharge;

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


    @Column(name="refurburshiedproducttax")
    private Long refurburshiedProductTax;

    @Column(name="newproducttax")
    private Long newProductTax;

    @Transient
    private String refurburshiedProductTaxName;

    @Transient
    private String newProductTaxName;

    @Transient
    @DiffIgnore
    private List<SpecificationParametersDTO> specificationParametersDTOList;

    @Column(name = "filename")
    private String filename;
    @Column(name = "uniquename")
    private String uniquename;
    @Column(name = "license_date")
    private LocalDate licenseDate;

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

    public Product(Product product){
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
        this.hasOEMConsider = product.getHasAssetConsider();
        this.hasAssetConsider = product.getHasAssetConsider();
        this.isDeleted = product.getIsDeleted();
        this.productCategory = product.getProductCategory();
        this.expiryTime = product.getExpiryTime();
        this.expiryTimeUnit = product.getExpiryTimeUnit();
        this.refurburshiedProductRefAmountInWarranty = product.getRefurburshiedProductRefAmountInWarranty();
        this.refurburshiedProductRefAmountPostWarranty = product.getRefurburshiedProductRefAmountPostWarranty();
        this.newProductRefAmountInWarranty = product.getNewProductRefAmountInWarranty();
        this.newProductRefAmountPostWarranty = product.getNewProductRefAmountPostWarranty();
        this.caseId = product.getCaseId();
        this.vendor = product.getVendor();
        this.newProductCharge = product.getNewProductCharge();
        this.refurburshiedProductCharge = product.getRefurburshiedProductCharge();
        this.actualpricenewProduct = product.getActualpricenewProduct();
        this.actualpricerefurbishedProduct = product.getActualpricerefurbishedProduct();
        this.newProductAmount = product.getNewProductAmount();
        this.refurburshiedProductAmount = product.getRefurburshiedProductAmount();
        this.newProductTax = product.getNewProductTax();
        this.newPrice = product.getNewPrice();
        this.refurburshiedPrice = product.getRefurburshiedPrice();
        this.refurburshiedProductTax = product.getRefurburshiedProductTax();
        this.refurburshiedProductTaxName = product.getRefurburshiedProductTaxName();
        this.newProductTaxName = product.getNewProductTaxName();
        this.specificationParametersDTOList = product.getSpecificationParametersDTOList();
        this.filename = product.getFilename();
        this.uniquename = product.getUniquename();
    }

    public Product(Long id , String name) {
        this.id = id;
        this.name = name;
    }
}
