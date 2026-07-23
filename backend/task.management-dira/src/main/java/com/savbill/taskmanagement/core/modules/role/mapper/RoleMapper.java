package com.savbill.taskmanagement.core.modules.role.mapper;

import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.role.domain.Role;
import com.savbill.taskmanagement.core.modules.role.model.RoleDTO;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper extends IBaseMapper<RoleDTO, Role> {
}
