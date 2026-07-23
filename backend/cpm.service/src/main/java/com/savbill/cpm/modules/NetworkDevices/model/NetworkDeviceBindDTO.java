package com.savbill.cpm.modules.NetworkDevices.model;

import com.savbill.cpm.core.dto.IBaseDto;
import com.savbill.cpm.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NetworkDeviceBindDTO extends Auditable implements IBaseDto {

    @JsonIgnore
    private Long id;
    private String deviceName;
    private String parentDeviceName;
    private Long currentDeviceId;
    private String currentDevicePort;
    private String portType;

    private Long otherDeviceId;
    private String otherDevicePort;
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
}
