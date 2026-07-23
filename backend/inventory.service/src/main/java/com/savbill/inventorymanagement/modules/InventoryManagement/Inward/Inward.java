package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblminward")
@EntityListeners(AuditableListener.class)
public class Inward extends Auditable implements IBaseData<Long> {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inward_id")
    private Long id;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inward_number")
    private String inwardNumber;

    @ToString.Exclude
    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "product_id")
    Product productId;


    @Column(name = "quantity")
    Long qty;

    @Column(name = "used_qty")
    Long usedQty;

    @Column(name = "unused_qty")
    Long unusedQty;

    @Column(name = "inward_date_time")
    LocalDateTime inwardDateTime;

//    @ToString.Exclude
//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    WareHouse wareHouseId;

    @Column(name = "type")
    String type;

    @Column(name = "status")
    String status;


    @Column(name = "mvno_id", updatable = false)
    @DiffIgnore
    private Integer mvnoId;


    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_id")
    @DiffIgnore
    private Long sourceId;

    @Column(name = "destination_type")
    private String destinationType;

    @Column(name = "destination_id")
    @DiffIgnore
    private Long destinationId;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @ToString.Exclude
    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "service_area_id")
    private ServiceArea serviceArea;

    @ManyToOne(targetEntity = Outward.class)
    @DiffIgnore
    @JoinColumn(name = "outward_id", referencedColumnName = "outward_id")
    Outward outwardId;

    @Column(name = "out_transit_qty")
    private Long outTransitQty;

    @DiffIgnore
    @Column(name = "rejected_qty")
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "rms_inward_id")
    @DiffIgnore
    private String rmsInwardId;

    @Column(name = "nav_inward_id")
    @DiffIgnore
    private String navInwardId;

    @DiffIgnore
    @Column(name = "total_mac_serial")
    private Long totalMacSerial;

    @Column(name = "approval_remark")
    private String approvalRemark;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "assign_non_serialized_item_qty")
    private Long assignNonSerializedItemQty;
//
//    @OneToMany(targetEntity = InOutWardMACMapping.class,fetch = FetchType.LAZY)
//    @JoinColumn(name = "inward_id")
//    List<InOutWardMACMapping> inOutWardMACMappingList;

    @Column(name = "request_inventory_id")
    @DiffIgnore
    private Long requestInventoryId;

    @Column(name = "warrantystartdate")
    private LocalDate startDateTime;

    @Column(name = "warrantyenddate")
    private LocalDate expiryDateTime;


    @Column(name = "oem_warranty_days")
    private Integer oemWarrantyRemainingDays;

    @Column(name = "oem_warranty_status")
    private String oemWarrantyStatus;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "is_group")
    private Boolean isGroup= false;

    @Transient
    @DiffIgnore
    List<SpecificationParametersDTO> specificationParametersDTOS = new ArrayList<>();
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

    public Inward(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Inward   toString Override :" + inwardNumber;
    }

    public Inward(Inward inward){
        this.id = inward.getId();
        this.inwardNumber = inward.getInwardNumber();
        this.productId = inward.getProductId();
        this.qty = inward.getQty();
        this.usedQty = inward.getUsedQty();
        this.unusedQty = inward.getUnusedQty();
        this.inwardDateTime = inward.getInwardDateTime();
        this.type = inward.getType();
        this.status = inward.getStatus();
        this.mvnoId = inward.getMvnoId();
        this.isDeleted = inward.getIsDeleted();
        this.sourceId = inward.getSourceId();
        this.sourceType = inward.getSourceType();
        this.destinationId = inward.getDestinationId();
        this.destinationType = inward.getDestinationType();
        this.inTransitQty = inward.getInTransitQty();
        this.serviceArea = inward.getServiceArea();
        this.outwardId = inward.getOutwardId();
        this.outTransitQty = inward.getOutTransitQty();
        this.rejectedQty = inward.getRejectedQty();
        this.approvalStatus = inward.getApprovalStatus();
        this.categoryType = inward.getCategoryType();
        this.rmsInwardId = inward.getRmsInwardId();
        this.navInwardId = inward.getNavInwardId();
        this.totalMacSerial = inward.getTotalMacSerial();
        this.approvalRemark = inward.getApprovalRemark();
        this.description = inward.getDescription();
        this.assignNonSerializedItemQty = inward.getAssignNonSerializedItemQty();
        this.requestInventoryId = inward.getRequestInventoryId();
        this.startDateTime = inward.getStartDateTime();
        this.expiryDateTime = inward.getExpiryDateTime();
        this.oemWarrantyStatus = inward.getOemWarrantyStatus();
        this.oemWarrantyRemainingDays = inward.getOemWarrantyRemainingDays();
        this.specificationParametersDTOS = inward.getSpecificationParametersDTOS();
        this.groupId = inward.getGroupId();
        this.isGroup = inward.getIsGroup();


    }

    public Inward(Long id,String inwardNumber,Product productId,String type,Long qty,Long inTransitQty,String status,String approvalStatus,String createdByName,String destinationType , Integer createdById,Long destinationId,Long totalMacSerial,Long outwardId){
        this.id = id;
        this.inwardNumber = inwardNumber;
        this.productId = productId;
        this.type = type;
        this.qty = qty;
        this.inTransitQty = inTransitQty;
        this.status = status;
        this.approvalStatus = approvalStatus;
        if (outwardId != null) {  // Only set if not null
            this.outwardId = new Outward();
            this.outwardId.setId(outwardId);
        } else {
            this.outwardId = null;  // Explicitly set to null
        }
        this.setCreatedByName(createdByName);
        this.setCreatedById(createdById);
        this.destinationType = destinationType;
        this.destinationId = destinationId;
        this.totalMacSerial = totalMacSerial;
    }

    public Inward(String sourceType, Long sourceId, Long destinationId, String destinationType, Long inTransitQty) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.destinationType = destinationType;
        this.inTransitQty = inTransitQty;
    }


    public Inward(Long id,String inwardNumber,Product productId,String type,Long qty,Long inTransitQty,String status,String approvalStatus,String createdByName,String destinationType , Integer createdById,Long destinationId,Long totalMacSerial,Long outwardId,Long groupId,Boolean isGroup){
        this.id = id;
        this.inwardNumber = inwardNumber;
        this.productId = productId;
        this.type = type;
        this.qty = qty;
        this.inTransitQty = inTransitQty;
        this.status = status;
        this.approvalStatus = approvalStatus;
        if (outwardId != null) {  // Only set if not null
            this.outwardId = new Outward();
            this.outwardId.setId(outwardId);
        } else {
            this.outwardId = null;  // Explicitly set to null
        }
        this.setCreatedByName(createdByName);
        this.setCreatedById(createdById);
        this.destinationType = destinationType;
        this.destinationId = destinationId;
        this.totalMacSerial = totalMacSerial;
        this.groupId = groupId;
        this.isGroup = isGroup;
    }


}
