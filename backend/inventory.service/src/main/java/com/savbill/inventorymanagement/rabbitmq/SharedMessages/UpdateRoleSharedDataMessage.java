package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.acl.domain.RoleACLEntry;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
//    private LocalDateTime createdate;
//    private LocalDateTime updatedate;
//    private List<CustomACLEntry> aclEntry = new ArrayList<>();
    private List<RoleACLEntry> aclEntry;
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
