package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.DebitDocumentData;
import com.savbill.integrationsystem.deviceveri.model.DebitDocDTO;

@Mapper
public abstract class DebitDocMapper implements IBaseMapper<DebitDocDTO, DebitDocumentData> {

    @Override
    public abstract DebitDocumentData dtoToDomain(DebitDocDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract DebitDocDTO domainToDTO(DebitDocumentData domain, @Context CycleAvoidingMappingContext context);


}
