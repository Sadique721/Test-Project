package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.AreaData;
import com.savbill.integrationsystem.deviceveri.model.AreaDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class AreaMapper implements IBaseMapper<AreaDTO, AreaData> {

    @Override
    public abstract AreaData dtoToDomain(AreaDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract AreaDTO domainToDTO(AreaData domain, @Context CycleAvoidingMappingContext context);


}
