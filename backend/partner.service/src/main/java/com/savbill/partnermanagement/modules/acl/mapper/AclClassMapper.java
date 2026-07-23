package com.savbill.partnermanagement.modules.acl.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.acl.domain.AclClass;
import com.savbill.partnermanagement.modules.acl.model.AclClassDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
