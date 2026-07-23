package com.savbill.radius.kafka.message;

import lombok.Data;

@Data
public class UpdateBusinessUnitSharedDataMessage {
    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private String planBindingType;
    private Boolean isDeleted;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
