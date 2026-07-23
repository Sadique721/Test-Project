package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmoutward")
@EntityListeners(AuditableListener.class)
public class Outward extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outward_id")
    private Long id;

    @Column(name = "outward_number")
    @DiffIgnore
    private String outwardNumber;

    @Column(name = "quantity")
    Long qty;

//    @Column(name = "user_type")
//    String userType;

    @Column(name = "status")
    String status;


    @ManyToOne(targetEntity = Product.class)
    @DiffIgnore
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    Product productId;

//    @ManyToOne(targetEntity = WareHouse.class)
//    @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouse_id")
//    WareHouse wareHouseId;



//    @ManyToOne(targetEntity = StaffUser.class)
//    @Column(name = "staff_id")
//    Long staffId;


    @ManyToOne(targetEntity = Inward.class)
    @DiffIgnore
    @JoinColumn(name = "inward_id", referencedColumnName = "inward_id")
    Inward inwardId;

    @DiffIgnore
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "outward_date_time")
    LocalDateTime outwardDateTime;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "used_qty")
    Long usedQty;

    @DiffIgnore
    @Column(name = "unused_qty")
    Long unusedQty;



    private transient String productName;
    private transient String wareHouseName;
    private transient String inwardNumber;
    private transient String unit;


    @Column(name = "source_type")
    private String sourceType;

    @DiffIgnore
    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "destination_type")
    private String destinationType;

    @DiffIgnore
    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @ToString.Exclude
    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "service_area_id")
    private ServiceArea serviceArea;

    @Column(name = "out_transit_qty")
    private Long outTransitQty;

    @Column(name = "rejected_qty")
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "category_type")
    private String categoryType;

    @DiffIgnore
    @Column(name = "rms_outward_id")
    private String rmsOutwardId;

    @DiffIgnore
    @Column(name = "nav_outward_id")
    private String navOutwardId;

    @Column(name = "approval_remark")
    private String approvalRemark;

    @Column(name = "type")
    private String type;

    @DiffIgnore
    @Column(name = "request_inventory_id")
    private Long requestInventoryId;

    @DiffIgnore
    @Column(name="request_inventory_product_id")
    private Long requestInventoryProductId;

    @Column(name = "selecteditems")
    private Long selectedItems;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name="is_group")
    private boolean isGroup= false;

    @Column(name="file_name")
    private String fileName;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public Outward(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Outward   toString Override :" + id;
    }

    public Outward(Outward outward){
        this.id = outward.getId();
        this.outwardNumber = outward.getOutwardNumber();
        this.qty = outward.getQty();
        this.status = outward.getStatus();
        this.productId = outward.getProductId();
        this.mvnoId = outward.getMvnoId();
        this.outwardDateTime = outward.getOutwardDateTime();
        this.isDeleted = outward.getIsDeleted();
        this.usedQty = outward.getUsedQty();
        this.unusedQty = outward.getUnusedQty();
        this.productName = outward.getProductName();
        this.wareHouseName = outward.getWareHouseName();
        this.inwardNumber = outward.getInwardNumber();
        this.inwardId = outward.getInwardId();
        this.unit = outward.getUnit();
        this.sourceId = outward.getSourceId();
        this.sourceType = outward.getSourceType();
        this.destinationType = outward.getDestinationType();
        this.destinationId = outward.getDestinationId();
        this.inTransitQty = outward.getInTransitQty();
        this.serviceArea = outward.getServiceArea();
        this.outTransitQty = outward.getOutTransitQty();
        this.rejectedQty = outward.getRejectedQty();
        this.approvalRemark = outward.getApprovalRemark();
        this.categoryType = outward.getCategoryType();
        this.rmsOutwardId = outward.getRmsOutwardId();
        this.navOutwardId = outward.getNavOutwardId();
        this.approvalStatus = outward.getApprovalStatus();
        this.type = outward.getType();
        this.requestInventoryId = outward.getRequestInventoryId();
        this.requestInventoryProductId = outward.getRequestInventoryProductId();
        this.selectedItems = outward.getSelectedItems();
        this.description = outward.getDescription();


    }

    public Outward(Long id, String outwardNumber, String destinationType, String sourceType, String status,
                   String approvalStatus, Long inTransitQty, String createdBy, Long destinationId, Long sourceId,
                   Long selectedItems) {
        this.id = id;
        this.outwardNumber = outwardNumber;
        this.destinationType = destinationType;
        this.sourceType = sourceType;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.inTransitQty = inTransitQty;
        setCreatedByName(createdBy);
        this.destinationId = destinationId;
        this.sourceId = sourceId;
        this.selectedItems = selectedItems;
    }
    public Outward (String sourceType, Long sourceId) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }
}
