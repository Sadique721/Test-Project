package com.savbill.integrationsystem.mvno;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PaymentDto {

    @NotBlank(message = "clientId cannot be null or empty")
    String clientId;
    @NotBlank(message = "invoiceNo cannot be null or empty")
    String invoiceNo;
    Double amount;

}
