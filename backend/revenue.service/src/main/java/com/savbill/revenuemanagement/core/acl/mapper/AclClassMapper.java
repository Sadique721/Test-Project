package com.savbill.revenuemanagement.core.acl.mapper;

import com.savbill.revenuemanagement.core.acl.domain.AclClass;
import com.savbill.revenuemanagement.core.acl.model.AclClassDTO;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
