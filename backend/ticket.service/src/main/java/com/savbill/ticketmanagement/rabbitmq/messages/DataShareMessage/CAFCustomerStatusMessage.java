package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CAFCustomerStatusMessage {

    private Integer customerId;
    private String Status;
}
