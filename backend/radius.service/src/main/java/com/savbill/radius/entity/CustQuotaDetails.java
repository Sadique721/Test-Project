package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@Table(name = "tblcustquotadtls")
public class CustQuotaDetails {

    public CustQuotaDetails() {
    }

    @Id
    @Column(name = "quotadtlsid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "planid")
    private Long planId;

    @Column(name = "quotatype")
    private String quotaType;

    @Column(name = "totalquota")
    private Double totalQuota = 0.0;

    @Column(name = "usedquota")
    private Double usedQuota = 0.0;

    @Column(name = "quotaunit")
    private String quotaUnit;

    @Column(name = "timetotalquota")
    private Double timeTotalQuota = 0.0;

    @Column(name = "timequotaused")
    private Double timeQuotaUsed = 0.0;

    @Column(name = "timequotaunit")
    private String timeQuotaUnit;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    @Column(name = "totalquotakb")
    private Double totalQuotaKB = 0.0;

    @Column(name = "usedquotakb")
    private Double usedQuotaKB = 0.0;

    @Column(name = "timeusedquotasec")
    private Double timeUsedQuotaSec = 0.0;

    @Column(name = "timetotalquotasec")
    private Double timeTotalQuotaSec = 0.0;

    @Column(name = "currentsessionusagetime")
    private Double currentSessionUsageTime = 0.0;

    @Column(name = "currentsessionusagevolume")
    private Double currentSessionUsageVolume = 0.0;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custpackageid")
    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
    private CustPlanMappping custPlanMappping;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JoinColumn(name = "custid")
//    private Customers customer;

    @Column(name = "custid")
    private Integer custid;

    private Double didtotalquota;
    private Double didusedquota;
    private Double intercomtotalquota;
    private Double intercomusedquota;
    private String didQuotaUnit;
    private String intercomQuotaUnit;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @Column(name = "parnet_quota_type")
    private String parentQuotaType;

    @Column(name = "is_chunk_available")
    private boolean isChunkAvailable;

    @Column(name = "reserved_quota_in_per")
    private Double reservedQuotaInPer;

    //Not in Percentage will be show total reserve quota in KB
    @Column(name = "total_reserved_quota")
    private Double totalReservedQuota;
    @Column(name = "usage_quota_type")
    private String usageQuotaType;

    @Column(name = "skip_quota_update")
    private Boolean skipQuotaUpdate;

    @Column(name = "last_quota_reset", nullable = false, updatable = true)
    private LocalDateTime lastQuotaReset;

    @Column(name = "isquotaupdateskipped")
    private Boolean isQuotaUpdateSkipped;

    @Transient
    private Long cprId;


    public CustQuotaDetails(Map custQuotaDetails) {
        if (custQuotaDetails.get("id") != null)
            this.id = Integer.parseInt(custQuotaDetails.get("id").toString());
        if (custQuotaDetails.get("planId") != null)
            this.planId = Long.parseLong(custQuotaDetails.get("planId").toString());
        if (custQuotaDetails.get("quotaType") != null)
            this.quotaType = custQuotaDetails.get("quotaType").toString();
        if (custQuotaDetails.get("totalQuota") != null)
            this.totalQuota = Double.parseDouble(custQuotaDetails.get("totalQuota").toString());
        if (custQuotaDetails.get("usedQuota") != null)
            this.usedQuota = Double.parseDouble(custQuotaDetails.get("usedQuota").toString());
        if (custQuotaDetails.get("quotaUnit") != null)
            this.quotaUnit = custQuotaDetails.get("quotaUnit").toString();
        if (custQuotaDetails.get("timeTotalQuota") != null)
            this.timeTotalQuota = Double.parseDouble(custQuotaDetails.get("timeTotalQuota").toString());
        if (custQuotaDetails.get("timeQuotaUsed") != null)
            this.timeQuotaUsed = Double.parseDouble(custQuotaDetails.get("timeQuotaUsed").toString());
        if (custQuotaDetails.get("timeQuotaUnit") != null)
            this.timeQuotaUnit = custQuotaDetails.get("timeQuotaUnit").toString();
        if (custQuotaDetails.get("isDelete") != null)
            this.isDelete = Boolean.parseBoolean(custQuotaDetails.get("isDelete").toString());
        if (custQuotaDetails.get("totalQuotaKB") != null)
            this.totalQuotaKB = Double.parseDouble(custQuotaDetails.get("totalQuotaKB").toString());
        if (custQuotaDetails.get("usedQuotaKB") != null)
            this.usedQuotaKB = Double.parseDouble(custQuotaDetails.get("usedQuotaKB").toString());
        if (custQuotaDetails.get("timeUsedQuotaSec") != null)
            this.timeUsedQuotaSec = Double.parseDouble(custQuotaDetails.get("timeUsedQuotaSec").toString());
        if (custQuotaDetails.get("timeTotalQuotaSec") != null)
            this.timeTotalQuotaSec = Double.parseDouble(custQuotaDetails.get("timeTotalQuotaSec").toString());
        if (custQuotaDetails.get("didtotalquota") != null)
            this.didtotalquota = Double.parseDouble(custQuotaDetails.get("didtotalquota").toString());
        if (custQuotaDetails.get("didusedquota") != null)
            this.didusedquota = Double.parseDouble(custQuotaDetails.get("didusedquota").toString());
        if (custQuotaDetails.get("intercomtotalquota") != null)
            this.intercomtotalquota = Double.parseDouble(custQuotaDetails.get("intercomtotalquota").toString());
        if (custQuotaDetails.get("intercomusedquota") != null)
            this.intercomusedquota = Double.parseDouble(custQuotaDetails.get("intercomusedquota").toString());
        if (custQuotaDetails.get("didQuotaUnit") != null)
            this.didQuotaUnit = custQuotaDetails.get("didQuotaUnit").toString();
        if (custQuotaDetails.get("intercomQuotaUnit") != null)
            this.intercomQuotaUnit = custQuotaDetails.get("intercomQuotaUnit").toString();
        if (custQuotaDetails.get("customer") != null)
            this.custid = Integer.parseInt(custQuotaDetails.get("customer").toString());
        if (custQuotaDetails.get("createdByStaffId") != null)
            this.createdById = Integer.parseInt(custQuotaDetails.get("createdByStaffId").toString());
        if (custQuotaDetails.get("lastModifiedByStaffId") != null)
            this.lastModifiedById = Integer.parseInt(custQuotaDetails.get("lastModifiedByStaffId").toString());
        if (custQuotaDetails.get("createdByName") != null)
            this.createdByName = custQuotaDetails.get("createdByName").toString();
        if (custQuotaDetails.get("updatedByName") != null)
            this.lastModifiedByName = custQuotaDetails.get("updatedByName").toString();
        if (custQuotaDetails.get("parentQuotaType") != null)
            this.parentQuotaType = custQuotaDetails.get("parentQuotaType").toString();
        if (custQuotaDetails.get("isChunkAvailable") != null)
            this.isChunkAvailable = Boolean.parseBoolean(custQuotaDetails.get("isChunkAvailable").toString());
        if (custQuotaDetails.get("reservedQuotaInPer") != null)
            this.reservedQuotaInPer = Double.valueOf(custQuotaDetails.get("reservedQuotaInPer").toString());
        if (custQuotaDetails.get("totalReservedQuota") != null)
            this.totalReservedQuota = Double.valueOf(custQuotaDetails.get("totalReservedQuota").toString());
        if (custQuotaDetails.get("usageQuotaType") != null)
            this.usageQuotaType = custQuotaDetails.get("usageQuotaType").toString();
        if (custQuotaDetails.get("skipQuotaUpdate") != null)
            this.skipQuotaUpdate = Boolean.parseBoolean(custQuotaDetails.get("skipQuotaUpdate").toString());
        else
            this.skipQuotaUpdate = false;//CommonConstants.TOTAL;
    }
}
