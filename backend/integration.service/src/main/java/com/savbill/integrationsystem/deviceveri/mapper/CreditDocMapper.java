package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CreditDocData;
import com.savbill.integrationsystem.deviceveri.model.CreditDocDTO;

@Mapper
public abstract class CreditDocMapper implements IBaseMapper<CreditDocDTO, CreditDocData> {

    @Override
    public abstract CreditDocData dtoToDomain(CreditDocDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CreditDocDTO domainToDTO(CreditDocData domain, @Context CycleAvoidingMappingContext context);


}
