package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NetworkDeviceBindDTO extends Auditable implements IBaseDto {

    private Long id;
    private String deviceName;
    private String parentDeviceName;
    private Long currentDeviceId;
    private String currentDevicePort;
    private String portType;
    private Long otherDeviceId;
    private String otherDevicePort;
    private String currentDevice;
    private String otherDevice;
    private String currentDevicePortNumber;
    private String otherDevicePortNumber;
    private String currentDeviceType;
    private String otherDeviceType;
    @JsonIgnore
    private int mappingId;
    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
