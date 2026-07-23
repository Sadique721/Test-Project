package com.savbill.notification.rabbitmq.message;

import java.util.List;

import com.savbill.notification.entity.RoleScreens;
import com.savbill.notification.entity.Staff;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaffMessage {
	private Staff staff;
	private List<RoleScreens> roleScreenList;
	private boolean isUpdate;
	private boolean isDelete;
	private String actualName;
	private String traceId;
	private String spanId;
	
	public StaffMessage(Staff staff, List<RoleScreens> roleScreenList,boolean isUpdate,boolean isDelete,String actualName, String traceId, String spanId)
	{
		this.staff = staff;
		this.roleScreenList = roleScreenList;
		this.isUpdate = isUpdate;
		this.isDelete = isDelete;
		this.actualName = actualName;
		this.traceId = traceId;
		this.spanId = spanId;
	}

}
