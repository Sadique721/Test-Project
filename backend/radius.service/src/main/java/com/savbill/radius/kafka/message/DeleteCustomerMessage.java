package com.savbill.radius.kafka.message;

import lombok.Data;

@Data
public class DeleteCustomerMessage {
    private Integer custid;
    private Integer mvnoId;
    private String username;
    private String gatewayIpBind;
}
