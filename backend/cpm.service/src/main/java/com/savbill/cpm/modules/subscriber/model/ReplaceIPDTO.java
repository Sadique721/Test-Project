package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class ReplaceIPDTO extends UpdateAbstarctDTO {
    private Long currentPoolDetailsId;
    private Long currentAllocatedId;
    private Integer currentChargeId;
    private Long newPoolDetailsId;
    private String remarks;
}
