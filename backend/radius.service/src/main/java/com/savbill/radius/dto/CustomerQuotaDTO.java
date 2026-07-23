package com.savbill.radius.dto;

import lombok.Data;

@Data
public class CustomerQuotaDTO {

    private Integer customerPackageId;

    private Double totalQuota;

    private Double timeTotalQuota;

    private Double currentSessionVolumeUsage;

    private Double getCurrentSessionTimeUsage;

    private Double totalUsedTime;

    private Double totalUsedQuota;


}
