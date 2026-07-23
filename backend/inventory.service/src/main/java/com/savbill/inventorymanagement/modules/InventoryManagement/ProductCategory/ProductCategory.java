package com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmproductcategory")
@EntityListeners(AuditableListener.class)
public class ProductCategory extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "has_mac")
    private boolean hasMac;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "rms_product_id")
    private String productId;

    public ProductCategory(Long id) {
        this.id = id;
    }

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "has_serial")
    private boolean hasSerial;

    @Column(name = "has_trackable")
    private boolean hasTrackable;
    @Column(name = "has_port")
    private boolean hasPort;

    @Column(name="has_cas")
    private boolean hasCas;

    @Column(name="dtvcategory")
    private String dtvCategory;

    @Column(name="device_type")
    private String deviceType;

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

    public ProductCategory(ProductCategory productCategory){
        this.id = productCategory.getId();
        this.name = productCategory.getName();
        this.unit = productCategory.getUnit();
        this.mvnoId = productCategory.getMvnoId();
        this.hasMac = productCategory.isHasMac();
        this.type = productCategory.getType();
        this.status = productCategory.getStatus();
        this.productId = productCategory.getProductId();
        this.isDeleted = productCategory.getIsDeleted();
        this.hasSerial = productCategory.isHasSerial();
        this.hasPort = productCategory.isHasPort();
        this.hasCas = productCategory.isHasCas();
        this.dtvCategory = productCategory.getDtvCategory();
    }

    public ProductCategory(boolean hasMac, boolean hasSerial, boolean hasTrackable, boolean hasPort,
                           boolean hasCas, String unit, String type, String dtvCategory, String name, String deviceType) {
        this.hasMac = hasMac;
        this.hasSerial = hasSerial;
        this.hasTrackable = hasTrackable;
        this.hasPort = hasPort;
        this.hasCas = hasCas;
        this.unit = unit;
        this.type = type;
        this.dtvCategory = dtvCategory;
        this.name = name;
        this.deviceType = deviceType;
    }
}
