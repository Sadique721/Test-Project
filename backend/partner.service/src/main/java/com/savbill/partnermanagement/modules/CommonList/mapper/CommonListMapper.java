package com.savbill.partnermanagement.modules.CommonList.mapper;



import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.CommonList.domain.CommonList;
import com.savbill.partnermanagement.modules.CommonList.model.CommonListDTO;
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
