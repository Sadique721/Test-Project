package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblippool")
@EntityListeners(AuditableListener.class)
public class IPPool extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poolId;
    private String poolName;
    private String displayName;
    private String poolType;
    private String poolCategory;
    private String ipRange;
    private String netMask;
    private String networkIp;
    private String broadcastIp;
    private String firstHost;
    private String lastHost;
    private Integer totalHost;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isStaticIpPool = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean defaultPoolFlag = false;
    private String status;
    private String remark;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer mvnoId;
    
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return poolId;
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
