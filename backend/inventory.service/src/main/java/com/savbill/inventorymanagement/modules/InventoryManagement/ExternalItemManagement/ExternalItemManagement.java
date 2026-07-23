package com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmexternalitemmanagement")
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class ExternalItemManagement extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_item_id")
    private Long id;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "external_item_number")
    @DiffIgnore
    private String externalItemGroupNumber;

    @ToString.Exclude
    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "product_id")
    private Product productId;

    @Column(name = "quantity")
    private Long qty;

    @Column(name = "used_qty")
    @DiffIgnore
    private Long usedQty;

    @Column(name = "unused_qty")
    @DiffIgnore
    private Long unusedQty;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "status")
    private String status;

    @Column(name = "mvno_id", updatable = false)
    @DiffIgnore
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "servicearea_id")
    @DiffIgnore
    private ServiceArea serviceAreaId;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @Column(name = "rejected_qty")
    @DiffIgnore
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "total_mac_serial")
    @DiffIgnore
    private Long totalMacSerial;

    @Column(name = "approval_remark")
    private String approvalRemark;


    @Column(name = "owner_id")
    @DiffIgnore
    private Long ownerId;


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
    public ExternalItemManagement(ExternalItemManagement externalItemManagement){
        this.id = externalItemManagement.getId();
        this.externalItemGroupNumber = externalItemManagement.getExternalItemGroupNumber();
        this.productId = externalItemManagement.getProductId();
        this.qty = externalItemManagement.getQty();
        this.usedQty = externalItemManagement.getUsedQty();
        this.unusedQty = externalItemManagement.getUnusedQty();
        this.ownershipType = externalItemManagement.getOwnershipType();
        this.status = externalItemManagement.getStatus();
        this.mvnoId = externalItemManagement.getMvnoId();
        this.isDeleted = externalItemManagement.getIsDeleted();
        this.serviceAreaId = externalItemManagement.getServiceAreaId();
        this.inTransitQty = externalItemManagement.getInTransitQty();
        this.approvalRemark = externalItemManagement.getApprovalRemark();
        this.totalMacSerial = externalItemManagement.getTotalMacSerial();
        this.approvalStatus = externalItemManagement.getApprovalStatus();
        this.ownerId = externalItemManagement.getOwnerId();

    }
}
