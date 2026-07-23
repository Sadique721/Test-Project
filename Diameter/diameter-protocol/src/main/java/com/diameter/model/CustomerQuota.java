package com.diameter.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tblcustquotadtls")
public class CustomerQuota {

	@Id
    private BigInteger quotaDtlsId;
	@NotNull
    private BigInteger custId;
	@NotNull
    private BigInteger planId;
	@NotBlank
    private String quotaType;
	@NotNull
    private BigDecimal totalQuota;
	@NotNull
    private BigDecimal usedQuota;
	@NotBlank
    private String quotaUnit;
	@NotNull
    private BigDecimal timeTotalQuota;
	@NotNull
    private BigDecimal timeQuotaUsed;
    private String timeQuotaUnit;
    private BigDecimal createdByStaffId;
    private Timestamp createDate;
    private BigDecimal lastModifiedByStaffId;
    private Timestamp lastModifiedDate;
    private Boolean isDeleted;
    private BigDecimal totalQuotaKb;
    private BigDecimal usedQuotaKb;
    private BigDecimal timeUsedQuotaSec;
    private BigDecimal timeTotalQuotaSec;
    @NotNull
    private Integer custPackageId;
    private BigDecimal didTotalQuota;
    private BigDecimal didUsedQuota;
    private BigDecimal intercomTotalQuota;
    private BigDecimal intercomUsedQuota;
    private String didQuotaUnit;
    private String intercomQuotaUnit;
    private String createByName;
    private String updateByName;
    private Boolean speedDowngradeFlag;
    private Boolean isFupApplied;
    private Timestamp fupAppliedDate;
    private BigDecimal currentSessionUsageTime;
    private BigDecimal currentSessionUsageVolume;
    private String parentQuotaType;
    private Boolean isChunkAvailable;
    private Double reservedQuotaInPer;
    private Double totalReservedQuota;
    private String usageQuotaType;
    private Boolean skipQuotaUpdate;
    private Timestamp lastQuotaReset;
    private Boolean isQuotaUpdateSkipped;
	public BigInteger getQuotaDtlsId() {
		return quotaDtlsId;
	}
	public void setQuotaDtlsId(BigInteger quotaDtlsId) {
		this.quotaDtlsId = quotaDtlsId;
	}
	public BigInteger getCustId() {
		return custId;
	}
	public void setCustId(BigInteger custId) {
		this.custId = custId;
	}
	public BigInteger getPlanId() {
		return planId;
	}
	public void setPlanId(BigInteger planId) {
		this.planId = planId;
	}
	public String getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(String quotaType) {
		this.quotaType = quotaType;
	}
	public BigDecimal getTotalQuota() {
		return totalQuota;
	}
	public void setTotalQuota(BigDecimal totalQuota) {
		this.totalQuota = totalQuota;
	}
	public BigDecimal getUsedQuota() {
		return usedQuota;
	}
	public void setUsedQuota(BigDecimal usedQuota) {
		this.usedQuota = usedQuota;
	}
	public String getQuotaUnit() {
		return quotaUnit;
	}
	public void setQuotaUnit(String quotaUnit) {
		this.quotaUnit = quotaUnit;
	}
	public BigDecimal getTimeTotalQuota() {
		return timeTotalQuota;
	}
	public void setTimeTotalQuota(BigDecimal timeTotalQuota) {
		this.timeTotalQuota = timeTotalQuota;
	}
	public BigDecimal getTimeQuotaUsed() {
		return timeQuotaUsed;
	}
	public void setTimeQuotaUsed(BigDecimal timeQuotaUsed) {
		this.timeQuotaUsed = timeQuotaUsed;
	}
	public String getTimeQuotaUnit() {
		return timeQuotaUnit;
	}
	public void setTimeQuotaUnit(String timeQuotaUnit) {
		this.timeQuotaUnit = timeQuotaUnit;
	}
	public BigDecimal getCreatedByStaffId() {
		return createdByStaffId;
	}
	public void setCreatedByStaffId(BigDecimal createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public BigDecimal getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	public void setLastModifiedByStaffId(BigDecimal lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public BigDecimal getTotalQuotaKb() {
		return totalQuotaKb;
	}
	public void setTotalQuotaKb(BigDecimal totalQuotaKb) {
		this.totalQuotaKb = totalQuotaKb;
	}
	public BigDecimal getUsedQuotaKb() {
		return usedQuotaKb;
	}
	public void setUsedQuotaKb(BigDecimal usedQuotaKb) {
		this.usedQuotaKb = usedQuotaKb;
	}
	public BigDecimal getTimeUsedQuotaSec() {
		return timeUsedQuotaSec;
	}
	public void setTimeUsedQuotaSec(BigDecimal timeUsedQuotaSec) {
		this.timeUsedQuotaSec = timeUsedQuotaSec;
	}
	public BigDecimal getTimeTotalQuotaSec() {
		return timeTotalQuotaSec;
	}
	public void setTimeTotalQuotaSec(BigDecimal timeTotalQuotaSec) {
		this.timeTotalQuotaSec = timeTotalQuotaSec;
	}
	public Integer getCustPackageId() {
		return custPackageId;
	}
	public void setCustPackageId(Integer custPackageId) {
		this.custPackageId = custPackageId;
	}
	public BigDecimal getDidTotalQuota() {
		return didTotalQuota;
	}
	public void setDidTotalQuota(BigDecimal didTotalQuota) {
		this.didTotalQuota = didTotalQuota;
	}
	public BigDecimal getDidUsedQuota() {
		return didUsedQuota;
	}
	public void setDidUsedQuota(BigDecimal didUsedQuota) {
		this.didUsedQuota = didUsedQuota;
	}
	public BigDecimal getIntercomTotalQuota() {
		return intercomTotalQuota;
	}
	public void setIntercomTotalQuota(BigDecimal intercomTotalQuota) {
		this.intercomTotalQuota = intercomTotalQuota;
	}
	public BigDecimal getIntercomUsedQuota() {
		return intercomUsedQuota;
	}
	public void setIntercomUsedQuota(BigDecimal intercomUsedQuota) {
		this.intercomUsedQuota = intercomUsedQuota;
	}
	public String getDidQuotaUnit() {
		return didQuotaUnit;
	}
	public void setDidQuotaUnit(String didQuotaUnit) {
		this.didQuotaUnit = didQuotaUnit;
	}
	public String getIntercomQuotaUnit() {
		return intercomQuotaUnit;
	}
	public void setIntercomQuotaUnit(String intercomQuotaUnit) {
		this.intercomQuotaUnit = intercomQuotaUnit;
	}
	public String getCreateByName() {
		return createByName;
	}
	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}
	public String getUpdateByName() {
		return updateByName;
	}
	public void setUpdateByName(String updateByName) {
		this.updateByName = updateByName;
	}
	public Boolean getSpeedDowngradeFlag() {
		return speedDowngradeFlag;
	}
	public void setSpeedDowngradeFlag(Boolean speedDowngradeFlag) {
		this.speedDowngradeFlag = speedDowngradeFlag;
	}
	public Boolean getIsFupApplied() {
		return isFupApplied;
	}
	public void setIsFupApplied(Boolean isFupApplied) {
		this.isFupApplied = isFupApplied;
	}
	public Timestamp getFupAppliedDate() {
		return fupAppliedDate;
	}
	public void setFupAppliedDate(Timestamp fupAppliedDate) {
		this.fupAppliedDate = fupAppliedDate;
	}
	public BigDecimal getCurrentSessionUsageTime() {
		return currentSessionUsageTime;
	}
	public void setCurrentSessionUsageTime(BigDecimal currentSessionUsageTime) {
		this.currentSessionUsageTime = currentSessionUsageTime;
	}
	public BigDecimal getCurrentSessionUsageVolume() {
		return currentSessionUsageVolume;
	}
	public void setCurrentSessionUsageVolume(BigDecimal currentSessionUsageVolume) {
		this.currentSessionUsageVolume = currentSessionUsageVolume;
	}
	public String getParentQuotaType() {
		return parentQuotaType;
	}
	public void setParentQuotaType(String parentQuotaType) {
		this.parentQuotaType = parentQuotaType;
	}
	public Boolean getIsChunkAvailable() {
		return isChunkAvailable;
	}
	public void setIsChunkAvailable(Boolean isChunkAvailable) {
		this.isChunkAvailable = isChunkAvailable;
	}
	public Double getReservedQuotaInPer() {
		return reservedQuotaInPer;
	}
	public void setReservedQuotaInPer(Double reservedQuotaInPer) {
		this.reservedQuotaInPer = reservedQuotaInPer;
	}
	public Double getTotalReservedQuota() {
		return totalReservedQuota;
	}
	public void setTotalReservedQuota(Double totalReservedQuota) {
		this.totalReservedQuota = totalReservedQuota;
	}
	public String getUsageQuotaType() {
		return usageQuotaType;
	}
	public void setUsageQuotaType(String usageQuotaType) {
		this.usageQuotaType = usageQuotaType;
	}
	public Boolean getSkipQuotaUpdate() {
		return skipQuotaUpdate;
	}
	public void setSkipQuotaUpdate(Boolean skipQuotaUpdate) {
		this.skipQuotaUpdate = skipQuotaUpdate;
	}
	public Timestamp getLastQuotaReset() {
		return lastQuotaReset;
	}
	public void setLastQuotaReset(Timestamp lastQuotaReset) {
		this.lastQuotaReset = lastQuotaReset;
	}
	public Boolean getIsQuotaUpdateSkipped() {
		return isQuotaUpdateSkipped;
	}
	public void setIsQuotaUpdateSkipped(Boolean isQuotaUpdateSkipped) {
		this.isQuotaUpdateSkipped = isQuotaUpdateSkipped;
	}
	@Override
	public String toString() {
		return "CustomerQuota [quotaDtlsId=" + quotaDtlsId + ", custId=" + custId + ", planId=" + planId
				+ ", quotaType=" + quotaType + ", totalQuota=" + totalQuota + ", usedQuota=" + usedQuota
				+ ", quotaUnit=" + quotaUnit + ", timeTotalQuota=" + timeTotalQuota + ", timeQuotaUsed=" + timeQuotaUsed
				+ ", timeQuotaUnit=" + timeQuotaUnit + ", createdByStaffId=" + createdByStaffId + ", createDate="
				+ createDate + ", lastModifiedByStaffId=" + lastModifiedByStaffId + ", lastModifiedDate="
				+ lastModifiedDate + ", isDeleted=" + isDeleted + ", totalQuotaKb=" + totalQuotaKb + ", usedQuotaKb="
				+ usedQuotaKb + ", timeUsedQuotaSec=" + timeUsedQuotaSec + ", timeTotalQuotaSec=" + timeTotalQuotaSec
				+ ", custPackageId=" + custPackageId + ", didTotalQuota=" + didTotalQuota + ", didUsedQuota="
				+ didUsedQuota + ", intercomTotalQuota=" + intercomTotalQuota + ", intercomUsedQuota="
				+ intercomUsedQuota + ", didQuotaUnit=" + didQuotaUnit + ", intercomQuotaUnit=" + intercomQuotaUnit
				+ ", createByName=" + createByName + ", updateByName=" + updateByName + ", speedDowngradeFlag="
				+ speedDowngradeFlag + ", isFupApplied=" + isFupApplied + ", fupAppliedDate=" + fupAppliedDate
				+ ", currentSessionUsageTime=" + currentSessionUsageTime + ", currentSessionUsageVolume="
				+ currentSessionUsageVolume + ", parentQuotaType=" + parentQuotaType + ", isChunkAvailable="
				+ isChunkAvailable + ", reservedQuotaInPer=" + reservedQuotaInPer + ", totalReservedQuota="
				+ totalReservedQuota + ", usageQuotaType=" + usageQuotaType + ", skipQuotaUpdate=" + skipQuotaUpdate
				+ ", lastQuotaReset=" + lastQuotaReset + ", isQuotaUpdateSkipped=" + isQuotaUpdateSkipped + "]";
	}

    
}
