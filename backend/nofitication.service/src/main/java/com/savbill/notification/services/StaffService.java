package com.savbill.notification.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.savbill.notification.entity.RoleScreens;
import com.savbill.notification.entity.Staff;

public interface StaffService extends UserDetailsService{

    void saveRoleAndStaff(String queueStaffSuccess, Staff staff, List<RoleScreens> roleScreenList, boolean update,
	    boolean delete,String oldName);
}
