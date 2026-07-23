package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemMapper implements IBaseMapper<ItemDto, Item> {
    @Override
    public abstract Item dtoToDomain(ItemDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemDto domainToDTO(Item domain, @Context CycleAvoidingMappingContext context);

}
