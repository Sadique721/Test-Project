package com.savbill.partnermanagement.modules.PlanGroup.mapper;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroup;
import com.savbill.partnermanagement.modules.PlanGroup.dto.PlanGroupDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract interface PlangroupMapper extends IBaseMapper<PlanGroupDTO, PlanGroup> {
//
//    public abstract PlanGroupDTO domainTODTO(PlanGroup planGroup, @Context CycleAvoidingMappingContext context);
//
//    public abstract PlanGroup dtoToDomain(PlanGroupDTO dtoData, @Context CycleAvoidingMappingContext context);
}
