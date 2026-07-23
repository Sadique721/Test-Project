package com.savbill.integrationsystem.billgen.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.savbill.integrationsystem.billgen.entity.DebitDocument;
import com.savbill.integrationsystem.billgen.model.DebitDocumentDTO;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;

@Mapper
@Component
public abstract class DebitDocumentMapper implements IBaseMapper<DebitDocumentDTO, DebitDocument> {

    @Override
    public abstract DebitDocumentDTO domainToDTO(DebitDocument data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract DebitDocument dtoToDomain(DebitDocumentDTO dtoData, @Context CycleAvoidingMappingContext context);

}
