package com.savbill.commonGateway.moules.acl.mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.acl.domain.AclMenu;
import com.savbill.commonGateway.moules.acl.model.AclMenuDtoNew;
import org.mapstruct.Mapper;

@Mapper
public interface AclMenuMapper extends IBaseMapper<AclMenuDtoNew, AclMenu> {
}
