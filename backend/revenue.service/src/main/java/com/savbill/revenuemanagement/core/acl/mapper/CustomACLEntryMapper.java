package com.savbill.revenuemanagement.core.acl.mapper;


import com.savbill.revenuemanagement.core.acl.domain.CustomACLEntry;
import com.savbill.revenuemanagement.core.acl.model.CustomACLEntryDTO;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;

import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
