package com.savbill.commonGateway.moules.MasterManagement.Pincode.model;

import lombok.Data;

@Data
public class PincodeMvnoDto {
    Long pincodeid;
    Integer mvnoId;

    public PincodeMvnoDto(Long pincodeid, Integer mvnoId) {
        this.pincodeid = pincodeid;
        this.mvnoId = mvnoId;
    }
}
