package com.savbill.radius.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerQuotaInfo {
    private String userName;
    private Long mvnoId;
    private Double timeBasedTotalQuota;
    private Double timeBasedUsedQuota;
    private Double timeBasedSessionUsedQuota;
    private Double timeBasedUnusedQuota;
    private Double volumeBasedTotalQuota;
    private Double volumeBasedUsedQuota;
    private Double volumeBasedSessionUsedQuota;
    private Double volumeBasedUnusedQuota;
    private String planName;
    private String planType;
    private String messageId;
    private String message;
    private Date messageDate;
    private Integer custpackageid;
    private Integer custId;
    private Integer planId;
    private String quotaType;
    private Boolean skipQuotaReset;
    private String quotaUnit;

    public CustomerQuotaInfo() {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's used data updates";
    }
}
