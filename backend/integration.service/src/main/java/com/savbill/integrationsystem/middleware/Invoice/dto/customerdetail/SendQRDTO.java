package com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail;

import lombok.Data;

@Data
public class SendQRDTO {

    private String qr;

    private Integer debitdocId;
}
