package com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tblmbulkconsumption")
@EntityListeners(AuditableListener.class)

public class BulkConsumption  extends Auditable implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bulkconsumption_id")
    private Long id;

    @Column(name = "name")
    private String bulkConsumptionName;

//    @Column(name = "status")
//    private String bulkConsumptionStatus;

    @DiffIgnore
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;


    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltbulkconsumptionmacmapping", joinColumns = {@JoinColumn(name = "bulkconsumptionid")}
            , inverseJoinColumns = {@JoinColumn(name = "mac_mapping_id")})
    @DiffIgnore
    private List<InOutWardMACMapping> itemListLongId = new ArrayList<>();

    @Column(name = "product_id")
    @DiffIgnore
    private Long productId;

    @Column(name = "inward_id")
    @DiffIgnore
    private Long inwardId;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "approval_remark")
    private String approvalRemark;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "itemtype")
    private String itemType;

    @DiffIgnore
    @Column(name = "ownerid")
    private Long ownerId;

    @Column(name = "ownertype")
    private String ownerType;
    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return  this.isDeleted;
    }

    public BulkConsumption(BulkConsumption bulkConsumption){
        this.id = bulkConsumption.getId();
        this.bulkConsumptionName = getBulkConsumptionName();
        this.mvnoId = bulkConsumption.getMvnoId();
        this.itemListLongId = bulkConsumption.getItemListLongId();
        this.productId = bulkConsumption.getProductId();
        this.inwardId = bulkConsumption.getInwardId();
        this.approvalStatus = bulkConsumption.getApprovalStatus();
        this.approvalRemark = bulkConsumption.getApprovalRemark();
        this.isDeleted = bulkConsumption.getIsDeleted();
        this.itemType = bulkConsumption.getItemType();
        this.ownerId = bulkConsumption.getOwnerId();
        this.ownerType = bulkConsumption.getOwnerType();

    }
}
