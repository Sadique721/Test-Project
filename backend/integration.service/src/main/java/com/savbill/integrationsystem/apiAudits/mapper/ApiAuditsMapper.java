package com.savbill.integrationsystem.apiAudits.mapper;

import com.savbill.integrationsystem.apiAudits.entity.ApiAudits;
import com.savbill.integrationsystem.apiAudits.model.ApiAuditsDTO;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public abstract class ApiAuditsMapper implements IBaseMapper<ApiAuditsDTO, ApiAudits> {

    @Override
    public abstract ApiAuditsDTO domainToDTO(ApiAudits apiAudits, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract ApiAudits dtoToDomain(ApiAuditsDTO apiAuditsDTO, @Context CycleAvoidingMappingContext context);
}
