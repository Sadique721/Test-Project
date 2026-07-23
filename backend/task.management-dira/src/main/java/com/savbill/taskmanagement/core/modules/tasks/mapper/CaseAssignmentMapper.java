package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseAssignment;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseAssignmentDTO;
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
