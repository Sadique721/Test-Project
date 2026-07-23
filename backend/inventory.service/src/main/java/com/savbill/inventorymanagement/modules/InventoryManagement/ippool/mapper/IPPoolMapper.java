package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPPool;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPPoolDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface IPPoolMapper extends IBaseMapper<IPPoolDTO, IPPool> {
    @Override
    @Mappings({
            @Mapping(target = "displayId", source = "poolId"),
            @Mapping(target = "displayPoolName", source = "poolName")
    })
    public abstract IPPoolDTO domainToDTO(IPPool domain, @Context CycleAvoidingMappingContext context);
}
