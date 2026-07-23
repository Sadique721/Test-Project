package com.savbill.taskmanagement.core.modules.acl.mapper;

import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.acl.domain.AclMenu;
import com.savbill.taskmanagement.core.modules.acl.model.AclMenuDtoNew;
import org.mapstruct.Mapper;

@Mapper
public interface AclMenuMapper extends IBaseMapper<AclMenuDtoNew, AclMenu> {
}
