package com.savbill.ticketmanagement.core.modules.tickets.mapper;

import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseUpdate;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseUpdateDTO;
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
