package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.Date;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class ChangeIPExpiryDTO extends UpdateAbstarctDTO {
    private Integer currentChargeId;
    private Date revisedExpiryDate;
}
