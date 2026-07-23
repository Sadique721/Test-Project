package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.pojo.api.PlanGroupMappingDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class PlanGroupMappingMapper implements IBaseMapper<PlanGroupMappingDTO,PlanGroupMapping> {

    public abstract PlanGroupMappingDTO domainToDTO(PlanGroupMapping data , @Context CycleAvoidingMappingContext context);


}
