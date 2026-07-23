package com.savbill.taskmanagement.core.modules.acl.mapper;



import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.acl.domain.CustomACLEntry;
import com.savbill.taskmanagement.core.modules.acl.model.CustomACLEntryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
