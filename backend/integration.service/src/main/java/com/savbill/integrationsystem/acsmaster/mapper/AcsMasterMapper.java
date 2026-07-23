package com.savbill.integrationsystem.acsmaster.mapper;

import com.savbill.integrationsystem.acsmaster.entity.AcsMaster;
import com.savbill.integrationsystem.acsmaster.model.AcsMasterDTO;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class AcsMasterMapper implements IBaseMapper<AcsMasterDTO, AcsMaster> {

    @Override
    public abstract AcsMaster dtoToDomain(AcsMasterDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract AcsMasterDTO domainToDTO(AcsMaster domain, @Context CycleAvoidingMappingContext context);


}
