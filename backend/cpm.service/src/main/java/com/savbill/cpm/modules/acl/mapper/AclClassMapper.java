package com.savbill.cpm.modules.acl.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.acl.domain.AclClass;
import com.savbill.cpm.modules.acl.model.AclClassDTO;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
