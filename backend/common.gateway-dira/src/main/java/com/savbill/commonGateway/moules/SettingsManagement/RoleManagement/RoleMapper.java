package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

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
