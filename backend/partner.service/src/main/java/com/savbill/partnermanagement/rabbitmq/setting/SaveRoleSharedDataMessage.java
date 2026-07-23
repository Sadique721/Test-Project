package com.savbill.partnermanagement.rabbitmq.setting;

import com.savbill.partnermanagement.modules.acl.domain.CustomACLEntry;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
    private List<CustomACLEntry> aclEntry = new ArrayList<>();
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;private Integer createdById;
    private Integer lastModifiedById;

}
