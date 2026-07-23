package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;
import com.savbill.cpm.pojo.api.CustomerAddressPojo;

import lombok.Data;

@Data
public class AddressUpdateDTO extends UpdateAbstarctDTO {
    private CustomerAddressPojo address;
    private String addressType;
    private String remarks;
    private Integer custId;
}
