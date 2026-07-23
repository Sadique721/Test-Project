package com.savbill.revenuemanagement.core.dto.customer;

import lombok.Data;

@Data
public class CustomerVoucherDTO {

    private Integer custId;
    private Integer volletId;
    private Double voucherAmount;
    private Integer partnerId;
}
