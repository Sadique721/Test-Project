package com.savbill.integrationsystem.billgen.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.savbill.integrationsystem.billgen.entity.CreditDocumentData;
import com.savbill.integrationsystem.billgen.model.CreditDocumentDTO;
import com.savbill.integrationsystem.billgen.repository.ServiceAreaInRepo;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;

@Mapper
@Component
public abstract class CreditDocumentMapper implements IBaseMapper<CreditDocumentDTO, CreditDocumentData> {

    @Autowired
    ServiceAreaInRepo serviceAreaInRepo;

    @Override
    public abstract CreditDocumentDTO domainToDTO(CreditDocumentData data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CreditDocumentData dtoToDomain(CreditDocumentDTO dtoData, @Context CycleAvoidingMappingContext context);

}
