package com.savbill.taskmanagement.core.modules.acl.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleACLEntryDTO {

    private Long id;

    private String code;

    private int menuid;

    public RoleACLEntryDTO(Long id, String code, int menuid) {
        this.id= id;
        this.code = code;
        this.menuid = menuid;
    }


}
