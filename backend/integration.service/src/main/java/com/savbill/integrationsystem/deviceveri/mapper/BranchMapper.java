package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.BranchData;
import com.savbill.integrationsystem.deviceveri.model.BranchDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class BranchMapper implements IBaseMapper<BranchDTO, BranchData> {

    @Override
    public abstract BranchData dtoToDomain(BranchDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract BranchDTO domainToDTO(BranchData domain, @Context CycleAvoidingMappingContext context);


}
