package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.RoleScreens;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleMessage {
	private Role role;
	private List<RoleScreens> roleScreenList;
	private boolean isUpdate;
	private boolean isDelete;
	private String roleName;
    
    public RoleMessage(Role role, List<RoleScreens> roleScreenList,boolean isUpdate, boolean isDelete,String roleName) {
		this.role = role;
		this.roleScreenList = roleScreenList;
		this.isUpdate = isUpdate;
		this.isDelete=isDelete;
		this.roleName=roleName;
	}
}
