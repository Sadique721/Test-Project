package com.savbill.cpm.modules.subscriber.model;

import com.savbill.cpm.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class ContactDetailsDTO extends UpdateAbstarctDTO {
    private String mobile;
    private String altMobile;
    private String email;
    private String altEmail;
    private String phone;
    private String altPhone;
    private String fax;
    private String remarks;
    private Integer custId;
}
