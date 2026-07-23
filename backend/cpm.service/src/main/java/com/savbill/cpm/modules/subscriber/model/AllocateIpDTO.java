package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class AllocateIpDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private Integer chargeId;
    private Long ipPoolDtlsId;
    private String remarks;
}
