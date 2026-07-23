package com.savbill.inventorymanagement.modules.acl.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.acl.domain.AclClass;
import com.savbill.inventorymanagement.modules.acl.model.AclClassDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
