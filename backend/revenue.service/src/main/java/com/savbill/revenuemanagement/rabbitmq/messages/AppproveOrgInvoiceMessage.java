package com.savbill.revenuemanagement.rabbitmq.messages;

import lombok.Data;

@Data
public class AppproveOrgInvoiceMessage {
    private Integer debitdocId;
    private Boolean isApproveRequest;
}
