package com.savbill.integrationsystem.navmaster.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.navmaster.entity.NAVMaster;
import com.savbill.integrationsystem.navmaster.model.NAVMasterDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class NAVMasterMapper implements IBaseMapper<NAVMasterDTO, NAVMaster> {

//    @Mapping(source = "mvnoid")
    @Override
    public abstract NAVMasterDTO domainToDTO(NAVMaster navMaster, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract NAVMaster dtoToDomain(NAVMasterDTO dtoData, @Context CycleAvoidingMappingContext context);
}
