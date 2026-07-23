package com.savbill.cpm.modules.tickets.mapper;
//
//import org.mapstruct.Context;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
//import com.savbill.cpm.core.mapper.IBaseMapper;
//import com.savbill.cpm.modules.tickets.domain.Case;
//import com.savbill.cpm.modules.tickets.domain.CaseAssignment;
//import com.savbill.cpm.modules.tickets.model.CaseAssignmentDTO;
//
//@Mapper(uses = {CaseMapper.class})
public abstract class CaseAssignmentMapper {
//
//    @Override
//    @Mapping(target = "staffUser", source = "dto.staffUserId")
//    @Mapping(target = "cases", source = "dto.casesId")
//    public abstract CaseAssignment dtoToDomain(CaseAssignmentDTO dto, @Context CycleAvoidingMappingContext context);
//
//    @Override
//    @Mapping(source = "domain.staffUser", target = "staffUserId")
//    @Mapping(source = "domain.cases", target = "casesId")
//    public abstract CaseAssignmentDTO domainToDTO(CaseAssignment domain, @Context CycleAvoidingMappingContext context);
//
//    Long fromCaseToId(Case entity) {
//        return entity == null ? null : entity.getCaseId();
//    }
//
//    Case fromIdToCase(Long entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        final Case aCase = new Case();
//        aCase.setCaseId(entityId);
//        return aCase;
//    }
//
}
