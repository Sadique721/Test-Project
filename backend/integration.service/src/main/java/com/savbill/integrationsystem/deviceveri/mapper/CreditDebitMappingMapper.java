package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CreditDebitMappingData;
import com.savbill.integrationsystem.deviceveri.model.CreditDebitMappingDTO;

@Mapper
public abstract class CreditDebitMappingMapper implements IBaseMapper<CreditDebitMappingDTO, CreditDebitMappingData> {

    @Override
    public abstract CreditDebitMappingData dtoToDomain(CreditDebitMappingDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CreditDebitMappingDTO domainToDTO(CreditDebitMappingData domain, @Context CycleAvoidingMappingContext context);


}
