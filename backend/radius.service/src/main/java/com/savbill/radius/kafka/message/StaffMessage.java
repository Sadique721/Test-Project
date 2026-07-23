package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.RoleScreens;
import com.savbill.radius.entity.Staff;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StaffMessage {
	private Staff staff;
	private List<RoleScreens> roleScreenList;
	private boolean isUpdate;
	private boolean isDelete;
	private String actualName;

	public StaffMessage(Staff staff, List<RoleScreens> roleScreenList,boolean isUpdate,boolean isDelete,String actualName) {
		this.staff = staff;
		this.roleScreenList = roleScreenList;
		this.isUpdate = isUpdate;
		this.isDelete = isDelete;
		this.actualName = actualName;
	}
}
