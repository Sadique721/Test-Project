package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.savbill.commonGateway.moules.acl.model.AclMenuStructureDTO;
import com.savbill.commonGateway.moules.acl.model.CustomACLEntryDTO;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class RoleDTO extends Auditable implements IBaseDto {


    private Long id;

    @NotNull
    private String rolename;

    @NotNull
    private String status;

    @NotNull
    private Boolean sysRole = false;

    private Set<Integer> staffuserIds;

    private List<CustomACLEntryDTO> aclEntryPojoList = new ArrayList<>();

    private List<RoleACLEntryDTO> aclMenu = new ArrayList<>();

    private List<AclMenuStructureDTO> aclMenus = new ArrayList();

    private Integer mvnoId;

    private Integer lcoId;

    private String product;

    private String assignableRoleName;

    private Long assignableRoleId;

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    private Boolean isDelete = false;

//	private Integer lcoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }
}

