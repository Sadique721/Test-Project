package com.savbill.radius.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Table(name = "TBLTMACADDRESSMAPPING")
public class MacAddressMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Customer mac address Id")
    @Column(name = "macaddressid", nullable = false)
    private Long macAddressId;

    @ApiModelProperty(notes = "This is user name of customer Id")
    @Column(name = "custid", nullable = false)
    private Long customerId;

    @ApiModelProperty(notes = "This is user name of customer Mac Address")
    @Column(name = "macaddress", nullable = true, length = 250)
    private String macAddress;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdate")
    private Timestamp createDate;

    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate")
    private Timestamp lastModificationDate;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdby")
    private String createdBy;

    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodifiedby")
    private String lastModifiedBy;

    @Column(name = "custsermappingid")
    private Integer custsermappingid;

    @ApiModelProperty(hidden = true)
    @Column(name = "macretentiondate")
    private Timestamp macRetentionDate;

    @ApiModelProperty(notes = "This is user name of customer Mac Address")
    @Column(name = "normalizemac", nullable = true, length = 250)
    private String normalizeMac;

    public Integer getCustsermappingid() {
        return custsermappingid;
    }

    public void setCustsermappingid(Integer custsermappingid) {
        this.custsermappingid = custsermappingid;
    }

    public Long getMacAddressId() {
        return macAddressId;
    }

    public void setMacAddressId(Long macAddressId) {
        this.macAddressId = macAddressId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Timestamp lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Timestamp getMacRetentionDate() {
        return macRetentionDate;
    }

    public void setMacRetentionDate(Timestamp macRetentionDate) {
        this.macRetentionDate = macRetentionDate;
    }

    public MacAddressMapping() {
        super();
    }

    public String getNormalizeMac() {
        return normalizeMac;
    }

    public void setNormalizeMac(String normalizeMac) {
        this.normalizeMac = normalizeMac;
    }

    public MacAddressMapping(MacAddressMappingDto macAddressMappingDto) {
        this.customerId = macAddressMappingDto.getCustomerId();
        this.macAddress = macAddressMappingDto.getMacAddress();
        this.macRetentionDate = macAddressMappingDto.getMacRetentionDate();
        this.normalizeMac = macAddressMappingDto.getMacAddress().replace(":", "").replace("-", "").replace(".", "");
    }

    public MacAddressMapping(Map map) {
        if (map.get("macAddress") != null)
            this.macAddress = map.get("macAddress").toString();
    }
}
