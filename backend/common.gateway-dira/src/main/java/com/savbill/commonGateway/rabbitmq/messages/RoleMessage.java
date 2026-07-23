package com.savbill.commonGateway.rabbitmq.messages;


import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMessage {

	private Long id;

	private String rolename;
    
    private String status;

	private Boolean sysRole = false;
	
//	private List<CustomACLEntryDTO> aclEntryDTOList = new ArrayList<>();

	private List<RoleACLEntryDTO> aclEntryDTOList = new ArrayList<>();
	
    private Integer mvnoId;


	private String createdate;

	private String updatedate;
    
    private Boolean isDelete = false;

	private String product;
    
    public RoleMessage(Role role) {
    	this.id = role.getId();
    	this.rolename = role.getRolename();
    	this.status = role.getStatus();
    	this.sysRole = role.getSysRole();
    	this.mvnoId = role.getMvnoId();
    	this.isDelete = role.getIsDelete();
		this.createdate = role.getCreatedate().toString();
		this.updatedate = role.getUpdatedate().toString();
		this.product = role.getProduct();
    	if(role.getRoleAclEntry() != null && role.getRoleAclEntry().size() > 0) {
    		List<RoleACLEntryDTO> customACLEntryDTOList = new ArrayList<RoleACLEntryDTO>();
    		for (RoleACLEntry customACLEntry : role.getRoleAclEntry()) {
    			customACLEntryDTOList.add(new RoleACLEntryDTO(customACLEntry.getId(), customACLEntry.getCode(), customACLEntry.getMenuid(),customACLEntry.getRole().getId().intValue(),customACLEntry.getProduct()));
			}
    		this.aclEntryDTOList = customACLEntryDTOList;
       	}
    }
    
}
