package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import lombok.Data;

import java.util.List;

@Data
public class SaveDepartmentSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

    private Boolean isDelete = false;
    private Integer mvnoId;

    private List<Integer> planIds;
}
