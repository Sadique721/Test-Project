package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.ServicesData;
import com.savbill.integrationsystem.deviceveri.model.ServicesDTO;

@Mapper
public abstract class ServicesMapper implements IBaseMapper<ServicesDTO, ServicesData> {

    @Override
    public abstract ServicesData dtoToDomain(ServicesDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ServicesDTO domainToDTO(ServicesData domain, @Context CycleAvoidingMappingContext context);


}
