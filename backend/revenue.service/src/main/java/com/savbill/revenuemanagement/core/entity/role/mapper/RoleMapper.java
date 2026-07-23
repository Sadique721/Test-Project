package com.savbill.revenuemanagement.core.entity.role.mapper;

import com.savbill.revenuemanagement.core.entity.role.domain.Role;
import com.savbill.revenuemanagement.core.entity.role.model.RoleDTO;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;

import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper extends IBaseMapper<RoleDTO, Role> {
}
