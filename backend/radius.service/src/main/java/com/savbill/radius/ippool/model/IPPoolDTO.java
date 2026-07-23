package com.savbill.radius.ippool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class IPPoolDTO {
    private Long poolId;
    private String poolName;
    private String ipRange;
    private String usageCategory;
    private String netMask;
    private String networkIp;
    private String broadcastIp;
    private String firstHost;
    private String lastHost;
    private Integer totalHost;
    private Boolean isDelete = false;
    private String status;
    private String remark;
    private Long mvnoId;

    private Timestamp createdOn;
    private Timestamp lastModifiedOn;
    @JsonIgnore
    public Long getIdentityKey() {
        return poolId;
    }

    public Long getMvnoId() {
        return mvnoId;
    }
}
