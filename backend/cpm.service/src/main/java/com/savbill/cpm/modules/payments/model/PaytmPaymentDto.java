package com.savbill.cpm.modules.payments.model;

import lombok.Data;

@Data
public class PaytmPaymentDto {
    Integer custId;
    Long mobileNo;
    Long orderId;
    Integer txnAmount;
    String customerName;
    String customerEmail;

    String isFromCaptive;
}
