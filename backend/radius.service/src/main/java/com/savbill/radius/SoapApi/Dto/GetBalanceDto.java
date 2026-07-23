package com.savbill.radius.SoapApi.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetBalanceDto {

    private Integer id;
    private Long planId;
    private Double totalQuota;
    private Long totalQuotaLong;
    private Double usedQuota;
    private Long usedQuotaLong;
    private Double currentSessionUsageVolume;
    private Long currentSessionUsageVolumeLong;
    private String usageQuotaType;
    private String service;
    private String uploadQuota;
    private String downloadQuota;
    private String quotaUnit;
    private String planName;
    private String planType;
    private Double currentSessionUsageTime;
    private Long currentSessionUsageTimeLong;
    private Long cprId;

    public GetBalanceDto() {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
        this.currentSessionUsageVolume = currentSessionUsageVolume;
    }

    public GetBalanceDto(int id,Long planId, double totalQuota, double usedQuota , double currentSessionUsageVolume) {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
        this.currentSessionUsageVolume = currentSessionUsageVolume;
    }

    public GetBalanceDto(int id,Long planId, double totalQuota, double usedQuota , double currentSessionUsageVolume, String usageQuotaType, String service, String quotaUnit) {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
        this.currentSessionUsageVolume = currentSessionUsageVolume;
        this.usageQuotaType = usageQuotaType;
        this.service = service;
        this.quotaUnit = quotaUnit;
    }

//    public GetBalanceDto(int id,Long planId, double totalQuota, double usedQuota , double currentSessionUsageVolume, String usageQuotaType, String service, String quotaUnit, String planName, String planType, double currentSessionUsageTime) {
//        this.planId = planId;
//        this.id = id;
//        this.totalQuota = totalQuota;
//        this.usedQuota = usedQuota;
//        this.currentSessionUsageVolume = currentSessionUsageVolume;
//        this.usageQuotaType = usageQuotaType;
//        this.service = service;
//        this.quotaUnit = quotaUnit;
//        this.planName = planName;
//        this.planType = planType;
//        this.currentSessionUsageTime = currentSessionUsageTime;
//    }

    public GetBalanceDto(int id,Long planId, Double totalQuota, Double usedQuota , Double currentSessionUsageVolume, String usageQuotaType, String service, String quotaUnit, String planName, String planType, Double currentSessionUsageTime) {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.totalQuotaLong = totalQuota.longValue();
        this.usedQuota = usedQuota;
        this.usedQuotaLong = usedQuota.longValue();
        this.currentSessionUsageVolume = currentSessionUsageVolume;
        this.currentSessionUsageVolumeLong = currentSessionUsageVolume.longValue();
        this.usageQuotaType = usageQuotaType;
        this.service = service;
        this.quotaUnit = quotaUnit;
        this.planName = planName;
        this.planType = planType;
        this.currentSessionUsageTime = currentSessionUsageTime;
        this.currentSessionUsageTimeLong = currentSessionUsageTime.longValue();
    }

    public GetBalanceDto(int id,Long planId, Double totalQuota, Double usedQuota , Double currentSessionUsageVolume, String usageQuotaType, String service, String quotaUnit, String planName, String planType, Double currentSessionUsageTime, Long cprId) {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.totalQuotaLong = totalQuota.longValue();
        this.usedQuota = usedQuota;
        this.usedQuotaLong = usedQuota.longValue();
        this.currentSessionUsageVolume = currentSessionUsageVolume;
        this.currentSessionUsageVolumeLong = currentSessionUsageVolume.longValue();
        this.usageQuotaType = usageQuotaType;
        this.service = service;
        this.quotaUnit = quotaUnit;
        this.planName = planName;
        this.planType = planType;
        this.currentSessionUsageTime = currentSessionUsageTime;
        this.currentSessionUsageTimeLong = currentSessionUsageTime.longValue();
        this.cprId = cprId;
    }
}
