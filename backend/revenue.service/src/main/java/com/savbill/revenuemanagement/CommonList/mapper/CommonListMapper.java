package com.savbill.revenuemanagement.CommonList.mapper;


import com.savbill.revenuemanagement.CommonList.domain.CommonList;
import com.savbill.revenuemanagement.CommonList.model.CommonListDTO;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class CommonListMapper implements IBaseMapper<CommonListDTO, CommonList> {
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "text")
    public abstract CommonListDTO domainToDTO(CommonList data, @Context CycleAvoidingMappingContext context);

    public abstract CommonList dtoToDomain(CommonListDTO dtoData, @Context CycleAvoidingMappingContext context);

}
