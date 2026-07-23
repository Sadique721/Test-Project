package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdateDetails;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseUpdateDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = CaseMapper.class)
public abstract class CaseUpdateDetailsMapper implements IBaseMapper<CaseUpdateDetailsDTO, CaseUpdateDetails> {

    @Override
    @Mapping(source = "domain.resolution", target = "resolutionId")
    public abstract CaseUpdateDetailsDTO domainToDTO(CaseUpdateDetails domain, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dto.resolutionId", target = "resolution")
    public abstract CaseUpdateDetails dtoToDomain(CaseUpdateDetailsDTO dto, CycleAvoidingMappingContext context);

}
