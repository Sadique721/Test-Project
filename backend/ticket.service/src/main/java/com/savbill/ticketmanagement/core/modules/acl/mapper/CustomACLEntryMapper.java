package com.savbill.ticketmanagement.core.modules.acl.mapper;



import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.acl.domain.CustomACLEntry;
import com.savbill.ticketmanagement.core.modules.acl.model.CustomACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
