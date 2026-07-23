package com.savbill.radius.ippool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "tblipallocationdtls")
public class IPPoolAllocationDtls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pool_details_id")
    private Long poolDetailsId;
    @Column(name = "pool_id")
    private Long poolId;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "status")
    private String status;
    @Column(name = "is_delete")
    private Boolean isDelete = false;
    @Column(name = "block_by_cust_id")
    private Long blockByCustId;
    @Column(name = "block_by_session_id")
    private String blockBySessionId;
    @Column(name = "nas_ip_address")
    private String nasIpAddress;

    @Column (name="createdate")
    private Timestamp createdOn;
    @Column (name="lastmodificationdate")
    private Timestamp lastModifiedOn;

    @JsonIgnore
    public Long getPrimaryKey() {
        return poolDetailsId;
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
