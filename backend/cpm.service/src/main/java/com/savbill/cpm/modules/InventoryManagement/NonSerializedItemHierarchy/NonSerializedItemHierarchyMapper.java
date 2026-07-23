package com.savbill.cpm.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class NonSerializedItemHierarchyMapper implements IBaseMapper<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy> {
    @Override
    public abstract NonSerializedItemHierarchy dtoToDomain(NonSerializedItemHierarchyDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NonSerializedItemHierarchyDto domainToDTO(NonSerializedItemHierarchy domain, @Context CycleAvoidingMappingContext context);
}
