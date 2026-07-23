package com.savbill.radius.kafka.message;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class MacAddressMappingMessage {

    private List<HashMap<String, Object>> macAddress;
    private boolean isUpdate;
    private boolean isDelete;

    public MacAddressMappingMessage(List<HashMap<String, Object>> macAddress, boolean isUpdate, boolean isDelete) {
        this.macAddress = macAddress;
        this.isUpdate = isUpdate;
        this.isDelete = isDelete;
    }
}
