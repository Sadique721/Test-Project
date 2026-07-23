package com.savbill.cpm.mapper.postpaid;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.postpaid.PlanGroup;
import com.savbill.cpm.pojo.api.PlanGroupDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract interface PlangroupMapper extends IBaseMapper<PlanGroupDTO, PlanGroup> {
//
//    public abstract PlanGroupDTO domainTODTO(PlanGroup planGroup, @Context CycleAvoidingMappingContext context);
//
//    public abstract PlanGroup dtoToDomain(PlanGroupDTO dtoData, @Context CycleAvoidingMappingContext context);
}
