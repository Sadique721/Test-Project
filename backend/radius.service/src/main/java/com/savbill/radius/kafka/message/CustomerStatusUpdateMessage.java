package com.savbill.radius.kafka.message;

import lombok.Data;

@Data
public class CustomerStatusUpdateMessage {
    Integer custId;
    String status;
    String remark;

}
