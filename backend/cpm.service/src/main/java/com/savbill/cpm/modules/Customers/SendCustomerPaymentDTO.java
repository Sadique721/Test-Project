package com.savbill.cpm.modules.Customers;

import lombok.Data;

@Data
public class SendCustomerPaymentDTO {

    private Integer custId;

    private Double amount;
}
