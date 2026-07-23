package com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement;

import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class VendorMapper implements IBaseMapper<VendorDto,Vendor> {
//    public abstract VendorDto domainToDTO(Vendor data, @Context CycleAvoidingMappingContext context);
//
//    public abstract Vendor dtoToDomain(VendorDto dtoData, @Context CycleAvoidingMappingContext context);
}
