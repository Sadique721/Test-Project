package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;



import com.savbill.ticketmanagement.core.modules.acl.domain.CustomACLEntry;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleSharedDataMessage {
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
    private String createdByName;
    private String lastModifiedByName;
}
