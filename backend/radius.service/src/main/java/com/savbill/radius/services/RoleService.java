package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.RoleScreens;

public interface RoleService {

    void saveRole(String queueStaffSuccess, Role role, List<RoleScreens> roleScreenList, boolean isUpdate, boolean isDelete,String oldName);
    Role findByName(String name, Long mvnoId);
}
