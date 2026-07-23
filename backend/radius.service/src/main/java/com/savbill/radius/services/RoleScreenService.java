package com.savbill.radius.services;

import com.savbill.radius.entity.RoleScreens;

import java.util.List;

public interface RoleScreenService {
    List<RoleScreens> getScreensByRole(Long id, Integer mvnoId);
    List<RoleScreens> getScreensByRoles(List<Long> ids, Long mvnoId);
}
