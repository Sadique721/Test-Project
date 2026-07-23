package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;

import com.savbill.revenuemanagement.core.dto.common.Auditable;

import lombok.Data;

@Data
public class CountrySharedDataMessage extends Auditable {
    private Integer id;
    private String name;
    private String status;
    private Boolean isDelete;
    private Integer mvnoId;
}
