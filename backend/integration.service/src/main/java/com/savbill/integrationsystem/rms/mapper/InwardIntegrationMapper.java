package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.Inward;
import com.savbill.integrationsystem.rms.model.InwardDto;
import org.mapstruct.Mapper;

@Mapper
public abstract class InwardIntegrationMapper implements IBaseMapper<InwardDto, Inward> {
    @Override
    public abstract InwardDto domainToDTO(Inward inward, CycleAvoidingMappingContext context) ;

    @Override
    public abstract Inward dtoToDomain(InwardDto dtoData, CycleAvoidingMappingContext context) ;

}
