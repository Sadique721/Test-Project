package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.PincodeData;
import com.savbill.integrationsystem.deviceveri.model.PincodeDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class PincodeMapper implements IBaseMapper<PincodeDTO, PincodeData> {

    @Override
    public abstract PincodeData dtoToDomain(PincodeDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PincodeDTO domainToDTO(PincodeData domain, @Context CycleAvoidingMappingContext context);


}
