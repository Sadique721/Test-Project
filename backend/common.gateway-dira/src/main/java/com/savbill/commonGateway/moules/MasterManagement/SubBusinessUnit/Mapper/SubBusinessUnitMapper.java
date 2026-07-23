package com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Mapper;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Domain.SubBusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.SubBusinessUnit.Model.SubBusinessUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class SubBusinessUnitMapper implements IBaseMapper<SubBusinessUnitDTO, SubBusinessUnit> {

    String MODULE = " [SubBusinessUnitMapper] ";

    @Override
    @Mappings({@Mapping(target = "displayId", source = "data.id"),
            @Mapping(target = "displayName", source = "data.subbuname")})

//    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
    public abstract SubBusinessUnitDTO domainToDTO(SubBusinessUnit data, CycleAvoidingMappingContext context);
}
