package com.savbill.integrationsystem.deviceveri.mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CustomerLedgerData;
import com.savbill.integrationsystem.deviceveri.model.CustomerLedgerDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class CustomerLedgerMapper implements IBaseMapper<CustomerLedgerDTO, CustomerLedgerData> {

    @Override
    public abstract CustomerLedgerData dtoToDomain(CustomerLedgerDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerLedgerDTO domainToDTO(CustomerLedgerData domain, @Context CycleAvoidingMappingContext context);


}
