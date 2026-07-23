package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.StateData;
import com.savbill.integrationsystem.deviceveri.model.StateDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class StateMapper implements IBaseMapper<StateDTO, StateData> {

    @Override
    public abstract StateData dtoToDomain(StateDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract StateDTO domainToDTO(StateData domain, @Context CycleAvoidingMappingContext context);


}
