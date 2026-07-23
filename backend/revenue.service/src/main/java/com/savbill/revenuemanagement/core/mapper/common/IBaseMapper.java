package com.savbill.revenuemanagement.core.mapper.common;

import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.BankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.model.BankManagementDTO;
import org.mapstruct.Context;

import java.util.List;

public interface IBaseMapper<DTO, DATA> {
//    Object domainToDTO(DATA data, @Context CycleAvoidingMappingContext context);

    DTO domainToDTO(DATA data, @Context CycleAvoidingMappingContext context);
    DATA dtoToDomain(DTO dtoData, @Context CycleAvoidingMappingContext context);

    List<BankManagementDTO> domainToDTO(List<BankManagement> bankManagementList, @Context CycleAvoidingMappingContext context);
//    List<DTO> domainToDTO(List<DATA> data, @Context CycleAvoidingMappingContext context);

//    @Mappings({
//            @Mapping(target = "mvnoId", ignore = true)
//    })
//    DATA updateDTOToDomain(DTO dto, @MappingTarget DATA data, @Context CycleAvoidingMappingContext context);
}

