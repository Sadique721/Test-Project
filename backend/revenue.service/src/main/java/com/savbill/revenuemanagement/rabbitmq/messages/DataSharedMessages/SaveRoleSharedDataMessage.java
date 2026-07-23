package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;

import com.savbill.revenuemanagement.core.acl.domain.CustomACLEntry;
import lombok.Data;

import java.util.List;

@Data
public class SaveRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
    private List<CustomACLEntry> aclEntry;
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
