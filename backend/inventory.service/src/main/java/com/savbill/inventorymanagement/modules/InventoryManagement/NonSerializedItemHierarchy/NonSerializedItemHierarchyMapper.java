package com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy;


import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class NonSerializedItemHierarchyMapper implements IBaseMapper<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy> {
    @Override
    public abstract NonSerializedItemHierarchy dtoToDomain(NonSerializedItemHierarchyDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NonSerializedItemHierarchyDto domainToDTO(NonSerializedItemHierarchy domain, @Context CycleAvoidingMappingContext context);
}
