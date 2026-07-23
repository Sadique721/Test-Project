package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "tblhitemhistory")
@EntityListeners(AuditableListener.class)
public class InOutWardMACMapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mac_mapping_id")
    private Long id;

    @Column(name = "inward_id", nullable = false)
    private Long inwardId;

    @Column(name = "outward_id")
    private Long outwardId;

    @Column(name = "status")
    String status;

    @Column(name = "mac")
    String macAddress;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "cust_inventory_mapping_id")
    private Long custInventoryMappingId;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "mvno_id", updatable = false)
    @DiffIgnore
    private Integer mvnoId;

    private Integer currentApproveId;
    private Integer previousApproveId;
    private Integer teamHierarchyMappingId;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "inward_id_of_outward")
    private Long inwardIdOfOutward;

    @Column(name = "is_forwarded", columnDefinition = "Boolean default false", nullable = false)
    private Integer isForwarded = 0;

    @Column(name = "is_returned", columnDefinition = "Boolean default false", nullable = false)
    private Integer isReturned = 0;

    @Column(name = "remark")
    private String remark;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    @Column(name = "bulkconsumption_id")
    private Long bulkConsumptionId;

    @Column(name = "non_serialized_item_id")
    private Long nonSerializedItemId;

    @Column(name = "in_replacement_process")
    private Boolean inReplacementProcess;

    @Transient
    private String itemStatus;



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

    @Override
    public String toString() {
        return "id";
    }

    public InOutWardMACMapping(Long inwardId, Long outwardId, Long itemId, String macAddress,
                               String serialNumber, String createdByName, int createdById,
                               LocalDateTime createdDate) {
        super(createdByName, createdById, createdDate);
        this.inwardId = inwardId;
        this.outwardId = outwardId;
        this.itemId = itemId;
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
        this.isDeleted = false;
        this.isForwarded = 0;
        this.status = CommonConstants.ACTIVE_STATUS;
    }
}
