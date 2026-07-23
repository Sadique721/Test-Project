package com.savbill.cpm.modules.InventoryManagement.itemConditionMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract  class ItemConditionsMappingMapper implements IBaseMapper<ItemConditionsMappingDto, ItemConditionsMapping> {
    @Override
    public abstract ItemConditionsMapping dtoToDomain(ItemConditionsMappingDto dto, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ItemConditionsMappingDto domainToDTO(ItemConditionsMapping domain, @Context CycleAvoidingMappingContext context);

}
