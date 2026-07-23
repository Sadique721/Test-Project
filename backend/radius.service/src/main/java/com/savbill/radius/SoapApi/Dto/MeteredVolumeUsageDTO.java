package com.savbill.radius.SoapApi.Dto;

public class MeteredVolumeUsageDTO {
    private Integer id;
    private Long planId;
    private Double totalQuota;
    private Double usedQuota;
    private Double currentSessionUsageVolume;

    public MeteredVolumeUsageDTO(int id,Long planId, double totalQuota, double usedQuota , double currentSessionUsageVolume) {
        this.planId = planId;
        this.id = id;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
        this.currentSessionUsageVolume = currentSessionUsageVolume;
    }
}
