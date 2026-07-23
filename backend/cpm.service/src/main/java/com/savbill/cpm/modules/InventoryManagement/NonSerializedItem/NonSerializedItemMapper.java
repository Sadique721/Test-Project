package com.savbill.cpm.modules.InventoryManagement.NonSerializedItem;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class NonSerializedItemMapper implements IBaseMapper<NonSerializedItemDto, NonSerializedItem> {
    @Override
    public abstract NonSerializedItem dtoToDomain(NonSerializedItemDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NonSerializedItemDto domainToDTO(NonSerializedItem domain, @Context CycleAvoidingMappingContext context);

}
