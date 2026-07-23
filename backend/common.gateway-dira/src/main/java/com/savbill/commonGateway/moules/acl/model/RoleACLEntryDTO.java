package com.savbill.commonGateway.moules.acl.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleACLEntryDTO {

    private Long id;

    private Integer roleId;
    private String code;
    private String product;
    private int menuid;

    public RoleACLEntryDTO(Long id, String code, int menuid,Integer roleId, String product) {
        this.roleId = roleId;
        this.id= id;
        this.code = code;
        this.menuid = menuid;
        this.product = product;
    }

    public RoleACLEntryDTO(Long id, String code, int menuid) {
        this.id= id;
        this.code = code;
        this.menuid = menuid;
    }


}
