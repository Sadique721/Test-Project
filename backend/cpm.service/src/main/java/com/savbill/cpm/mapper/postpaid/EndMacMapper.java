package com.savbill.cpm.mapper.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.EndMacMappping;
import com.savbill.cpm.model.postpaid.EndMacMapppingPojo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class EndMacMapper implements IBaseMapper<EndMacMapppingPojo, EndMacMappping> {

    @Override
    public abstract EndMacMappping dtoToDomain(EndMacMapppingPojo pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract EndMacMapppingPojo domainToDTO(EndMacMappping domain, @Context CycleAvoidingMappingContext context);

}
