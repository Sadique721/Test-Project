package com.savbill.inventorymanagement.rabbitmq;

import lombok.Data;

@Data
public class RecordPaymentMessage {
    private Double amount;
    private String remark;
    private Long customerMappingId;
    private String paymode;
    private String paytype;
    private String type;
    private Integer customerid;
    private Long serviceId;

    private Boolean isCaf;

    private Boolean isSameDay;
}
