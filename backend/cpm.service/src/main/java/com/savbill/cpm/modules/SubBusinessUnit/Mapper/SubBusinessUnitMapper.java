package com.savbill.cpm.modules.SubBusinessUnit.Mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.SubBusinessUnit.Domain.SubBusinessUnit;
import com.savbill.cpm.modules.SubBusinessUnit.Model.SubBusinessUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class SubBusinessUnitMapper implements IBaseMapper<SubBusinessUnitDTO, SubBusinessUnit> {

    String MODULE = " [SubBusinessUnitMapper] ";

    @Override
    @Mapping(target = "displayId", source = "data.id")
    @Mapping(target = "displayName", source = "data.subbuname")
//    @Mapping(source = "data.investmentCodeList", target = "investmentcode_id")
    public abstract SubBusinessUnitDTO domainToDTO(SubBusinessUnit data,CycleAvoidingMappingContext context);
}
