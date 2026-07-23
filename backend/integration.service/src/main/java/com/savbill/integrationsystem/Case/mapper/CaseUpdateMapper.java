package com.savbill.integrationsystem.Case.mapper;

import com.savbill.integrationsystem.Case.CaseUpdate;
import com.savbill.integrationsystem.Case.CaseUpdateDTO;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseUpdateMapper implements IBaseMapper<CaseUpdateDTO, CaseUpdate> {

    @Override
    /*@Mapping(source = "caseUpdate.ticket", target = "ticketId")
    @Mapping(source = "caseUpdate.createdate", target = "createDateString")*/
    public abstract CaseUpdateDTO domainToDTO(CaseUpdate caseUpdate, CycleAvoidingMappingContext context);

    @Override
/*    @Mapping(source = "dtoData.ticketId", target = "ticket")*/
    public abstract CaseUpdate dtoToDomain(CaseUpdateDTO dtoData, CycleAvoidingMappingContext context);


}
