package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CityData;
import com.savbill.integrationsystem.deviceveri.model.CityDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class CityMapper implements IBaseMapper<CityDTO, CityData> {

    @Override
    public abstract CityData dtoToDomain(CityDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CityDTO domainToDTO(CityData domain, @Context CycleAvoidingMappingContext context);


}
