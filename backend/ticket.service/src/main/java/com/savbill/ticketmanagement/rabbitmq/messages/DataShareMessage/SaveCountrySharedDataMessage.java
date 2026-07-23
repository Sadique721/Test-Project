package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;

import lombok.Data;

@Data
public class SaveCountrySharedDataMessage {
    private Integer id;
    private String name;
    private String status;
    private Boolean isDelete;
    private Integer mvnoId;
}
