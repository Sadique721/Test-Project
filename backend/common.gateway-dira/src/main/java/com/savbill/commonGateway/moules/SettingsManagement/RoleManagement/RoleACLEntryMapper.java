package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract interface RoleACLEntryMapper  extends IBaseMapper<RoleACLEntryDTO, RoleACLEntry> {

}
