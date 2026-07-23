package com.savbill.cpm.modules.fieldMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class ScreenFieldMappingMapper implements IBaseMapper<ScreenFieldMappingDto,ScreenFieldMapping> {

    public abstract ScreenFieldMappingDto domainToDTO(ScreenFieldMapping data, @Context CycleAvoidingMappingContext context);

    public abstract ScreenFieldMapping dtoToDomain(ScreenFieldMappingDto dtoData, @Context CycleAvoidingMappingContext context);
}
