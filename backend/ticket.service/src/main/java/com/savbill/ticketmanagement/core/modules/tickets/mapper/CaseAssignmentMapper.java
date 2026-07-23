package com.savbill.ticketmanagement.core.modules.tickets.mapper;


import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.tickets.domain.Case;
import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseAssignment;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseAssignmentDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {CaseMapper.class})
public abstract class CaseAssignmentMapper implements IBaseMapper<CaseAssignmentDTO, CaseAssignment> {

    @Override
    @Mappings({@Mapping(target = "staffUser", source = "dto.staffUserId"),
            @Mapping(target = "cases", source = "dto.casesId")})
    public abstract CaseAssignment dtoToDomain(CaseAssignmentDTO dto, @Context CycleAvoidingMappingContext context);

    @Override
    @Mappings({@Mapping(source = "domain.staffUser", target = "staffUserId"),
            @Mapping(source = "domain.cases", target = "casesId")})

    public abstract CaseAssignmentDTO domainToDTO(CaseAssignment domain, @Context CycleAvoidingMappingContext context);

    Long fromCaseToId(Case entity) {
        return entity == null ? null : entity.getCaseId();
    }

    Case fromIdToCase(Long entityId) {
        if (entityId == null) {
            return null;
        }
        final Case aCase = new Case();
        aCase.setCaseId(entityId);
        return aCase;
    }

}
