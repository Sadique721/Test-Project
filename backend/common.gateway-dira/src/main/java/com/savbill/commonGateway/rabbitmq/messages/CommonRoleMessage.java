package com.savbill.commonGateway.rabbitmq.messages;

import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleDTO;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CommonRoleMessage {

    private Long id;

    private String rolename;

    private String status;


    private Boolean sysRole ;


    private List<RoleACLEntryDTO> aclMenus = new ArrayList();

    private Integer mvnoId;

    private Integer lcoId;

    private Boolean isDelete ;

    private String createdate;


    private String updatedate;


    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;

    private Integer lastModifiedById;


    public CommonRoleMessage(RoleDTO roleDTO){
        this.id = roleDTO.getId();
        this.rolename = roleDTO.getRolename();
        this.status = roleDTO.getStatus();
        this.sysRole = roleDTO.getSysRole();
        this.mvnoId = roleDTO.getMvnoId();
        this.lcoId = roleDTO.getLcoId();
        this.isDelete = roleDTO.getIsDelete();
        if (roleDTO.getCreatedate()!=null)
            this.createdate = null;
        this.updatedate = null;
        this.createdByName = roleDTO.getCreatedByName();
        this.lastModifiedByName = roleDTO.getLastModifiedByName();
        this.createdById = roleDTO.getCreatedById();
        this.lastModifiedById = roleDTO.getLastModifiedById();
    }


}
