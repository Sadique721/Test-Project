package com.savbill.revenuemanagement.productmanagement.PlanGroup.mapper;


import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.dto.PlanGroupDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract interface PlangroupMapper extends IBaseMapper<PlanGroupDTO, PlanGroup> {
//
//    public abstract PlanGroupDTO domainTODTO(PlanGroup planGroup, @Context CycleAvoidingMappingContext context);
//
//    public abstract PlanGroup dtoToDomain(PlanGroupDTO dtoData, @Context CycleAvoidingMappingContext context);
}
