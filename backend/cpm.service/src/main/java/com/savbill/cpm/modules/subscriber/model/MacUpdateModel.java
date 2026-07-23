package com.savbill.cpm.modules.subscriber.model;

import lombok.Data;

import java.util.List;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class MacUpdateModel extends UpdateAbstarctDTO {
    private Boolean macTelFlag = false;
    private List<MacUpdateDetailsModel> macAddresses;
    private Integer custId;
    private String remarks;
}
