package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.Vendor;
import com.savbill.integrationsystem.rms.model.VendorDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class VendorMapper implements IBaseMapper<VendorDto,Vendor> {

    @Override
    public abstract VendorDto domainToDTO(Vendor vendor, CycleAvoidingMappingContext context);

    @Override
    public abstract Vendor dtoToDomain(VendorDto dtoData, CycleAvoidingMappingContext context);
    @Override
    public List<VendorDto> domainToDTO(List<Vendor> vendors, CycleAvoidingMappingContext context){
        return null;
    }
    @Override
    public Vendor updateDTOToDomain(VendorDto vendorDto, Vendor vendor, CycleAvoidingMappingContext context){
        return null;
    }
}
