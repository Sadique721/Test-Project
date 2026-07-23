package com.diameter.model;

import java.math.BigInteger;

public class QOSPolicyGatewayMapping {

    private BigInteger id;
    private String name;

    private String downloadSpeed;
    private String uploadSpeed;

    private String baseDownloadSpeed;
    private String baseUploadSpeed;

    private String throttleDownloadSpeed;
    private String throttleUploadSpeed;

    private BigInteger qosPolicyId;

    // 👉 Generate Getters and Setters

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public String getUploadSpeed() {
        return uploadSpeed;
    }

    public void setUploadSpeed(String uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    public String getBaseDownloadSpeed() {
        return baseDownloadSpeed;
    }

    public void setBaseDownloadSpeed(String baseDownloadSpeed) {
        this.baseDownloadSpeed = baseDownloadSpeed;
    }

    public String getBaseUploadSpeed() {
        return baseUploadSpeed;
    }

    public void setBaseUploadSpeed(String baseUploadSpeed) {
        this.baseUploadSpeed = baseUploadSpeed;
    }

    public String getThrottleDownloadSpeed() {
        return throttleDownloadSpeed;
    }

    public void setThrottleDownloadSpeed(String throttleDownloadSpeed) {
        this.throttleDownloadSpeed = throttleDownloadSpeed;
    }

    public String getThrottleUploadSpeed() {
        return throttleUploadSpeed;
    }

    public void setThrottleUploadSpeed(String throttleUploadSpeed) {
        this.throttleUploadSpeed = throttleUploadSpeed;
    }

    public BigInteger getQosPolicyId() {
        return qosPolicyId;
    }

    public void setQosPolicyId(BigInteger qosPolicyId) {
        this.qosPolicyId = qosPolicyId;
    }

    @Override
    public String toString() {
        return "QOSPolicyGatewayMapping{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", downloadSpeed='" + downloadSpeed + '\'' +
                ", uploadSpeed='" + uploadSpeed + '\'' +
                ", baseDownloadSpeed='" + baseDownloadSpeed + '\'' +
                ", baseUploadSpeed='" + baseUploadSpeed + '\'' +
                ", throttleDownloadSpeed='" + throttleDownloadSpeed + '\'' +
                ", throttleUploadSpeed='" + throttleUploadSpeed + '\'' +
                ", qosPolicyId=" + qosPolicyId +
                '}';
    }
}