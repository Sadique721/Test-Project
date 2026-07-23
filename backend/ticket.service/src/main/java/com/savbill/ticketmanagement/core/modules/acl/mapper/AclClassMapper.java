package com.savbill.ticketmanagement.core.modules.acl.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.acl.domain.AclClass;
import com.savbill.ticketmanagement.core.modules.acl.model.AclClassDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
