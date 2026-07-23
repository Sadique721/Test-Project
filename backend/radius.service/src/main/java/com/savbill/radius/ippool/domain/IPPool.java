package com.savbill.radius.ippool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tblippool")
public class IPPool  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poolId;
    private String poolName;
    private String usageCategory;
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
    private String status;
    private String remark;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Long mvnoId;

    @Column (name="createdate")
    private Timestamp createdOn;

    @Column (name="lastmodificationdate")
    private Timestamp lastModifiedOn;
    
    @JsonIgnore
    public Long getPrimaryKey() {
        return poolId;
    }

    @JsonIgnore
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
