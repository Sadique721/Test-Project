package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaftoCustomerMessage {

    private Integer customerId;

    private Integer loggedInUser;

    private String status;

    private String cafApproveStatus;
}
