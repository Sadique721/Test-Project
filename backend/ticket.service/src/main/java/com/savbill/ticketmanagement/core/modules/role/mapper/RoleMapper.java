package com.savbill.ticketmanagement.core.modules.role.mapper;

import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.role.domain.Role;
import com.savbill.ticketmanagement.core.modules.role.model.RoleDTO;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper extends IBaseMapper<RoleDTO, Role> {
}
