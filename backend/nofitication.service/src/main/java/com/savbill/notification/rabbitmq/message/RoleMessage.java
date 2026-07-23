package com.savbill.notification.rabbitmq.message;

import java.util.List;

import com.savbill.notification.entity.Role;
import com.savbill.notification.entity.RoleScreens;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleMessage {
    private Role role;
    private List<RoleScreens> roleScreenList;
    private boolean isUpdate;
    private boolean isDelete;
    private String roleName;
	private String traceId;
	private String spanId;
    
    public RoleMessage(Role role, List<RoleScreens> roleScreenList,boolean isUpdate, boolean isDelete,String roleName, String traceId, String spanId)
	{
		this.role = role;
		this.roleScreenList = roleScreenList;
		this.isUpdate = isUpdate;
		this.isDelete=isDelete;
		this.roleName=roleName;
		this.traceId=traceId;
		this.spanId=spanId;
	}
}
