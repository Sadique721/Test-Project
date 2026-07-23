package com.savbill.taskmanagement.core.modules.role.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleACLEntryDTO {

    private Long id;

    private Integer roleId;
    private String code;

    private int menuid;

    public RoleACLEntryDTO(Long id, String code, int menuid, Integer roleId) {
        this.roleId = roleId;
        this.id= id;
        this.code = code;
        this.menuid = menuid;
    }


}
