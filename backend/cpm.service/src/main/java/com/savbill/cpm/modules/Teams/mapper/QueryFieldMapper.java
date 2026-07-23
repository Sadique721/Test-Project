package com.savbill.cpm.modules.Teams.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Teams.domain.QueryFieldMapping;
import com.savbill.cpm.modules.Teams.model.QueryFieldDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class QueryFieldMapper implements IBaseMapper<QueryFieldDTO, QueryFieldMapping> {


//    @Mapping(source = "teamHierarchyMapping.teamId", target = "teamId")
//    public abstract QueryFieldDTO domainToDTO(QueryFieldMapping data, @Context CycleAvoidingMappingContext context);
//
//
//    @Mapping(source = "teamId", target = "teamHierarchyMapping.teamId")
//    public abstract QueryFieldMapping dtoToDomain(QueryFieldDTO dtoData, @Context CycleAvoidingMappingContext context);


}
