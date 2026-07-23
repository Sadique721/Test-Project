package com.savbill.cpm.model.postpaid;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.LocationMaster.domain.CustomerLocationMapping;
import com.savbill.cpm.pojo.api.CustomerLocationMappingDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public abstract class CustomerLocationMapper implements IBaseMapper<CustomerLocationMappingDto, CustomerLocationMapping> {
    @Override
    public abstract CustomerLocationMappingDto domainToDTO(CustomerLocationMapping data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustomerLocationMapping dtoToDomain(CustomerLocationMappingDto dtoData, @Context CycleAvoidingMappingContext context);



}
