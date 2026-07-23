package com.savbill.partnermanagement.rabbitmq.setting;

import com.savbill.partnermanagement.modules.acl.domain.CustomACLEntry;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateRoleSharedDataMessage {
    private Long id;
    private String rolename;
    private String status;
    private Boolean sysRole = false;
//    private LocalDateTime createdate;
//    private LocalDateTime updatedate;
    private List<CustomACLEntry> aclEntry = new ArrayList<>();
    private Boolean isDelete;
    private Integer mvnoId;
    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
}
