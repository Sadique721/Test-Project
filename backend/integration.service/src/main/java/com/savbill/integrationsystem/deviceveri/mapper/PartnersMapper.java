package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.PartnersData;
import com.savbill.integrationsystem.deviceveri.model.PartnersDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class PartnersMapper implements IBaseMapper<PartnersDTO, PartnersData> {

    @Override
    public abstract PartnersData dtoToDomain(PartnersDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PartnersDTO domainToDTO(PartnersData domain, @Context CycleAvoidingMappingContext context);


}
