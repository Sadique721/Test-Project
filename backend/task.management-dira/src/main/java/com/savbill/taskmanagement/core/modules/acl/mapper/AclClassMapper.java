package com.savbill.taskmanagement.core.modules.acl.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.acl.domain.AclClass;
import com.savbill.taskmanagement.core.modules.acl.model.AclClassDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
