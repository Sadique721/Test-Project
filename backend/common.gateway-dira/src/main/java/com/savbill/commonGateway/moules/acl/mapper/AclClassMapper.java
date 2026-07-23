package com.savbill.commonGateway.moules.acl.mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.acl.domain.AclClass;
import com.savbill.commonGateway.moules.acl.model.AclClassDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
