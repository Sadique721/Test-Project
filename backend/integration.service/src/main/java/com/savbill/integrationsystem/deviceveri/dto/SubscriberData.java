package com.savbill.integrationsystem.deviceveri.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberData {

    @JsonProperty("subscriber_code")
    public String subscriberCode;
    
    @JsonProperty("subscriber_name")
    public String subscriberName;
    
    @JsonProperty("phone_number")
    public String phoneNumber;
    
    @JsonProperty("device_count")
    public Integer deviceCount;
    
    @JsonProperty("package")
    public Package packageName;
    
    @JsonProperty("devices")
    public List<Device> devices;
}
