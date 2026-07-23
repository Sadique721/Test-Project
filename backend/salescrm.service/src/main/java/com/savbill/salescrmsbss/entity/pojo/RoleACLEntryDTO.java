package com.savbill.salescrmsbss.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
