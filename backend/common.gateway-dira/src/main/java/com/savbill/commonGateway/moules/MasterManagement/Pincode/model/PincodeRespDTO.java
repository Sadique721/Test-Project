package com.savbill.commonGateway.moules.MasterManagement.Pincode.model;

import lombok.Data;

@Data
public class PincodeRespDTO {
    private Long pincodeid;
    private String pincode;
    private String status;

    public PincodeRespDTO(Long pincodeid, String pincode, String status) {
        this.pincodeid = pincodeid;
        this.pincode = pincode;
        this.status = status;
    }
}
