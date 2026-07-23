package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CustomerServiceMappingData;
import com.savbill.integrationsystem.deviceveri.model.CustomerServiceMappingDTO;

@Mapper
public abstract class CustomerServiceMappingMapper implements IBaseMapper<CustomerServiceMappingDTO, CustomerServiceMappingData> {

    @Override
    public abstract CustomerServiceMappingData dtoToDomain(CustomerServiceMappingDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerServiceMappingDTO domainToDTO(CustomerServiceMappingData domain, @Context CycleAvoidingMappingContext context);


}
