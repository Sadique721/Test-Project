package com.savbill.revenuemanagement.mastermanagement.BankManagement.mapper;

import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.domain.BankManagement;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.model.BankManagementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BankManagementMapper extends IBaseMapper<BankManagementDTO , BankManagement> {

    @Override
    @Mapping(target = "displayId", source = "bankManagement.id")
    @Mapping(target = "displayName", source = "bankManagement.bankname")
    BankManagementDTO domainToDTO(BankManagement bankManagement, CycleAvoidingMappingContext context);

}
