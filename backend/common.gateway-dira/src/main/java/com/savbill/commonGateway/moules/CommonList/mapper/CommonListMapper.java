package com.savbill.commonGateway.moules.CommonList.mapper;


import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.CommonList.domain.CommonList;
import com.savbill.commonGateway.moules.CommonList.model.CommonListDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public abstract class CommonListMapper implements IBaseMapper<CommonListDTO, CommonList> {
    @Mappings({
        @Mapping(target = "displayId", source = "id"),
        @Mapping(target = "displayName", source = "text")
    })
    public abstract CommonListDTO domainToDTO(CommonList data, @Context CycleAvoidingMappingContext context);

    public abstract CommonList dtoToDomain(CommonListDTO dtoData, @Context CycleAvoidingMappingContext context);

}
