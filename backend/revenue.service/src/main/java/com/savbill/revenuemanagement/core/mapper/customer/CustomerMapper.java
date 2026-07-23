package com.savbill.revenuemanagement.core.mapper.customer;

import com.savbill.revenuemanagement.core.dto.customer.CustomerDto;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustPlanMapppingMapper.class})
public abstract class CustomerMapper implements IBaseMapper<CustomerDto, Customers> {

    @Override
    public abstract CustomerDto domainToDTO(Customers data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract Customers dtoToDomain(CustomerDto dtoData, @Context CycleAvoidingMappingContext context);
}
