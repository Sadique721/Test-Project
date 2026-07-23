package com.savbill.partnermanagement.modules.acl.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.acl.domain.CustomACLEntry;
import com.savbill.partnermanagement.modules.acl.model.CustomACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
