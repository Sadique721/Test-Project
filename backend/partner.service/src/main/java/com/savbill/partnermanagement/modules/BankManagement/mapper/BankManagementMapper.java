package com.savbill.partnermanagement.modules.BankManagement.mapper;

import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.BankManagement.domain.BankManagement;
import com.savbill.partnermanagement.modules.BankManagement.model.BankManagementDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BankManagementMapper extends IBaseMapper<BankManagementDTO, BankManagement> {

    @Override
//    @Mapping(target = "displayId", source = "bankManagement.id")
//    @Mapping(target = "displayName", source = "bankManagement.bankname")
    BankManagementDTO domainToDTO(BankManagement bankManagement, CycleAvoidingMappingContext context);

}
