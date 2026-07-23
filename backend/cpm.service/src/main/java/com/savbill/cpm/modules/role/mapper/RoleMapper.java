package com.savbill.cpm.modules.role.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.role.domain.Role;
import com.savbill.cpm.modules.role.model.RoleDTO;

@Mapper
public abstract interface RoleMapper extends IBaseMapper<RoleDTO, Role> {

//    @Autowired
//    CustomACLEntryRepository customAclEntryRepository = null;
//    @Override
////    @Mapping(source = "role.aclEntry", target = "aclEntryPojoList")
//    public abstract RoleDTO domainToDTO(Role role, @Context CycleAvoidingMappingContext context);
//
//    @Override
//    @Mapping(source = "dtoData.aclEntryPojoList", target = "aclEntry")
////    @Mapping(source = "dtoData.aclEntryPojoList.roleId", target = "aclEntry")
//    public abstract Role dtoToDomain(RoleDTO dtoData, @Context CycleAvoidingMappingContext context);
}
