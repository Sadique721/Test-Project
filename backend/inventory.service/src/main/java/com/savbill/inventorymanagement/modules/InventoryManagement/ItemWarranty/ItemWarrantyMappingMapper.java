package com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemWarrantyMappingMapper implements IBaseMapper<ItemWarrantyMappingDto, ItemWarrantyMapping> {
    @Override
    public abstract ItemWarrantyMapping dtoToDomain(ItemWarrantyMappingDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemWarrantyMappingDto domainToDTO(ItemWarrantyMapping domain, @Context CycleAvoidingMappingContext context);

}
