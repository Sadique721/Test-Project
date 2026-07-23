package com.savbill.integrationsystem.billgen.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.model.CustomerDTO;
import com.savbill.integrationsystem.billgen.repository.ServiceAreaInRepo;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;

@Mapper
@Component
public abstract class CustomerMapper implements IBaseMapper<CustomerDTO, CustomerData> {

    @Autowired
    ServiceAreaInRepo serviceAreaInRepo;

    @Override
    public abstract CustomerDTO domainToDTO(CustomerData data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerData dtoToDomain(CustomerDTO dtoData, @Context CycleAvoidingMappingContext context);

}
