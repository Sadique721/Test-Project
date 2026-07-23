package com.savbill.cpm.modules.acl.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.acl.domain.CustomACLEntry;
import com.savbill.cpm.modules.acl.model.CustomACLEntryDTO;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
