package com.savbill.radius.services;

import java.util.List;
import java.util.Optional;

import com.savbill.radius.entity.RoleScreens;
import com.savbill.radius.entity.Staff;

public interface StaffService {

    void saveRoleAndStaff(String queueStaffSuccess, Staff staff, List<RoleScreens> roleScreenList, boolean update,
	    boolean delete,String oldName);

    Optional<Staff> findByUserName(String userName, Long mvnoId);
}
