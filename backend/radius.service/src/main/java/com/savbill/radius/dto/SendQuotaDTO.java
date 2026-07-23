package com.savbill.radius.dto;

import lombok.Data;

@Data
public class SendQuotaDTO {

    private Integer planId;

    private Integer custId;

    private Double percentage;

    private Double totalQuota;

    private Double usedQuota;

    private Integer cprId;

    private Double currentSessionUsageVolume;

    private Double currentSessionUsageTime;

    private boolean isChunkAvailable;

    private double totalReservedQuota;
}
