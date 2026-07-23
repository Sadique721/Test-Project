package com.savbill.revenuemanagement.core.mapper.customer;

import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustPlanMapppingMapper implements IBaseMapper<CustPlanMapppingDto, CustPlanMappping> {
    public abstract CustPlanMapppingDto domainToDTO(CustPlanMappping data, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract CustPlanMappping dtoToDomain(CustPlanMapppingDto dtoData, @Context CycleAvoidingMappingContext context);
}
