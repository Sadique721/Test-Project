package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CustomersData;
import com.savbill.integrationsystem.deviceveri.model.CustomersDTO;

@Mapper
public abstract class CustomersMapper implements IBaseMapper<CustomersDTO, CustomersData> {

    @Override
    public abstract CustomersData dtoToDomain(CustomersDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomersDTO domainToDTO(CustomersData domain, @Context CycleAvoidingMappingContext context);


}
