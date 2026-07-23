package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblipallocationdtls")
@EntityListeners(AuditableListener.class)
public class IPAllocation extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "terminated_date")
    private LocalDateTime terminatedDate;

    @Column(name = "is_system_updated")
    private Boolean isSystemUpdated;

    @Column(name = "termination_reason")
    private String terminationReason;
    private Long poolDetailsId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return getId();
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
