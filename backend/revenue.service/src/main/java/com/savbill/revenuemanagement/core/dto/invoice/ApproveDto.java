package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.Data;

@Data
public class ApproveDto {
    Integer custId;
    Integer creditDocID;

    public ApproveDto(Integer customerid, Integer id) {
        this.custId = customerid;
        this.creditDocID = id;
    }
}
