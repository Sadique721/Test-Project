package com.savbill.integrationsystem.deviceveri.dto.devicedetails;

import java.util.List;

import com.savbill.integrationsystem.deviceveri.dto.Device;
import com.savbill.integrationsystem.deviceveri.dto.SubscriberData;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetailsResponse {

    @JsonProperty("device_id")
    private String deviceId;
    
    @JsonProperty("master")
    private Boolean master;
    
    @JsonProperty("package")
    private String packageName;
    
    @JsonProperty("active_from")
    private String activeFrom;
    
    @JsonProperty("expiry_on")
    private String expiryOn;
    
    @JsonProperty("last_payment")
    private Double lastPayment;
    
    @JsonProperty("last_paid")
    private String lastPaid;
    
    @JsonProperty("subscriber")
    private SubscriberData subscriber;
    
    @JsonProperty("devices")
    private List<Device> devices;

}
