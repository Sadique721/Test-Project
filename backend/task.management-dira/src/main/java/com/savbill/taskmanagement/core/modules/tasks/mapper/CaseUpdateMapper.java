package com.savbill.taskmanagement.core.modules.tasks.mapper;

import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdate;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = CaseAssignmentMapper.class)
public abstract class CaseUpdateMapper implements IBaseMapper<CaseUpdateDTO, CaseUpdate> {

    @Override
    @Mappings({
            @Mapping(source = "caseUpdate.ticket", target = "ticketId"),
            @Mapping(source = "caseUpdate.createdate", target = "createDateString")
    })

    public abstract CaseUpdateDTO domainToDTO(CaseUpdate caseUpdate, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "dtoData.ticketId", target = "ticket")
    public abstract CaseUpdate dtoToDomain(CaseUpdateDTO dtoData, CycleAvoidingMappingContext context);


}
