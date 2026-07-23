package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmserializeditem")
public class Item extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serialized_item_id")
    @DiffIgnore
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

//    @Column(name = "status")
//    private String status;

    @Column(name = "item_condition")
    private String condition;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "current_inward_id")
    private Long currentInwardId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "warranty_period", nullable = false)
    private Integer warrantyPeriod;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "current_inward_type")
    private String currentInwardType;

    @Column(name = "item_status")
    private String itemStatus;

    @Column(name = "remaining_days")
    private  String remainingDays;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "intransiant_warrenty")
    private String intransiantWarrenty;


    @Column(name = "intransiant_ownership")
    private String intransiantOwnership;

    @Column(name = "intransiant_warrenty_status")
    private String intransiantWarrentyStatus;

    @Column(name="expiry_date")
    private LocalDateTime expireDate;

    @Column(name="intransiant_expiry_date")
    @DiffIgnore
    private LocalDateTime intransiantexpireDate;

    @Column(name="inven_spec_id")
    private Long invenSpecId;

    @Column(name = "assetid")
    private String assetId;

    @Column(name = "oemstartdate")
    private LocalDate oemStartDate;

    @Column(name = "oemenddate")
    private LocalDate oemEndDate;

    @Column(name = "oem_warranty_days")
    private Integer oemWarrantyRemainingDays;

    @Column(name = "oem_warranty_status")
    private String oemWarrantyStatus;

    @Transient
    private Double productRefundAmount;
    @Transient
    private boolean refundFlag;

    private String remarks;

    @Transient
    private String removeFrom;

    public Item(Long id) {
        this.id = id;
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

    public Item(String macAddress, String serialNumber, String name, String condition, Integer mvnoId, Long ownerId, String ownerType,
                String currentInwardType, Long currentInwardId, Long productId, String ownershipType, String itemStatus,
                Integer warrantyPeriod, String warranty, Long invenSpecId) {
    this.macAddress = macAddress;
    this.serialNumber = serialNumber;
    this.name = name;
    this.condition= condition;
    this.mvnoId = mvnoId;
    this.ownerId = ownerId;
    this.ownerType = ownerType;
    this.currentInwardType = currentInwardType;
    this.currentInwardId=currentInwardId;
    this.productId=productId;
    this.ownershipType = ownershipType;
    this.itemStatus=itemStatus;
    this.warrantyPeriod=warrantyPeriod;
    this.warranty=warranty;
    this.invenSpecId=invenSpecId;
    }

    public Item(Long id, String condition, String ownershipType) {
        this.id = id;
        this.condition = condition;
        this.ownershipType = ownershipType;
    }

    public Item(Long id, String name, String macAddress, String serialNumber, String assetId, String condition, Long productId) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
        this.assetId = assetId;
        this.condition = condition;
        this.productId = productId;
    }

}
