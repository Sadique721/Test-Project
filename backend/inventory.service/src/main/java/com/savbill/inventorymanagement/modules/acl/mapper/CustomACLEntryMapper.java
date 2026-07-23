package com.savbill.inventorymanagement.modules.acl.mapper;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.acl.domain.CustomACLEntry;
import com.savbill.inventorymanagement.modules.acl.model.CustomACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
