package com.savbill.inventorymanagement.rabbitmq;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CAFCustomerStatusMessage {
    private Integer customerId;
    private String Status;
}
