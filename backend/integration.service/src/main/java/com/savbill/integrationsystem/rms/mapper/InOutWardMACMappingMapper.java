package com.savbill.integrationsystem.rms.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.rms.entity.InOutWardMACMapping;
import com.savbill.integrationsystem.rms.model.InOutWardMACMapingDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class InOutWardMACMappingMapper implements IBaseMapper<InOutWardMACMapingDTO, InOutWardMACMapping> {
    @Override
    public abstract InOutWardMACMapingDTO domainToDTO(InOutWardMACMapping inOutWardMACMapping, CycleAvoidingMappingContext context);

    @Override
    public abstract InOutWardMACMapping dtoToDomain(InOutWardMACMapingDTO dtoData, CycleAvoidingMappingContext context);

}
