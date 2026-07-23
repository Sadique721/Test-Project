package com.savbill.commonGateway.rabbitmq.messages;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CAFCustomerStatusMessage {
    private Integer customerId;
    private String Status;
}
