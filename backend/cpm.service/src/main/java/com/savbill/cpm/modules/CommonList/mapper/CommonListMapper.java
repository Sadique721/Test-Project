package com.savbill.cpm.modules.CommonList.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.CommonList.domain.CommonList;
import com.savbill.cpm.modules.CommonList.model.CommonListDTO;
import org.mapstruct.Mapping;

@Mapper
public abstract class CommonListMapper implements IBaseMapper<CommonListDTO, CommonList> {
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "text")
    public abstract CommonListDTO domainToDTO(CommonList data, @Context CycleAvoidingMappingContext context);

    public abstract CommonList dtoToDomain(CommonListDTO dtoData, @Context CycleAvoidingMappingContext context);

}
