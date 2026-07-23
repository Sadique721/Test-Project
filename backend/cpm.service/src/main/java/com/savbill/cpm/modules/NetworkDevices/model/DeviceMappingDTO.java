package com.savbill.cpm.modules.NetworkDevices.model;

import lombok.Data;

import java.util.Set;

@Data
public class DeviceMappingDTO {

    private Long deviceId;
    private Set<Long> inPortDevices;
    private Set<Long> outPortDevices;

}
