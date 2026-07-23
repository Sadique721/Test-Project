package com.savbill.commonGateway.moules.MasterManagement.BankManagement.mapper;


import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.domain.BankManagement;
import com.savbill.commonGateway.moules.MasterManagement.BankManagement.model.BankManagementDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface BankManagementMapper extends IBaseMapper<BankManagementDTO, BankManagement> {

    @Override
    @Mappings({
            @Mapping(target = "displayId", source = "bankManagement.id"),
            @Mapping(target = "displayName", source = "bankManagement.bankname")

    })

    BankManagementDTO domainToDTO(BankManagement bankManagement, CycleAvoidingMappingContext context);

    @Override
    List<BankManagementDTO> domainToDTO(List<BankManagement> bankManagementList, @Context CycleAvoidingMappingContext context);

}
