package com.savbill.radius.ippool.model;

import lombok.Data;

@Data
public class IPPoolAllocationDtlsDTO {

    private String ipAddress;
    private String status;
    private String blockBySessionId;
    private String nasIpAddress;
    private String userName;

    public IPPoolAllocationDtlsDTO(String ipAddress, String status, String blockBySessionId, String nasIpAddress, String userName) {
        this.ipAddress = ipAddress;
        this.status = status;
        this.blockBySessionId = blockBySessionId;
        this.nasIpAddress = nasIpAddress;
        this.userName = userName;
    }

}
