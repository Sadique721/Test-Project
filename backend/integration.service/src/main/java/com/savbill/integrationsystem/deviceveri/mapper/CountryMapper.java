package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CountryData;
import com.savbill.integrationsystem.deviceveri.model.CountryDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class CountryMapper implements IBaseMapper<CountryDTO, CountryData> {

    @Override
    public abstract CountryData dtoToDomain(CountryDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CountryDTO domainToDTO(CountryData domain, @Context CycleAvoidingMappingContext context);


}
