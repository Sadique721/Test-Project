package com.savbill.integrationsystem.deviceveri.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.mapper.IBaseMapper;
import com.savbill.integrationsystem.deviceveri.domain.CustomerPackageRelData;
import com.savbill.integrationsystem.deviceveri.model.CustomerPackageRelDTO;

@Mapper
public abstract class CustomerPackageRelMapper implements IBaseMapper<CustomerPackageRelDTO, CustomerPackageRelData> {

    @Override
    public abstract CustomerPackageRelData dtoToDomain(CustomerPackageRelDTO pojo, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerPackageRelDTO domainToDTO(CustomerPackageRelData domain, @Context CycleAvoidingMappingContext context);


}
