package com.savbill.notification.services;

import java.util.List;

import com.savbill.notification.entity.Role;
import com.savbill.notification.entity.RoleScreens;

public interface RoleService {

    void saveRole(String queueStaffSuccess, Role role, List<RoleScreens> roleScreenList, boolean isUpdate, boolean isDelete,String oldName);
}
