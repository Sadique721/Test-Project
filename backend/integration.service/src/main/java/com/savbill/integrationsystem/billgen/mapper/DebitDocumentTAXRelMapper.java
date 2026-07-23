package com.savbill.integrationsystem.billgen.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.savbill.integrationsystem.billgen.entity.DebitDocumentTAXRel;
import com.savbill.integrationsystem.billgen.model.DebitDocumentTAXRelDTO;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;

@Mapper
@Component
public abstract class DebitDocumentTAXRelMapper implements IBaseMapper<DebitDocumentTAXRelDTO, DebitDocumentTAXRel> {

    @Override
    public abstract DebitDocumentTAXRelDTO domainToDTO(DebitDocumentTAXRel data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract DebitDocumentTAXRel dtoToDomain(DebitDocumentTAXRelDTO dtoData, @Context CycleAvoidingMappingContext context);

}
