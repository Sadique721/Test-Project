package com.savbill.integrationsystem.SOAPService.meteredVolumeUsageForSubAcctName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeteredVolumeUsageForSubsAccNameDTO {
    private Double aggregateBytesLimit;
    private Long planId;

    private String planName;
    private Double aggregateBytesRemaining;

    private Double aggregateBytesUsed;

    private Double inBytesLimit;

    private Double inBytesRemaining;

    private Double inBytesUsed;

    private Double outBytesLimit;

    private Double outBytesRemaining;

    private Double outBytesUsed;
    private String quotaUnit;
    private Double downloadOctate;
    private Double uploadOctate;



    public MeteredVolumeUsageForSubsAccNameDTO(Double aggregateBytesLimit, Long planId, String planName, Double aggregateBytesRemaining, Double aggregateBytesUsed, Double inBytesLimit, Double inBytesRemaining, Double inBytesUsed, Double outBytesLimit, Double outBytesRemaining, Double outBytesUsed, String quotaUnit) {
        this.planName=planName;
        this.planId = planId;
        this.aggregateBytesLimit = aggregateBytesLimit;
        this.aggregateBytesRemaining = aggregateBytesRemaining;
        this.aggregateBytesUsed = aggregateBytesUsed;
        this.inBytesLimit = inBytesLimit;
        this.inBytesRemaining = inBytesRemaining;
        this.inBytesUsed = inBytesUsed;
        this.outBytesLimit = outBytesLimit;
        this.outBytesRemaining = outBytesRemaining;
        this.outBytesUsed = outBytesUsed;
        this.quotaUnit=quotaUnit;
    }
}
