package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages;

import lombok.Data;

@Data
public class SaveBankSharedDataMessage {

    private Long id;
    private String bankname;
    private String accountnum;
    private String ifsccode;
    private String bankholdername;
    private String status;
    private Boolean isDeleted = false;
    private String bankcode;
    private Integer mvnoId;
    private String banktype;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
