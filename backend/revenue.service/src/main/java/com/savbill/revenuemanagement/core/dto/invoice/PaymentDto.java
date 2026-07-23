package com.savbill.revenuemanagement.core.dto.invoice;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PaymentDto {

    String clientId;
    String invoiceNo;
    Double amount;

}
