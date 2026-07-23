package com.savbill.cpm.modules.fieldMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class FieldsMapper implements IBaseMapper<FieldsDTO,Fields>{

    public abstract FieldsDTO domainToDTO(Fields data, @Context CycleAvoidingMappingContext context);

    public abstract Fields dtoToDomain(FieldsDTO dtoData, @Context CycleAvoidingMappingContext context);
}
