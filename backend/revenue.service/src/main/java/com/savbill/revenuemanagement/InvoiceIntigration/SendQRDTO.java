package com.savbill.revenuemanagement.InvoiceIntigration;

import lombok.Data;

@Data
public class SendQRDTO {

    private String qr;

    private Integer debitdocId;
}
