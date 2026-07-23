package com.savbill.commonGateway.MicroSeviceDataShare.SharedMessages;


import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
//    private List<CustomACLEntry> aclEntry;
    private List<RoleACLEntry> aclEntry;
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
