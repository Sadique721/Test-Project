package com.diameter.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

public class CustomerPackageRel {

    private BigInteger custPackageId;
    private BigInteger custId;
    private BigInteger planId;

    private Timestamp startDate;
    private Timestamp endDate;
    private Timestamp expiryDate;

    private String status;
    private String service;

    private BigInteger qosPolicyId;
    private String uploadQos;
    private String downloadQos;
    private String uploadTs;
    private String downloadTs;

    private BigDecimal createdByStaffId;
    private Timestamp createDate;
    private BigDecimal lastModifiedByStaffId;
    private Timestamp lastModifiedDate;

    private Boolean isDelete;

    private Double offerPrice;
    private Double taxAmount;

    private String createByName;
    private String updateByName;

    private BigInteger creditDocId;
    private BigInteger debitDocId;

    private Double walletBalUsed;

    private String purchaseType;
    private BigInteger onlinePurchaseId;
    private String purchaseFrom;

    private BigInteger graceDays;
    private String custPlanStatus;
    private BigInteger custServiceMappingId;

    private BigInteger notificationLevel;
    private Boolean isTriggerCoadm;
    private String onQuotaExhaustEventName;

    /* 👉 Generate Getters & Setters */

    public BigInteger getCustPackageId() {
        return custPackageId;
    }

    public void setCustPackageId(BigInteger custPackageId) {
        this.custPackageId = custPackageId;
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

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public BigInteger getQosPolicyId() {
        return qosPolicyId;
    }

    public void setQosPolicyId(BigInteger qosPolicyId) {
        this.qosPolicyId = qosPolicyId;
    }

    public String getUploadQos() {
        return uploadQos;
    }

    public void setUploadQos(String uploadQos) {
        this.uploadQos = uploadQos;
    }

    public String getDownloadQos() {
        return downloadQos;
    }

    public void setDownloadQos(String downloadQos) {
        this.downloadQos = downloadQos;
    }

    public String getUploadTs() {
        return uploadTs;
    }

    public void setUploadTs(String uploadTs) {
        this.uploadTs = uploadTs;
    }

    public String getDownloadTs() {
        return downloadTs;
    }

    public void setDownloadTs(String downloadTs) {
        this.downloadTs = downloadTs;
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

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(Double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
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

    public BigInteger getCreditDocId() {
        return creditDocId;
    }

    public void setCreditDocId(BigInteger creditDocId) {
        this.creditDocId = creditDocId;
    }

    public BigInteger getDebitDocId() {
        return debitDocId;
    }

    public void setDebitDocId(BigInteger debitDocId) {
        this.debitDocId = debitDocId;
    }

    public Double getWalletBalUsed() {
        return walletBalUsed;
    }

    public void setWalletBalUsed(Double walletBalUsed) {
        this.walletBalUsed = walletBalUsed;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public BigInteger getOnlinePurchaseId() {
        return onlinePurchaseId;
    }

    public void setOnlinePurchaseId(BigInteger onlinePurchaseId) {
        this.onlinePurchaseId = onlinePurchaseId;
    }

    public String getPurchaseFrom() {
        return purchaseFrom;
    }

    public void setPurchaseFrom(String purchaseFrom) {
        this.purchaseFrom = purchaseFrom;
    }

    public BigInteger getGraceDays() {
        return graceDays;
    }

    public void setGraceDays(BigInteger graceDays) {
        this.graceDays = graceDays;
    }

    public String getCustPlanStatus() {
        return custPlanStatus;
    }

    public void setCustPlanStatus(String custPlanStatus) {
        this.custPlanStatus = custPlanStatus;
    }

    public BigInteger getCustServiceMappingId() {
        return custServiceMappingId;
    }

    public void setCustServiceMappingId(BigInteger custServiceMappingId) {
        this.custServiceMappingId = custServiceMappingId;
    }

    public BigInteger getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(BigInteger notificationLevel) {
        this.notificationLevel = notificationLevel;
    }

    public Boolean getTriggerCoadm() {
        return isTriggerCoadm;
    }

    public void setTriggerCoadm(Boolean triggerCoadm) {
        isTriggerCoadm = triggerCoadm;
    }

    public String getOnQuotaExhaustEventName() {
        return onQuotaExhaustEventName;
    }

    public void setOnQuotaExhaustEventName(String onQuotaExhaustEventName) {
        this.onQuotaExhaustEventName = onQuotaExhaustEventName;
    }

    @Override
    public String toString() {
        return "CustomerPackageRel{" +
                "custPackageId=" + custPackageId +
                ", custId=" + custId +
                ", planId=" + planId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", expiryDate=" + expiryDate +
                ", status='" + status + '\'' +
                ", service='" + service + '\'' +
                ", qosPolicyId=" + qosPolicyId +
                ", uploadQos='" + uploadQos + '\'' +
                ", downloadQos='" + downloadQos + '\'' +
                ", uploadTs='" + uploadTs + '\'' +
                ", downloadTs='" + downloadTs + '\'' +
                ", createdByStaffId=" + createdByStaffId +
                ", createDate=" + createDate +
                ", lastModifiedByStaffId=" + lastModifiedByStaffId +
                ", lastModifiedDate=" + lastModifiedDate +
                ", isDelete=" + isDelete +
                ", offerPrice=" + offerPrice +
                ", taxAmount=" + taxAmount +
                ", createByName='" + createByName + '\'' +
                ", updateByName='" + updateByName + '\'' +
                ", creditDocId=" + creditDocId +
                ", debitDocId=" + debitDocId +
                ", walletBalUsed=" + walletBalUsed +
                ", purchaseType='" + purchaseType + '\'' +
                ", onlinePurchaseId=" + onlinePurchaseId +
                ", purchaseFrom='" + purchaseFrom + '\'' +
                ", graceDays=" + graceDays +
                ", custPlanStatus='" + custPlanStatus + '\'' +
                ", custServiceMappingId=" + custServiceMappingId +
                ", notificationLevel=" + notificationLevel +
                ", isTriggerCoadm=" + isTriggerCoadm +
                ", onQuotaExhaustEventName='" + onQuotaExhaustEventName + '\'' +
                '}';
    }
}
