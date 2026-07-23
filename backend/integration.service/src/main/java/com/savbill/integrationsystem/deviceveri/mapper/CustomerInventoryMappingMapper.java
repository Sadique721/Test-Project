package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CustomerInventoryMappingData;
import com.savbill.integrationsystem.deviceveri.model.CustomerInventoryMappingDTO;

@Mapper
public abstract class CustomerInventoryMappingMapper implements IBaseMapper<CustomerInventoryMappingDTO, CustomerInventoryMappingData> {

    @Override
    public abstract CustomerInventoryMappingData dtoToDomain(CustomerInventoryMappingDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerInventoryMappingDTO domainToDTO(CustomerInventoryMappingData domain, @Context CycleAvoidingMappingContext context);


}
