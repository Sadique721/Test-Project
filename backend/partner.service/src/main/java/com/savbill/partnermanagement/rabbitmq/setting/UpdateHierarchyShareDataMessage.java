package com.savbill.partnermanagement.rabbitmq.setting;

import lombok.Data;

@Data
public class UpdateHierarchyShareDataMessage {
    private Long id;

    private Integer mvnoId;

    private Boolean isDeleted;

    private Long buId;

    private String hierarchyName;

    private String eventName;

    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
