package com.savbill.cpm.modules.BankManagement.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.BankManagement.domain.BankManagement;
import com.savbill.cpm.modules.BankManagement.model.BankManagementDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface BankManagementMapper extends IBaseMapper<BankManagementDTO , BankManagement> {

    @Override
    @Mapping(target = "displayId", source = "bankManagement.id")
    @Mapping(target = "displayName", source = "bankManagement.bankname")
    BankManagementDTO domainToDTO(BankManagement bankManagement, CycleAvoidingMappingContext context);

    @Override
    List<BankManagementDTO> domainToDTO(List<BankManagement> bankManagementList, @Context CycleAvoidingMappingContext context);

}
