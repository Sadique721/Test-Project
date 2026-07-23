package com.savbill.commonGateway.moules.acl.mapper;


import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.acl.domain.CustomACLEntry;
import com.savbill.commonGateway.moules.acl.model.CustomACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
