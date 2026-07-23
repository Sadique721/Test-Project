package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class ChangeQosRequestDTO extends UpdateAbstarctDTO {

    private Integer planId;
    private Integer custPackRelId;
    private Long qosPolicyId;
    private String remark;
    private Integer custId;
}
