package com.savbill.ticketmanagement.core.modules.acl.mapper;

import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.acl.domain.AclMenu;
import com.savbill.ticketmanagement.core.modules.acl.model.AclMenuDtoNew;
import org.mapstruct.Mapper;

@Mapper
public interface AclMenuMapper extends IBaseMapper<AclMenuDtoNew, AclMenu> {
}
